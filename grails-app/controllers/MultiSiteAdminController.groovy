import grails.converters.JSON

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
			println "cookie not set"
			redirect(action: 'config')
		}

	}


	def reset = {
		session.invalidate()
		redirect action: 'config'
	}


	def config = {
		def defaultPath = ''

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
			ctrl.getMultiSitesProperties(filter)
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
			ctrl.getMultiSitesProperties(filter)
		}

	}

	def translationsByLanguage = {
		def locales = [:]
		DateFormat.availableLocales.each {
			locales[it.toString()] = it
		}

		def obj = new MultiSiteFileUtils(resolvePath(), MultiSiteFileUtils.TRANSLATIONS)

		def translationsByLang = obj.translationsByLang()

		def localesList = translationsByLang.messagesByLocale.keySet().sort()

		[
				locales: locales,
				localesList: localesList,
				messagesByLocale: translationsByLang.messagesByLocale,
				allTranslations: translationsByLang.allTranslations,
				problem: translationsByLang.problem
		]

	}

	def siteParamsByKey = {
		def siteParamsBySite = new MultiSiteFileUtils(resolvePath(), MultiSiteFileUtils.SITE_PARAMS).siteParamsBySite

		[allConfig: siteParamsBySite]
	}


}
