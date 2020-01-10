package spring

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        // "500"(view:"/error")  error in this was displaying tomcat error page
        "500"(controller:'error',action: 'handleError')
        "404"(view:'/notFound')
    }
}
