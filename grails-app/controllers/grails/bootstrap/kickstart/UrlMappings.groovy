package grails.bootstrap.kickstart

class UrlMappings {

    static mappings = {

        // simple/static pages
        "/"(controller: 'simplePage', action: 'index')
        "/page/$viewName"(controller: 'simplePage', action: 'index')

        // default grails /controller/action/id-style
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }


        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
