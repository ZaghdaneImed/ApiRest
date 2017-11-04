package apirest

class Book {
    String name;
    String ISBN;
    String author;
    Date releaseDate;

    static belongsTo = [library:Library]

    static constraints = {
        name blank: false
        //releaseDate nullable:false
        ISBN null:false
        author blank: false
    }
}
