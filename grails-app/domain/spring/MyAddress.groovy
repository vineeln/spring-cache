package spring

class MyAddress {
    String addressLine1
    String addressLine2

    static constraints = {
        addressLine1 (nullable: true)
        addressLine2 (nullable: true)
    }

    static belongsTo = [member:MyMember]
}
