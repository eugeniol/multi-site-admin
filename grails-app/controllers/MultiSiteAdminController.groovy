import grails.converters.JSON
import groovy.io.FileType
import org.apache.commons.io.FilenameUtils
import org.apache.commons.configuration.AbstractConfiguration
import org.apache.commons.configuration.PropertiesConfiguration
import org.apache.commons.lang.StringEscapeUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

import java.text.DateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created with IntelliJ IDEA.
 * User: elattanzio
 * Date: 23/07/14
 * Time: 16:45
 * To change this template use File | Settings | File Templates.
 */
class MultiSiteAdminController {
	String PROJECT_PATH = 'C:\\Users\\elattanzio\\Workspaces\\scenic3\\ema-site-app\\'

	def index = {
		[:]
	}

	def templatesInspector = {
		def emaIntl = new ProjectEntity(
				base: new File(getProjectPath(), '../ema-intl'),
				sitesManager: new SitesManager(projectPath)
		)

		return [project: emaIntl, allTranslations: emaIntl.sitesManager.allMessages]
	}

	def beforeInterceptor = {

		if (!projectPath.exists() && !(actionName in ['config', 'reset'])) {
			redirect(action: 'config')
		}
	}


	def siteParams = {
		return handleTableAction('siteParams')
	}

	def translationsBySite = {
		return handleTableAction('messages')
	}

	def translationsByLanguage = {
		def manager = new SitesManager(projectPath)
		def sites = filterSites('messages', manager.sites)
		Map<Locale, List<SiteEntity>> sitesByLocale = sites.groupBy { it.locale }
		def locales = sitesByLocale.keySet().findAll { it != null }

		if (request.post) {
			sitesByLocale.find { it.key.toString() == params.site }?.value.each { site ->
				site.messages.setProperty(params.key, params.value)
				manager.saveProperties(site.messages)
			}
			return render([ok: true] as JSON)
		}

		return [
				localesList: locales,
				sitesByLocale: sitesByLocale,
				allTranslations: manager.allMessages,
				table: 'messages'
		]
	}


	def duplicate = {
		performMulitOperation { prop, key, newKey ->
			if (prop.containsKey(key)) {
				def val = prop.getString(key)
				prop.addProperty(newKey, val)
				return true
			}
		}
	}

	def delete = {
		performMulitOperation { prop, key, newKey ->
			if (prop.containsKey(key)) {
				prop.clearProperty(key)
				return true
			}
		}
	}

	def rename = {
		performMulitOperation { prop, key, newKey ->
			if (prop.containsKey(key)) {
				def val = prop.getString(key)
				prop.clearProperty(key)
				prop.addProperty(newKey, val)
				return true
			}
		}
	}

	def config = {
		[defaultPath: defaultPath]
	}

	private def performMulitOperation(Closure body) {
		if (request.post) {
			def key = params.key,
			    newKey = params.newKey,
			    table = params.table

			def ctrl = new SitesManager(projectPath)

			def messages = filterSites(table, ctrl.sites).collect { table == 'messages' ? it.messages : it.siteParams }
			int c = 0
			messages.each { prop ->
				if (body(prop, key, newKey)) {
					ctrl.saveProperties(prop)
					c++
				}
			}

			return render(([counter: c] + params) as JSON)
		}
	}

	private String getDefaultPath() {
		def defaultPath = ''
		String applicationPath = request.getSession().getServletContext().getRealPath("")

		def workspace = new File(new File(applicationPath), '../../')
		def emaFile = new File(workspace, 'ema-site-app')

		if (emaFile.exists())
			defaultPath = emaFile.canonicalFile.absolutePath
		defaultPath
	}




	private def handleTableAction(String table) {
		def ctrl = new SitesManager(projectPath)
		if (request.post) {
			updateAndSave(ctrl, table, params.site, params.key, params.value)
			render([ok: true] as JSON)
		} else {
			return [
					allTranslations: filterProperties(ctrl.getProperty('all' + table.capitalize())),
					sites: filterSites(table, ctrl.sites),
					allSites: ctrl.sites,
					table: table
			]
		}
	}

	private List<SiteEntity> filterSites(String table, List<SiteEntity> values) {
		List filter = params.list('filter')
		(filter ? values.findAll { it.name in filter } : values).findAll { it.getProperty(table)?.file?.exists() }
	}


	private Map filterProperties(Map values) {
		List filter = params.list('filter')
		filter ? values.findAll { it.value.keySet().find { it.name in filter } } : values
	}

