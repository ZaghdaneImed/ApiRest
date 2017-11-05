package apirest

import grails.converters.JSON
import grails.converters.XML

class ApiController {

        def book() {
            switch (request.getMethod()){
                case "GET":
                    if(params.id) {
                        def book = Book.get(params.id)
                        if (book) {
                            withFormat {
                                json { render book as JSON }
                                xml { render book as XML }
                            }
                        } else {
                            response.status = 404
                            render "Not existing book!!."
                        }
                    }else {
                        withFormat {
                            json { render Book.findAll() as JSON }
                            xml { render Book.findAll() as XML }
                        }
                    }
                    break;
                case "POST":
                    def jsonObj = request.JSON
                    if(jsonObj.library){
                        def lib = Library.findById(jsonObj.library.id)
                        if(lib){
                            lib.addToBooks(new Book(name: jsonObj.name,ISBN: jsonObj.ISBN,author: jsonObj.author,releaseDate: 2010))
                            lib.save(flush: true)

                            if(lib.save(flush: true)) {
                                response.status = 201
                                render "Successfully saved."
                            }
                            else {
                                render "Library Not saved."
                                response.status = 400
                            }
                        }else{
                            response.status = 404
                            render "Not Existing library."
                        }

                    }else{
                        render """You must include the ID of the library in the JSON body"""
                    }

                    break;
                case "PUT":
                    def jsonObj = request.JSON
                    if(params.id){
                        def book = Book.findById(params.id)
                        if(book) {
                            if (jsonObj.name != null) {
                                book.name = jsonObj.name
                            }
                            if (jsonObj.ISBN != null) {
                                book.ISBN = jsonObj.ISBN
                            }
                            if (jsonObj.releaseDate != null) {
                                book.releaseDate = new Date(jsonObj.releaseDate)
                            }
                            if (jsonObj.author != null) {
                                book.author = jsonObj.author
                            }

                            if (jsonObj.library != null) {
                                if (book.library != jsonObj.library) {
                                    def lib = Library.findById(book.library.id)
                                    lib.removeFromBooks(book)
                                    def lib2 = Library.findById(jsonObj.library.id)
                                    if (lib2) {
                                        lib2.addToBooks(book)
                                        book.save(flush: true)
                                        lib2.save(flash: true)
                                    } else {
                                        render (status: 404, text: "Library not exist.")
                                        return
                                    }
                                }
                            }

                            book.save(flush: true)
                            if (book.save()) {
                                response.status = 200 // OK
                                //render "Successfully update.\n"
                                render book as JSON
                            } else {
                                response.status = 500 //Internal Server Error
                                render "Could not update due to errors"
                            }
                        }else{
                            response.status = 404 //not found
                            render "Book not found"
                        }
                    }
                    else{
                        //render """You must include the ID of the book!"""
                        render (status: 400, text: """You must include the ID of the book!""")
                        return
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
                            render "Book not found."
                        }
                    }
                    else{
                        response.status = 400 //Bad Request
                        render """DELETE request must include the ID of the book"""
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
                    if(lib) {
                        if (params.idB) {
                            def book = Book.get(params.idB)
                            if (book) {
                                if (lib.books.contains(book)) {
                                    withFormat {
                                        json { render book as JSON }
                                        xml { render book as XML }
                                    }
                                } else {
                                    response.status = 404 //not found
                                    render """This book is not existing in the library!!"""
                                }
                            } else {
                                response.status = 404 //not found
                                render """This book is not existing!!"""
                            }
                        } else {
                            withFormat {
                                json { render lib.books as JSON }
                                xml { render lib.books as XML }
                            }
                        }
                    }else{
                        render (status: 404, text: "Not existing library")
                    }
                break;
                case "POST":
                    if(!Library.findById(params.id)){
                        render (status: 404, text: "can't attach a book to a not existent library")
                        return
                    }

                    def jsonObj = request.JSON
                    def lib = Library.findById(params.id)
                    lib.addToBooks(new Book(name: jsonObj.name,ISBN: jsonObj.ISBN,author: jsonObj.author,releaseDate: new Date(jsonObj.releaseDate)))
                    lib.save(flush: true)
                    if(lib.save(flush:true)) {
                        response.status = 201
                        render "Successfully saved."
                    }
                    else{
                        response.status = 400
                        render "Book not saved."
                    }

                    break;
                case "PUT":
                    def jsonObj = request.JSON
                    if(params.id){
                        if(params.idB) {
                            def lib = Library.findById(params.id)
                            def book = Book.findById(params.idB)
                            if(lib) {
                                if(book) {
                                    if (lib.books.contains(book)) {
                                        if (jsonObj.name != null) {
                                            book.name = jsonObj.name
                                        }
                                        if (jsonObj.ISBN != null) {
                                            book.ISBN = jsonObj.ISBN
                                        }
                                        if (jsonObj.releaseDate != null) {
                                            book.releaseDate = new Date(jsonObj.releaseDate)
                                        }
                                        if (jsonObj.author != null) {
                                            book.author = jsonObj.author
                                        }

                                        book.save(flush: true)
                                        lib.save(flush: true)
                                        if (lib.save()) {
                                            response.status = 200 // OK
                                            render "Successfully updated."
                                        } else {
                                            response.status = 400 //
                                            render "Book not updated!!"
                                        }
                                    }
                                    else{
                                        render (status: 404, text: "This book is not existing in the library!!")
                                    }
                                }else{
                                    render (status: 404, text: "Not existing Book")
                                }
                            }else{
                                render (status: 404, text: "Not existing library")
                            }
                        }
                        else{
                            response.status = 400 //Bad Request
                            render """Update request must include the ID of the book"""
                        }
                    }
                    else{
                        response.status = 400 //Bad Request
                        render """Update request must include the IDof library"""
                    }
                    break
                case "DELETE":
                    if(params.id){
                        if(params.idB) {
                            def lib = Library.findById(params.id)
                            def book = Book.findById(params.idB)
                            if (lib) {
                                if (book) {
                                    if (lib.books.contains(book)) {
                                        lib.removeFromBooks(book)
                                        book.delete(flush: true)
                                        render "Successfully Deleted."
                                    } else {
                                        response.status = 404 //not found
                                        render """This book is not existing in the library!!"""
                                    }
                                } else {
                                    response.status = 404 //Not Found
                                    render "Book not found."
                                }
                            }else{
                                response.status = 404 //Not Found
                                render "Library not found."
                            }
                        }
                        else{
                            response.status = 400 //Bad Request
                            render """DELETE request must include the ID of the book"""
                        }
                    }
                    else{
                        response.status = 400 //Bad Request
                        render """DELETE request must include the IDof library"""
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
                        if(lib){
                            withFormat {
                                json { render lib as JSON}
                                xml { render lib as XML}
                            }
                        }
                        else{
                            render (status: 404, text: "Library not exist.")
                        }

                    }

                    break;
                case "POST":
                    def jsonObj = request.JSON


                    def lib = new Library(jsonObj)
                    if (!lib.save(flush: true)){
                        lib.errors.each {
                            println it
                        }
                        render (status: 400, text: "Library not saved (Bad Request)")
                    }else{
                        lib.save(flush: true)
                        render (status: 201, text: "Library saved")
                    }
                    break;
            case "PUT":
                def jsonObj = request.JSON
                if(params.id){
                    def lib = Library.findById(params.id)
                    if(lib){
                        if( jsonObj.name != null )
                        {lib.name = jsonObj.name}

                        if( jsonObj.address != null )
                        {lib.address = jsonObj.address}

                        if( jsonObj.yearCreated != null )
                        {lib.yearCreated = jsonObj.yearCreated}

                        if(lib.save(flush: true)){
                            response.status = 200 // OK
                            render lib as JSON
                        }
                        else{
                            response.status = 500 //Internal Server Error
                            render "Could not update due to errors"
                        }
                    }else{
                        response.status = 404
                        render "This library is not existing"
                    }
                }
                else{
                    response.status = 400 //Bad Request
                    render """PUT request must include the ID of the library"""
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
                        render "Library not found."
                    }
                }
                else{
                    response.status = 400 //Bad Request
                    render """DELETE request must include the ID of the library"""
                }
                break
                default:
                    response.status = 405
                    break;
            }
            //render "ok"
        }
    }
