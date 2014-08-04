class TreeBuilderTagLib {
	static namespace = "uke"
	def tree = { attrs ->
		def template = attrs.template

		if (template.childrens) {
			out << '<ul>'
			template.childrens.each {
				out << '<li>'
				out << it.name
				out << tree(template: it)
				out << '</li>'
			}
			out << '</ul>'
		}
	}
}
