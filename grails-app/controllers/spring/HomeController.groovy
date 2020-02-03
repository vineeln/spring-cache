package spring

import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import org.hibernate.Session
import org.hibernate.SessionFactory

// while processing an error
//   : exception in view displays "Tomcat Internal error"
//   : exception in a controller:
//       will go thru this flow again & causes a recursion
@Slf4j
class HomeController {

    SessionFactory sessionFactory
    SpringCacheService springCacheService

    def index() {
        render view:'/index'
    }

    def invalidView() {
        render view:'/someinvalidview'
    }

    def viewWithException() {
        render view:'/viewWithException'
    }

    def errorAction() {
        throw new ClassNotFoundException("from Home:errorAction");
    }

    def viewWithMember() {
        Session session = sessionFactory.currentSession

        MyMember m = springCacheService.findFirstMember()
        request.setAttribute("app.current.member",m)

        log.info("session: ${session}")
        log.info("retrieved member")
        render view:'/index', model: [member:m, sf:sessionFactory]
        //forward(controller:'error',action:'handleError')
    }

    // the following should not be persisted, as it doesn't get flushed
    // http://localhost:8080/home/domainEditWithoutFlush?name=xyz
    def domainEditWithoutFlush() {
        MyMember m = springCacheService.findMember("somename")
        request.setAttribute("app.current.member",m)
        m.setName(params.name)
        m.save(failOnError:true)  // this doesn't get committed
        render view:'/index', model: [member:m]
    }

    // the following should get comitted in DB
    // http://localhost:8080/home/domainEditWithFlush?name=xyz
    def domainEditWithFlush() {
        MyMember m = springCacheService.findMember("somename")
        request.setAttribute("app.current.member",m)
        m.setName(params.name)
        m.save(failOnError:true,flush:true)  // this gets comitted since its AutoCommit mode
        render view:'/index', model: [member:m]
    }

    // with logSQL turned on we should see two update statements
    def domainEditVerifyDirtyCheck() {
        MyMember m = springCacheService.findFirstMember()
        request.setAttribute("app.current.member",m)

        m.setName(params.name)
        log.info("is Dirty: ${m.isDirty()}");
        springCacheService.dummyTransactionMethod();  // this should flush & commit member

        m.setName(params.name+params.name)
        log.info("is Dirty: ${m.isDirty()}");
        springCacheService.dummyTransactionMethod();  // this should flush & commit member
        render view:'/index', model: [member:m]
    }

    // this should update only once at the end
    @Transactional
    def domainEditVerifyDirtyCheckWithTransactiona() {
        MyMember m = springCacheService.findFirstMember()
        request.setAttribute("app.current.member",m)

        m.setName(params.name)
        log.info("is Dirty: ${m.isDirty()}");
        springCacheService.dummyTransactionMethod();  // this should flush & commit member

        m.setName(params.name+params.name)
        log.info("is Dirty: ${m.isDirty()}");
        springCacheService.dummyTransactionMethod();  // this should flush & commit member
        render view:'/index', model: [member:m]
    }

}
