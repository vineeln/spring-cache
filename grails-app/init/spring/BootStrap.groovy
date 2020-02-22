package spring

class BootStrap {

    SpringCacheService springCacheService

    def init = { servletContext ->
        MyUser.withTransaction {
            createUser('vnalla','password')
            createUser( 'otheruser','password')
            createUser( 'filteruser','password')
            createUser( 'serviceuser','password')
            createUser( 'controlleruser','password')

            springCacheService.createMember("filterMember")
            springCacheService.createMember("serviceMember")
            springCacheService.createMember("controllerMember")
            springCacheService.createMember("lazy1")
            springCacheService.createMember("lazy2")
        }
    }
    def destroy = {
    }


    def createUser = { String name, String pwd ->
        MyRole role = MyRole.findByAuthority('ROLE_USER')
        if( !role ) {
            role = new MyRole(authority: 'ROLE_USER')
            role.save(flush:true)
        }

        MyUser user = MyUser.findByUsername(name)
        if( !user ) {
            user = new MyUser(username:name,password: pwd)
            user.save(flush:true);
            MyUserMyRole.create( user, role )
        }
    }
}