	private File getProjectPath() {
		String cookieVal = request.cookies.find { it.name == 'project_path' }?.value?.decodeURL()
		String project_path = ''

		if (cookieVal && new File(cookieVal).exists()) {
			project_path = cookieVal
		} else {
//			project_path =session.project_path
		}

		def file = new File(project_path)

		return file
	}

	private void updateAndSave(SitesManager ctrl, String table, String site, String key, String value) {

		def prop = ctrl.sites.find { it.name == site }?.getProperty(table)
		if (prop) {
			if (value)
				prop.setProperty(key, value)
			else
				prop.clearProperty(key)

			ctrl.saveProperties(prop)
		}
	}


	private Map<String, Locale> getLocales() {
		def locales = [:]
		DateFormat.availableLocales.each {
			locales[it.toString()] = it
		}
		return locales

	}


	Map getSiteParamsBySite() {
		Map allConfig = [:]
		sites.each { site ->
			def properties = getMessagesProperties(site, SITE_PARAMS_FILENAME)
			properties.keys.each {
				def key = properties.getString(it)

				if (!allConfig.containsKey(it))
					allConfig[it] = [:]

				if (!allConfig[it].containsKey(key))
					allConfig[it][key] = []

				allConfig[it][key] << site
			}
		}

		allConfig
	}


	def siteParamsByKey = {
		Map allConfig = [:]
		def ctrl = new SitesManager(projectPath)

		filterSites('siteParams', ctrl.sites).each { site ->
			def properties = site.siteParams

			properties.keys.each {
				def key = properties.getString(it)

				if (!allConfig.containsKey(it))
					allConfig[it] = [:]

				if (!allConfig[it].containsKey(key))
					allConfig[it][key] = []

				allConfig[it][key] << site
			}
		}

		[allConfig: allConfig]
	}


}


class SitesManager {
	static final String SITE_PARAMS = 'siteParams.properties'
	static final String TRANSLATIONS = 'messages.properties'


	private static final String I18N_PATH = 'grails-app/i18n/'
	private static final String SITEPARAMS_PATH = 'web-app/WEB-INF/sites/'
	private static final String SITE_PARAMS_FILENAME = 'siteParams.properties'
	private static final String I18N_FILENAME = 'messages.properties'
	private static final String ENCODING = "UTF-8"

	private List<SiteEntity> _sites
	private Map<String, Map<SiteEntity, String>> _allSiteParams
	private Map<String, Map<SiteEntity, String>> _allMessages

	File workspace

	Map<String, Locale> localesCache = [:]

	SitesManager(File workspace) {
		this.workspace = workspace
		assert workspace?.exists()
	}

	List<SiteEntity> getSites() {
		if (_sites == null) {
			_sites = []
			new File(workspace, SITEPARAMS_PATH).eachDir {
				_sites << new SiteEntity(name: it.name, manager: this)
			}
		}
		return _sites
	}

	File getPropertyFile(SiteEntity site, SitesManagerPropertiesType type) {
		switch (type) {
			case SitesManagerPropertiesType.SITE_PARAMS:
				return new File(workspace, SITEPARAMS_PATH + site.name + '/' + SITE_PARAMS_FILENAME)
			case SitesManagerPropertiesType.TRANSLATIONS:
				return new File(workspace, I18N_PATH + site.name + '/' + I18N_FILENAME)
		}
	}

	PropertiesConfiguration getPropertyConfig(SiteEntity site, SitesManagerPropertiesType type) {
		switch (type) {
			case SitesManagerPropertiesType.SITE_PARAMS:
				return site.siteParams
			case SitesManagerPropertiesType.TRANSLATIONS:
				return site.messages
		}
	}

	Map<String, Map<SiteEntity, String>> getAllSiteParams() {
		if (_allSiteParams == null) {
			_allSiteParams = loadAllParams(SitesManagerPropertiesType.SITE_PARAMS)
		}
		_allSiteParams
	}

	Map<String, Map<SiteEntity, String>> getAllMessages() {
		if (_allMessages == null) {
			_allMessages = loadAllParams(SitesManagerPropertiesType.TRANSLATIONS)
		}
		_allMessages
	}

	Map<String, Map<SiteEntity, String>> loadAllParams(SitesManagerPropertiesType type) {
		Map<String, Map<SiteEntity, String>> allTranslations = [:]
		sites.each { site ->
			def properties = getPropertyConfig(site, type)
			properties.keys.each {
				if (!allTranslations.containsKey(it)) allTranslations[it] = [:]
				allTranslations[it][site] = properties.getString(it)
			}
		}
		return allTranslations
	}

