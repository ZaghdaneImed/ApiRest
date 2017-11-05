package apirest

class BootStrap {

    def init = { servletContext ->
        def lib1 = new Library(name: "lib1",address: "nice",yearCreated: 2010).save(flush:true)
        def lib2 = new Library(name: "lib2",address: "sophia",yearCreated: 2015).save(flush:true)
        lib1.addToBooks(new Book(name: "livre1",ISBN: "a10",author: "author1",releaseDate: new Date ("01/01/2010"))).save(flush:true)
        lib1.addToBooks(new Book(name: "livre2",ISBN: "a11",author: "author2",releaseDate: new Date ("01/01/2010"))).save(flush:true)
        lib2.addToBooks(new Book(name: "livre3",ISBN: "a12",author: "author3",releaseDate: new Date ("01/01/2010"))).save(flush:true)
    }
    def destroy = {
    }
}
