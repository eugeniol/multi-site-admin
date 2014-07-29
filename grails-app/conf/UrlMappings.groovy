class UrlMappings {
	static mappings = {
		"/"(controller: 'multiSiteAdmin', action: 'index')

		"/admintool/$action?/$id?"(controller: 'multiSiteAdmin')


	}
}
