import grails.converters.JSON
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.apache.commons.configuration.AbstractConfiguration
import org.apache.commons.configuration.PropertiesConfiguration
import org.apache.commons.lang.StringEscapeUtils
import java.text.DateFormat

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

	}


	def beforeInterceptor = {
		if (!session.project_path && !(actionName in ['config', 'reset'])) {
//			println "cookie not set"
//			session.project_path = PROJECT_PATH
//			redirect(action: 'translationsByLanguage')
//			return
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
		def manager = new SitesManager(resolvePath())
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
		if (request.post) {
			session.project_path = params.project_path
			redirect action: 'index'
		}
		[defaultPath: defaultPath]
	}

	def reset = {
		session.invalidate()
		redirect action: 'config'
	}

	private def performMulitOperation(Closure body) {
		if (request.post) {
			def key = params.key,
			    newKey = params.newKey,
			    table = params.table

			def ctrl = new SitesManager(resolvePath())

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
		def ctrl = new SitesManager(resolvePath())
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

	private File resolvePath() {
		assert session.project_path
		def file = new File(session.project_path)
		assert file.exists()

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
		def ctrl = new SitesManager(resolvePath())

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

	List<String> getSteNames() {
		return sites.keySet().sort()
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