	PropertiesConfiguration loadProperties(File propertiesFile) {
		PropertiesConfiguration properties = new PropertiesConfiguration()
		properties.with {
			listDelimiter = AbstractConfiguration.DISABLED_DELIMITER
			file = propertiesFile
			encoding = ENCODING
			if (propertiesFile.exists()) {
				try {
					load(propertiesFile.newReader(ENCODING))
				}
				catch (Exception ex) {
					println ex
				}
			}
		}
		return properties
	}

	void saveProperties(PropertiesConfiguration messages) {
		ByteArrayOutputStream os = new ByteArrayOutputStream()
		messages.save(os)
		File f = messages.file
		OutputStreamWriter newFile = new OutputStreamWriter(new FileOutputStream(f), ENCODING);
		newFile.write(StringEscapeUtils.unescapeJava(os.toString(ENCODING)))
		newFile.close()
	}


}

enum SitesManagerPropertiesType {
	SITE_PARAMS, TRANSLATIONS
}

class SiteEntity {
	SitesManager manager
	String name

	private PropertiesConfiguration _messages
	private PropertiesConfiguration _siteParams

	static List LocalesWithCountry = ['es', 'pt', 'zh']

	private Locale _locale



	Locale getLocale() {
		if (_locale == null) {
			String languageCode = siteParams.getProperty('languageCode')
			def l
			if (languageCode in LocalesWithCountry)
				l = new Locale(languageCode, siteParams.getProperty('countryCode'))
			else if (languageCode)
				l = new Locale(languageCode)
			else
				l = new Locale('en')
			_locale = l

//			                                                 println l.toString()
//			if (manager.localesCache.containsKey(l.toString()))
//				_locale = manager.localesCache[l.toString()]
//			else
//				_locale = manager.localesCache[l.toString()] = l


		}
		return _locale
	}

	String getCountryCode() {
		return siteParams.getString('countryCode')?.toLowerCase()
	}

	PropertiesConfiguration getSiteParams() {
		if (_siteParams == null) {
			_siteParams = manager.loadProperties(manager.getPropertyFile(this, SitesManagerPropertiesType.SITE_PARAMS))
		}
		return _siteParams
	}

	PropertiesConfiguration getMessages() {
		if (_messages == null) {
			_messages = manager.loadProperties(manager.getPropertyFile(this, SitesManagerPropertiesType.TRANSLATIONS))
		}
		return _messages
	}

	@Override
	String toString() {
		return name
	}
}

abstract class TemplateEntity implements Comparable {
	ProjectEntity project
	List<TemplateEntity> parents = []
	SitesManager sitesManager

	abstract String getText()

	abstract String getName()

	abstract List<TemplateEntity> getChildrens()

	String[] getMessagesCalls() { [] }

	String[] getTranslations() {
		[]
	}

	List getWords() { [] }


	@Override
	String toString() {
		return name
	}

	@Override
	int compareTo(Object o) {
		if (o instanceof TemplateEntity) {
			name.compareTo(o.name)
		} else {
			0
		}
	}

}
class CodeTemplateEntity extends TemplateEntity {
	String name
	String text = ''

	@Override
	List<TemplateEntity> getChildrens() {
		[]
	}
}

class FileTemplateEntity extends TemplateEntity {
	File file

	private String _name

	@Override
	String getText() {
		file.exists() ? file.text : ''
	}


	FileTemplateEntity(ProjectEntity project, File file, SitesManager sitesManager = null) {
		this.project = project
		this.file = file
		this.sitesManager = sitesManager
	}

	String getName() {
		if (!_name) {
			if (file.exists()) {
				_name = (file.parent - project.viewsDir.path).replace('\\', '/') + '/' +
						FilenameUtils.getBaseName(file.name).replaceFirst(/^_/, '')
			} else {
				_name = file.name
			}
		}
		return _name
	}

	List<TemplateEntity> _childrens


	protected GspTemplateParser _parser

	protected GspTemplateParser getParser() {
		if (_parser == null) {
			_parser = new GspTemplateParser()
			file.withReader { r ->
				_parser.parse(r)
			}
		}
		_parser
	}

	protected Document _dom

	protected Document getDom() {
		if (_dom == null) {
			_dom = Jsoup.parse(parser.text);
		}
		return _dom
	}


	String[] getChildTemplatesNames() {
		Elements links = dom.select("[template]"); // a with href
		links.collect { it.attr('template') }
	}

	String[] _messagesCalls

