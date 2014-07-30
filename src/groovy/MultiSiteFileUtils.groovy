import grails.converters.JSON
import org.apache.commons.configuration.AbstractConfiguration
import org.apache.commons.configuration.PropertiesConfiguration
import org.apache.commons.lang.StringEscapeUtils

import java.text.DateFormat

class MultiSiteFileUtils {
	static final String SITE_PARAMS = 'siteParams.properties'
	static final String TRANSLATIONS = 'messages.properties'

	private File projectPath
	private Map<String, PropertiesConfiguration> _messagesBySite = [:]

	private static final String SITE_PARAMS_FILENAME = 'siteParams.properties'
	private static final String I18N_PATH = 'grails-app/i18n/'
	private static final String SITE_PARAMS_PATH = 'web-app/WEB-INF/sites/'
	private static final String I18N_FILENAME = 'messages.properties'

	MultiSiteFileUtils(File path, String propertyFile) {
		propFile = propertyFile
		projectPath = path
	}

	protected String propFile = ''



	Map translationsByLang() {
		def messagesByLocale = [:],
		    allTranslations = [:],
		    problem = [:]


		def messagesPropertiesByLocale = [:]
		Map<String, PropertiesConfiguration> siteParams = [:]
		sites.each {
			siteParams[it] = getMessagesProperties(it, SITE_PARAMS_FILENAME)
		}

		List addCountry = ['es', 'pt', 'zn']
		siteParamsBySite.languageCode.each { language, sites2 ->
			def languages = [:]
			if (language in addCountry) {
				sites2.each {
					def k = language + '_' + siteParams[it].getString('countryCode')?.trim()
					languages[k] = [it]
				}
			} else {
				languages[language] = sites2
			}

			println languages

			languages.each { locale, sites ->
				def site = sites.last()

				def messages = sites.collect { messagesBySite[it] }

				def properties = messages.last()

				messagesByLocale[locale] = properties
				messagesPropertiesByLocale[locale] = messages

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
		}

		[
				messagesByLocale: messagesByLocale,
				allTranslations: allTranslations,
				problem: problem,
				messagesPropertiesByLocale: messagesPropertiesByLocale
		]
	}



	Map<String, PropertiesConfiguration> getMessagesBySite() {
		if (_messagesBySite)
			return _messagesBySite;

		_path(I18N_PATH).eachDir {
			def site = it
			if (it.name != '.svn')
				_messagesBySite[site.name] = getMessagesProperties(site, I18N_FILENAME)
		}
		return _messagesBySite
	}

	List<String> getSites() {
		List sites = []
		_path(dirName).eachDir {
			if (it.name != '.svn')
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
		File dir = _path(dirName)

		Map translations = [:], allTranslations = [:]
		List sites = [], allSites = []

		dir.eachDir {
			if (it.name != '.svn') {
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
		}

		[allTranslations: allTranslations, sites: sites, allSites: allSites]

	}


	String getFileName() {
		propFile == TRANSLATIONS ? I18N_FILENAME : SITE_PARAMS_FILENAME
	}

	String getDirName() {
		propFile == TRANSLATIONS ? I18N_PATH : SITE_PARAMS_PATH
	}

	private File getMessagesFile(String site, String file = '') {
		new File(new File(_path(dirName), site), file ?: fileName)
	}

	private PropertiesConfiguration getMessagesProperties(String site, String file = '') {
		def dir = file == TRANSLATIONS ? _path(I18N_PATH) : _path(SITE_PARAMS_PATH)
		getMessagesProperties(new File(dir, site), file)
	}

	private PropertiesConfiguration getMessagesProperties(File site, String file = '') {
		def properties = new PropertiesConfiguration()
		PropertiesConfiguration.defaultListDelimiter = AbstractConfiguration.DISABLED_DELIMITER

		File propertiesFile = new File(site, file ?: fileName)

		properties.file = propertiesFile
		properties.load(propertiesFile.newReader("UTF-8"))
		properties.setEncoding("UTF-8");
		return properties
	}



	void update(String site, Map map) {
		PropertiesConfiguration messages = getMessagesProperties(site, propFile)
		update(messages, map)
	}

	void update(PropertiesConfiguration messages, Map map) {
		messages.fileName
		map.each { key, value ->
			messages.setProperty(key, value.toString())
		}
		save(messages)
	}


	void save(PropertiesConfiguration messages) {
		def os = new ByteArrayOutputStream()
		messages.save(os);
		// hack
		File f = messages.file
		def newFile = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
		newFile.write(StringEscapeUtils.unescapeJava(os.toString('UTF-8')))
		newFile.close()
	}

	void update(PropertiesConfiguration messages, String key, String value) {
		Map map = [:]
		map[key] = value
		update(messages, map)
	}

	void update(String site, String key, String value) {
		Map map = [:]
		map[key] = value
		update(site, map)
	}




	private File _path(sub) {
		return new File(projectPath, sub)
	}


}
