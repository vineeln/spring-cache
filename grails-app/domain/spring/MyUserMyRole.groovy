package spring

import grails.gorm.DetachedCriteria
import groovy.transform.ToString

import org.codehaus.groovy.util.HashCodeHelper
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
@ToString(cache=true, includeNames=true, includePackage=false)
class MyUserMyRole implements Serializable {

	private static final long serialVersionUID = 1

	MyUser myUser
	MyRole myRole

	@Override
	boolean equals(other) {
		if (other instanceof MyUserMyRole) {
			other.myUserId == myUser?.id && other.myRoleId == myRole?.id
		}
	}

    @Override
	int hashCode() {
	    int hashCode = HashCodeHelper.initHash()
        if (myUser) {
            hashCode = HashCodeHelper.updateHash(hashCode, myUser.id)
		}
		if (myRole) {
		    hashCode = HashCodeHelper.updateHash(hashCode, myRole.id)
		}
		hashCode
	}

	static MyUserMyRole get(long myUserId, long myRoleId) {
		criteriaFor(myUserId, myRoleId).get()
	}

	static boolean exists(long myUserId, long myRoleId) {
		criteriaFor(myUserId, myRoleId).count()
	}

	private static DetachedCriteria criteriaFor(long myUserId, long myRoleId) {
		MyUserMyRole.where {
			myUser == MyUser.load(myUserId) &&
			myRole == MyRole.load(myRoleId)
		}
	}

	static MyUserMyRole create(MyUser myUser, MyRole myRole, boolean flush = false) {
		def instance = new MyUserMyRole(myUser: myUser, myRole: myRole)
		instance.save(flush: flush)
		instance
	}

	static boolean remove(MyUser u, MyRole r) {
		if (u != null && r != null) {
			MyUserMyRole.where { myUser == u && myRole == r }.deleteAll()
		}
	}

	static int removeAll(MyUser u) {
		u == null ? 0 : MyUserMyRole.where { myUser == u }.deleteAll() as int
	}

	static int removeAll(MyRole r) {
		r == null ? 0 : MyUserMyRole.where { myRole == r }.deleteAll() as int
	}

	static constraints = {
	    myUser nullable: false
		myRole nullable: false, validator: { MyRole r, MyUserMyRole ur ->
			if (ur.myUser?.id) {
				if (MyUserMyRole.exists(ur.myUser.id, r.id)) {
				    return ['userRole.exists']
				}
			}
		}
	}

	static mapping = {
		id composite: ['myUser', 'myRole']
		version false
	}
}
