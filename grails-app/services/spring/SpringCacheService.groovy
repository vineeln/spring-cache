package spring

import grails.gorm.transactions.Transactional
import groovy.util.logging.Log4j
import org.hibernate.SessionFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

//@Transactional
@Log4j
@Component
class SpringCacheService {

    SessionFactory sessionFactory

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

    def createMember() {
        MyMember m = new MyMember(name:"somename");
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
}
