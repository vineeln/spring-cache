package spring

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import groovy.util.logging.Log4j
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

import javax.servlet.http.HttpServletRequest
import java.lang.reflect.Member

//@Transactional
@Log4j
@Component
class SpringCacheService {

    SessionFactory sessionFactory
    SpringSecurityService springSecurityService

    def serviceMethod() {
        //log.info "In Service Method"
        println "In Service Method"
        "did something"
    }

    @Cacheable(value = "addresses", key = "#customer")
    def findCustomerName(String customer) {
        println "In findCustomerName"
        return "${customer} Name"
    }

    def createMember(String memberName) {
        MyMember m = new MyMember(name:memberName);
        MyAddress address = new MyAddress(addressLine1: 'line1', addressLine2: 'line2')
        m.addToAddresses(address)
        m.save(flush:true,failOnError:true)
    }

    //@Transactional
    def connectInfo() {
        def currentSession = sessionFactory.currentSession
        def q = "show variables like 'autocommit'"
        def data = currentSession.createSQLQuery(q)
        final result = data.list()
        return result
    }

    @Transactional(readOnly = true)
    MyMember findMember(String name) {
        def list = MyMember.findAllWhere(name:name)
        return list[0]
    }

    @Transactional(readOnly = true)
    MyMember findFirstMember() {
        def list = MyMember.list()
        return list[0]
    }

    @Transactional
    def updateMember(MyMember member) {
        member.save(flush:true,failOnError:true)
    }

    @Transactional
    def newMemberInTxn() {
        MyMember m = new MyMember(name:"somename");
        MyAddress address = new MyAddress(addressLine1: 'line1', addressLine2: 'line2')
        m.addToAddresses(address)
        m.save(flush:true,failOnError:true)
    }

    @Transactional
    def dummyTransactionMethod() {
        log.info("In the dummyTransactionMethod")
    }

    def logSessionNoTxn(String context) {
        Session s = findSession(context+':service:  noTxn')
        //loadUser('service:  noTxn')
        println "${context}:         service:  noTxn: id: ${s.hashCode()}, flushMode:${s.flushMode}"// value:${s}"
        println ""
    }

    @Transactional
    def logSessionWithTransaction(String context) {
        Session s = findSession(context+': service withTxn')
        //loadUser('service withTxn')
        println "${context}:         service withTxn: id: ${s.hashCode()}, flushMode:${s.flushMode}"// value:${s}"
        println ""
    }

    private loadUser(String context) {
        try {
            MyUser.findByUsername('serviceuser')
            println "${context}: loaded user"
        } catch( Exception x ) {
            println "${context}: exception while loading user"
        }
    }

    Session findSession(String context) {
        try {
            Session s = sessionFactory.currentSession
            //println "${context}: FOUND, id:${s.hashCode()} flushMode:${s.flushMode}"
            return s
        } catch(Exception x) {
            Session s=sessionFactory.openSession();
            //println "${context}: NEW:   id:${s.hashCode()} flushMode:${s.flushMode}"
            return s;
        }
    }

    @Transactional(readOnly = true)
    List<MyMember> loadMemberReadOnly(String context, HttpServletRequest request, String name) {
        Session s = findSession(context+'service withReadOnlyTxn')   // do we have a session here ?
        println "${context}: service withReadOnlyTxn: id: ${s.hashCode()}, flushMode:${s.flushMode}"// value:${s}"
        List<MyMember> memberList = request.getAttribute("app.member.list")
        if(memberList==null) {
            memberList = new ArrayList<Member>();
            request.setAttribute("app.member.list",memberList)
        }
        MyMember member = MyMember.findByName(name);
        if(member) {
            memberList.add(member)
        }
        return memberList
    }

    @Transactional
    def findLoggedInUser(String context) {
        def user = springSecurityService.currentUser
        //println "${context}: principal: ${springSecurityService.principal}, user: ${user}"
        return user
    }
}
