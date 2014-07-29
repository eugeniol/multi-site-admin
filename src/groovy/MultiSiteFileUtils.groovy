import grails.converters.JSON
import org.apache.commons.configuration.PropertiesConfiguration
import org.apache.commons.lang.StringEscapeUtils

import java.text.DateFormat

class MultiSiteFileUtils {
	static final String SITE_PARAMS = 'siteparams'
	static final String TRANSLATIONS = 'translations'

	private String projectPath = ''
	private Map<String, PropertiesConfiguration> _messagesBySite = [:]

	private static final String SITE_PARAMS_FILENAME = 'siteParams.properties'
	private static final String I18N_PATH = 'grails-app/i18n/'
	private static final String SITE_PARAMS_PATH = 'web-app/WEB-INF/sites/'
	private static final String I18N_FILENAME = 'messages.properties'

	MultiSiteFileUtils(String path, String propertyFile) {
		propFile = propertyFile
		projectPath = path
	}

	protected String propFile = ''



	def translationsByLang = {
//		propFile = 'translations'
//		File dir = new File(_path(dirName))
//		getMultiSitesProperties(dir)

		def messagesByLocale = [:],
		    allTranslations = [:],
		    problem = [:]


		siteParamsBySite.languageCode.each { locale, sites ->
			def site = sites.last()
			def properties = messagesBySite[site]

			messagesByLocale[locale] = properties

			properties.keys.each { key ->
				if (!allTranslations.containsKey(key)) allTranslations[key] = [:]

				def detail = [:]

				sites.each {
					detail[it] = messagesBySite[it].getString(key)
				}

				if (detail.values().toList().unique().size() > 1) {
					problem[key + '=' + locale] = detail
				}


				allTranslations[key][locale] = properties.getString(key)

			}
		}

		[
				messagesByLocale: messagesByLocale,
				allTranslations: allTranslations,
				problem: problem
		]
	}



	Map<String, PropertiesConfiguration> getMessagesBySite() {
		if (_messagesBySite)
			return _messagesBySite;

		new File(_path(I18N_PATH)).eachDir {
			def site = it
			_messagesBySite[site.name] = getMessagesProperties(site, I18N_FILENAME)
		}
		return _messagesBySite
	}

	List<String> getSites() {
		List sites = []
		new File(_path(dirName)).eachDir {
			sites << it.name
		}
		return sites
	}


	Map getSiteParamsBySite() {
		Map allConfig = [:]
		sites.each { site ->
			def properties = getMessagesProperties(site, SITE_PARAMS_FILENAME)
			properties.keys.each {
				def key = properties.getString(it).trim()

				if (!allConfig.containsKey(it))
					allConfig[it] = [:]

				if (!allConfig[it].containsKey(key))
					allConfig[it][key] = []

				allConfig[it][key] << site
			}
		}

		allConfig
	}


	Map getMultiSitesProperties() {
		getMultiSitesProperties([], true)
	}

	Map getMultiSitesProperties(List filter, Boolean all = false) {
		File dir = new File(_path(dirName))

		Map translations = [:], allTranslations = [:]
		List sites = [], allSites = []

		dir.eachDir {
			def site = it
			allSites << it.name
			if (all || site.name in filter) {
				sites << site.name
				def properties = getMessagesProperties(site)

				properties.keys.each {
					if (!allTranslations.containsKey(it)) allTranslations[it] = [:]

					allTranslations[it][site.name] = properties.getString(it)
				}

				translations[it.name] = properties
			}
		}

		[allTranslations: allTranslations, sites: sites, allSites: allSites]

	}


	String getFileName() {
		propFile == 'translations' ? I18N_FILENAME : SITE_PARAMS_FILENAME
	}

	String getDirName() {
		propFile == 'translations' ? I18N_PATH : SITE_PARAMS_PATH
	}

	private File getMessagesFile(String site, String file = '') {
		new File(new File(new File(_path(dirName)), site), file ?: fileName)
	}

	private PropertiesConfiguration getMessagesProperties(String site, String file = '') {
		def dir = file == TRANSLATIONS ? _path(I18N_PATH) : _path(SITE_PARAMS_PATH)
		getMessagesProperties(new File(new File(dir), site), file)
	}

	private PropertiesConfiguration getMessagesProperties(File site, String file = '') {
		def properties = new PropertiesConfiguration()
		File propertiesFile = new File(site, file ?: fileName)

		properties.load(propertiesFile.newReader("UTF-8"))
		properties.setEncoding("UTF-8");
		return properties
	}



	void update(String site, Map map) {
		PropertiesConfiguration messages = getMessagesProperties(site)

		map.each { key, value ->
			messages.setProperty(key, value.toString())
		}

		def os = new ByteArrayOutputStream()
		messages.save(os);

		// hack
		File f = getMessagesFile(site)
		def newFile = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
		newFile.write(StringEscapeUtils.unescapeJava(os.toString('UTF-8')))
		newFile.close()
	}

	void update(String site, String key, String value) {
		def map = [:]
		map[key] = value
		update(site, map)
	}



	private String _path(sub) {
		return projectPath + sub
	}

}
