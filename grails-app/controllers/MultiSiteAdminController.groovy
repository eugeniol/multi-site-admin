import grails.converters.JSON
import org.apache.commons.configuration.PropertiesConfiguration
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.springframework.core.io.ClassPathResource

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


	List getFilter() {
		params.list('filter') ?: []
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



	def delete = {
		if (request.post) {
			def key = params.key,
			    type = params.type == 'translations' ? MultiSiteFileUtils.TRANSLATIONS : MultiSiteFileUtils.SITE_PARAMS

			def ctrl = new MultiSiteFileUtils(resolvePath(), type)

			ctrl.messagesBySite.each { site, prop ->
				if (prop.containsKey(key)) {
					prop.clearProperty(key)

					ctrl.save(prop)
				}
			}

			return render(params as JSON)
		}
	}

	def rename = {
		if (request.post) {
			def key = params.key,
			    newKey = params.newKey,
			    type = params.type == 'translations' ? MultiSiteFileUtils.TRANSLATIONS : MultiSiteFileUtils.SITE_PARAMS

			def ctrl = new MultiSiteFileUtils(resolvePath(), type)

			ctrl.messagesBySite.each { site, prop ->
				if (prop.containsKey(key)) {
					def val = prop.getString(key)
					prop.clearProperty(key)
					prop.addProperty(newKey, val)

					ctrl.save(prop)
				}
			}

			return render(params as JSON)
		}
	}


	def reset = {
		session.invalidate()
		redirect action: 'config'
	}


	def config = {
		def defaultPath = ''

		String applicationPath = request.getSession().getServletContext().getRealPath("")


		def workspace = new File(new File(applicationPath), '../../')
		def emaFile = new File(workspace, 'ema-site-app')

		println emaFile.absolutePath
		if (emaFile.exists())
			defaultPath = emaFile.canonicalFile.absolutePath

		if (request.post) {
			session.project_path = params.project_path
			redirect action: 'index'
		}

		[defaultPath: defaultPath]
	}

	def siteParams = {
		def ctrl = new MultiSiteFileUtils(resolvePath(), MultiSiteFileUtils.SITE_PARAMS)
		if (request.post) {
			ctrl.update(params.site, params.key, params.value)
			render([ok: true] as JSON)
		} else {
			ctrl.getMultiSitesProperties(filter) + [type: 'siteparams']
		}

	}

	File resolvePath() {
		assert session.project_path
		def file = new File(session.project_path)
		assert file.exists()
		println file
		println file.exists()
		return file
	}


	def translationsBySite = {
		def ctrl = new MultiSiteFileUtils(resolvePath(), MultiSiteFileUtils.TRANSLATIONS)
		if (request.post) {
			ctrl.update(params.site, params.key, params.value) as JSON
			render([ok: true] as JSON)
		} else {
			ctrl.getMultiSitesProperties(filter) + [type: 'translations']
		}

	}

	def translationsByLanguage = {
		def locales = [:]
		DateFormat.availableLocales.each {
			locales[it.toString()] = it
		}


		def obj = new MultiSiteFileUtils(resolvePath(), MultiSiteFileUtils.TRANSLATIONS)

		def translationsByLang = obj.translationsByLang()

		def messagesPropertiesByLocale = translationsByLang.messagesPropertiesByLocale

		def messagesByLocale = translationsByLang.messagesByLocale

		def localesList = messagesByLocale.keySet().sort()

		if (request.post && messagesPropertiesByLocale[params.site]) {
			List<PropertiesConfiguration> prop = messagesPropertiesByLocale[params.site]
			prop.each {
				obj.update(it, params.key, params.value)
			}
		}

		[
				locales: locales,
				localesList: localesList,
				messagesByLocale: translationsByLang.messagesByLocale,
				allTranslations: translationsByLang.allTranslations,
				problem: translationsByLang.problem,
				type: 'siteparams'
		]

	}

	def siteParamsByKey = {

		def siteParamsBySite = new MultiSiteFileUtils(resolvePath(), MultiSiteFileUtils.SITE_PARAMS).siteParamsBySite

		[allConfig: siteParamsBySite]
	}


}

