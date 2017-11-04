package apirest

import grails.converters.JSON
import grails.converters.XML

class ApiController {

        def book() {
            switch (request.getMethod()){
                case "GET":
                    def book = Book.get(params.id)
                    if(book)
                    {
                        withFormat {
                            json { render book as JSON}
                            xml { render book as XML}
                        }
                    }
                    else
                    {
                        withFormat {
                            json { render Book.findAll() as JSON}
                            xml { render Book.findAll() as XML}
                        }
                    }

                    break;
                case "POST":
                    def jsonObj = request.JSON
                    //def book = new Book(jsonObj.name ,jsonObj.ISBN,jsonObj.author)
                    def lib = Library.findById(jsonObj.library.id)
                    //book.save(flush: true)
                    lib.addToBooks(new Book(name: jsonObj.name,ISBN: jsonObj.ISBN,author: jsonObj.author,releaseDate: 2010))
                    lib.save(flush: true)

                    if(lib.save(flush: true)) {
                        response.status = 201
                        render "Successfully saved."
                    }
                    else
                        render jsonObj.library.id
                        response.status = 400
                    break;
                case "PUT":
                    def jsonObj = request.JSON
                    def book = Book.findById(params.id)

                    if( jsonObj.name != null )
                    {book.name = jsonObj.name}
                    if( jsonObj.ISBN != null )
                    {book.ISBN = jsonObj.ISBN}
                    if( jsonObj.releaseDate != null )
                    {book.releaseDate = new Date(jsonObj.releaseDate)}
                    if( jsonObj.author != null )
                    {book.author = jsonObj.author}

                    if( jsonObj.library != null )
                    {
                        if(book.library != jsonObj.library){
                            def lib = Library.findById(book.library.id)
                            lib.removeFromBooks(book)
                            def lib2 = Library.findById(jsonObj.library.id)
                            if(lib2){
                                lib2.addToBooks(book)
                                book.save(flush: true)
                                lib2.save(flash: true)
                            }
                            else{
                                render "Library not exist."
                            }
                        }
                    }

                    book.save(flush: true)
                    if(book.save()){
                        response.status = 200 // OK
                        //render "Successfully update.\n"
                        render book as JSON
                    }
                    else{
                        response.status = 500 //Internal Server Error
                        render "Could not create new Catalogo due to errors:\n ${lib.errors}"
                    }
                    break
                case "DELETE":
                    if(params.id){
                        def book = Book.findById(params.id)
                        if(book){
                            def lib = Library.findById(book.library.id)
                            lib.removeFromBooks(book)
                            book.delete(flush: true)
                            render "Successfully Deleted."
                        }
                        else{
                            response.status = 404 //Not Found
                            render "${params.id} not found."
                        }
                    }
                    else{
                        response.status = 400 //Bad Request
                        render """DELETE request must include the ID code Example: /rest/library/id"""
                    }
                    break
            //othrs case
                default:
                    response.status = 405
                    break;
            }
            //render "ok"
        }

        def libraryBooks() {
            switch (request.getMethod()) {
                case "GET":
                    def lib = Library.get(params.id)
                    def book = Book.get(params.idB)
                    if(book)
                    {
                        if(lib.books.contains(book)) {
                            withFormat {
                                json { render book as JSON }
                                xml { render book as XML }
                            }
                        }
                    }
                    else {
                        withFormat {
                            json { render lib.books as JSON }
                            xml { render lib.books as XML }
                        }
                    }
                break;
                case "POST":
                    if(!Library.get(params.id)){
                        render (status: 400, text: "can't attach a book to a not existent library (${params.library.id})")
                        return
                    }

                    def jsonObj = request.JSON
                    def bookInstance = new Book(jsonObj)
                    def lib = Library.get(params.id)
                    //bookInstance.save(flush: true)
                    lib.addToBooks(new Book(name: jsonObj.name,ISBN: jsonObj.ISBN,author: jsonObj.author,releaseDate: new Date(jsonObj.releaseDate)))
                    lib.save(flush: true)
                    if(lib.save(flush:true)) {
                        response.status = 201
                        render "Successfully saved."
                    }
                    else
                        response.status = 400
                    break;
                case "PUT":
                    def jsonObj = request.JSON
                    def lib = Library.findById(params.id)
                    def book = Book.findById(params.idB)

                        if( jsonObj.name != null )
                        {book.name = jsonObj.name}
                        if( jsonObj.ISBN != null )
                        {book.ISBN = jsonObj.ISBN}
                        if( jsonObj.releaseDate != null )
                        {book.releaseDate = new Date(jsonObj.releaseDate)}
                        if( jsonObj.author != null )
                        {book.author = jsonObj.author}

                    book.save(flush: true)
                    lib.save(flush: true)
                    if(lib.save()){
                        response.status = 200 // OK
                        render book as JSON
                    }
                    else{
                        response.status = 500 //Internal Server Error
                        render "Could not create new Catalogo due to errors:\n ${lib.errors}"
                    }
                    break
                case "DELETE":
                    if(params.id){
                        def lib = Library.findById(params.id)
                        def book = Book.findById(params.idB)
                        if(book){
                            lib.removeFromBooks(book)
                            book.delete(flush: true)
                            render "Successfully Deleted."
                        }
                        else{
                            response.status = 404 //Not Found
                            render "${params.id} not found."
                        }
                    }
                    else{
                        response.status = 400 //Bad Request
                        render """DELETE request must include the ID code Example: /rest/library/id"""
                    }
                    break
                default:
                    response.status = 405
                    break;
            }
        }

        def library() {
            switch (request.getMethod()){
                case "GET":
                    if (params.id == null){
                        withFormat {
                            json { render Library.findAll() as JSON}
                            xml { render Library.findAll() as XML}
                        }
                    }
                    else
                    {
                        def lib = Library.get(params.id)
                        withFormat {
                            json { render lib as JSON}
                            xml { render lib as XML}
                        }
                    }

                    break;
                case "POST":
                    /*def libInstance = new Library(params.library)
                    if(libInstance.save(flush:true))
                        render (status: 201, text: "library saved")
                    else
                        response.status = 400*/
                    def jsonObj = request.JSON


                    def lib = new Library(jsonObj)
                    lib.save(flush: true)

                    if (!lib.save(flush: true)){
                        lib.errors.each {
                            println it
                        }
                    }

                    render  request.getJSON()
                    break;
            case "PUT":
                def jsonObj = request.JSON
                def lib = Library.findById(params.id)

                    if( jsonObj.name != null )
                    {lib.name = jsonObj.name}

                    if( jsonObj.address != null )
                    {lib.address = jsonObj.address}

                    if( jsonObj.yearCreated != null )
                    {lib.yearCreated = jsonObj.yearCreated}

                lib.save(flush: true)
                if(lib.save()){
                    response.status = 200 // OK
                    render lib as JSON
                }
                else{
                    response.status = 500 //Internal Server Error
                    render "Could not create new Catalogo due to errors:\n ${lib.errors}"
                }
                break
            case "DELETE":
                if(params.id){
                    def lib = Library.findById(params.id)
                    if(lib){
                        lib.books.each {
                            it.delete()
                        }
                        lib.delete(flush: true)
                        render "Successfully Deleted."
                    }
                    else{
                        response.status = 404 //Not Found
                        render "${params.id} not found."
                    }
                }
                else{
                    response.status = 400 //Bad Request
                    render """DELETE request must include the ID code Example: /rest/catalogo/id"""
                }
                break
                default:
                    response.status = 405
                    break;
            }
            //render "ok"
        }
    }
