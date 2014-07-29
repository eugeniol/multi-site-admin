class UrlMappings {


	static mappings = {

		getProperty('action')
		getProperty('id')
		name home: "/admintool/(*)?/(*)?"(controller: 'multiSiteAdmin')

		"/test/$action?"(controller: 'test')


	}
}