	@Override
	String[] getMessagesCalls() {
		if (_messagesCalls == null) {
			def ret = []

			parser.expressions.each { exp ->
				exp.findAll(/(?m)\w+.translate\s*(.*?text\s*:\s*["']([\.\w]+)["'].*?)/) {
					ret << it[2]
				}
			}

			Elements links = dom.select("[text]"); // a with href
			links.each { ret << it.attr('text') }

			childrens.each {
				it.messagesCalls.each { ret << it }
			}

			_messagesCalls = ret*.trim()
		}

		return _messagesCalls
	}

	List<TemplateEntity> getChildrens() {
		if (_childrens == null) {
			List childs = childTemplatesNames

//			file.text.find(/(?m)\w+.render\s*(.*?template\s*:\s*["']([\/\w]+)["'].*?)/) {
//				childs << it[1]
//				println it
//			}

			_childrens = childs.collect { name ->
				project.templates.find { it.name == name } ?: new CodeTemplateEntity(
						project: this.project, name: name)
			}.sort()

			_childrens.each {
				it.parents << this
			}
		}
		return _childrens
	}

	List _words
	int _wordsCount = 0

	static final Pattern WORDS_MATCHER = ~/\b([\.\w]+)\b/

	List getWords() {
		if (_words == null) {
			_words = []
			def allMessages = sitesManager?.allMessages
			def matcher = WORDS_MATCHER.matcher(text)

			while (matcher.find()) {
				_wordsCount++;

				def t = matcher.group()
				if (allMessages?.containsKey(t)) {
					def tt = new TranslationInTemplate(this, matcher, t)
					_words << tt
				}
			}
		}

		return childrens.collect { it.words }.flatten() + _words
	}

	@Override
	String[] getTranslations() {
		def ret = []

		return words.findAll { sitesManager.allMessages.containsKey(it) }

//		return ret.unique()
	}
}

class TranslationEntity {

	String key
	Map<SiteEntity, String> values = [:]


	@Override
	public java.lang.String toString() {
		return key
	}
}
class TranslationManager implements Map<String, TranslationEntity> {

	Map<String, TranslationEntity> translation = []

	@Override
	int size() {
		translation.size()
	}

	@Override
	boolean isEmpty() {
		translation.isEmpty()
	}

	@Override
	boolean containsKey(Object key) {
		translation.contains(key)

	}

	@Override
	boolean containsValue(Object value) {
		translation.containsValue(value)
	}

	@Override
	TranslationEntity get(Object key) {
		translation.get(key)
	}

	@Override
	TranslationEntity put(String key, TranslationEntity value) {
		translation.get(key, value)

	}

	@Override
	TranslationEntity remove(Object key) {
		translation.remove(key)
	}

	@Override
	void putAll(Map m) {
		translation.putAll(m)
	}

	@Override
	void clear() {
		translation.clear()
	}

	@Override
	Set keySet() {
		translation.keySet()
	}

	@Override
	Collection values() {
		translation.values()
	}

	@Override
	Set<Map.Entry> entrySet() {
		translation.entrySet()
	}

	TranslationManager(SitesManager site) {
		site.allMessages.each { key, sites ->
			def tr = new TranslationEntity()
			tr.key = key
			tr.values = sites.clone()
			translation << tr
		}
	}
}




class ProjectEntity {
	File base
	SitesManager sitesManager


	File getViewsDir() {
		new File(base, 'grails-app/views')
	}

	private List<TemplateEntity> _templates
	private List<TemplateEntity> _topLevelTemplates

	List<TemplateEntity> getTopLevelTemplates() {
		if (_topLevelTemplates == null) {
			_topLevelTemplates = templates.findAll { it.parents.size() == 0 }
		}
		return _topLevelTemplates
	}

	List<TemplateEntity> getTemplates() {
		if (_templates == null) {
			_templates = []
			viewsDir.eachFileRecurse(FileType.FILES) { file ->
				if (FilenameUtils.getExtension(file.name).toLowerCase() == 'gsp')
					_templates << new FileTemplateEntity(this, file, sitesManager)
			}

			_templates*.getChildrens()
		}
		return _templates
	}

}


class TranslationInTemplate {
	TemplateEntity template
	int start
	int end
	String text

	TranslationInTemplate(TemplateEntity tmpl, Matcher m, String t) {
		start = m.start()
		end = m.end()
		text = t
		template = tmpl
	}


	@Override
	public java.lang.String toString() {
		return "TranslationInTemplate{" +
				"template=" + template +
				", start=" + start +
				", end=" + end +
				", text='" + text + '\'' +
				'}';
	}
}