package apirest

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')

        "/api/books"(controller: 'api',action: 'book')
        "/api/book/$id"(controller: 'api',action: 'book')
        "/api/libraries"(controller: 'api',action: 'library')
        "/api/library/$id"(controller: 'api',action: 'library')
        "/api/library/$id/books"(controller: 'api',action: 'libraryBooks')
        "/api/library/$id/book/$idB"(controller: 'api',action: 'libraryBooks')
    }
}
