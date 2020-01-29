package spring

class MyMember {

    String name

    static constraints = {
    }

    static mapping = {
        version false
        //addresses joinTable: [name:'my_address', key:'my_member_id']
    }

    static hasMany = [addresses:MyAddress]

}
