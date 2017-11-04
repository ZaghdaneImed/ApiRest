package apirest

class BootStrap {

    def init = { servletContext ->
        def lib1 = new Library(name: "lib1",address: "nice",yearCreated: 2010).save(flush:true)
        def lib2 = new Library(name: "lib2",address: "sophia",yearCreated: 2015).save(flush:true)
        lib1.addToBooks(new Book(name: "abc",ISBN: "a12",author: "frnc",releaseDate: 2010)).save(flush:true)
        lib1.addToBooks(new Book(name: "abc",ISBN: "a12",author: "frnc",releaseDate: 2010)).save(flush:true)
        lib2.addToBooks(new Book(name: "abc",ISBN: "a12",author: "frnc",releaseDate: 2010)).save(flush:true)
    }
    def destroy = {
    }
}
