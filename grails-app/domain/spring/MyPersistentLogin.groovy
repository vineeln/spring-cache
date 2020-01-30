package spring

class MyPersistentLogin {

    String id
    String username
    String token


    static constraints = {
        username maxSize: 64
        token maxSize: 64
        id maxSize: 64
    }

    static transients = ['series']

    void setSeries(String series) { id = series }
    String getSeries() { id }

    static mapping = {
        table 'my_persistent_logins'
        id column: 'series', generator: 'assigned'
        version false
    }
}
