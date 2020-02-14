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

    /**
     * http://localhost:8080/home/exceptionInView
         * Default        : Displays Error page
         * Exception   Fix: Displays Error page
         * Dispatcher  Fix: Displays Error page
     * http://localhost:8080/home/exceptionInView?throwExceptionInErrorController=true  //doubleFault hailMary
         * Default        : Infinite loop
         * Exception   Fix: Displays hailMary,
         * Dispatcher  Fix: Displays hailMary
     * http://localhost:8080/home/exceptionInView?throwExceptionInErrorView=true        //doubleFault hailMary
         * Default        : Infinite loop
         * Exception   Fix: Displays Tomcat Servlet error page
         * Dispatcher  Fix: Displays hailMary
     * @return
     */
    def exceptionInView() {
        render view:'/viewWithException'
    }

    /**
     * http://localhost:8080/home/exceptionInController
         * Default        : Displays Error page
         * Exception   Fix: Displays Error page
         * Dispatcher  Fix: Displays Error page
     * http://localhost:8080/home/exceptionInController?throwExceptionInErrorController=true  //doubleFault hailMary
         * Default        : Infinite loop
         * Exception   Fix: Displays hailMary,
         * Dispatcher  Fix: Displays hailMary
     * http://localhost:8080/home/exceptionInController?throwExceptionInErrorView=true        //doubleFault hailMary
         * Default        : Infinite loop
         * Exception   Fix: Displays Tomcat Servlet error page
         * Dispatcher  Fix: Displays hailMary
     * @return
     */
    def exceptionInController() {
        throw new Exception("from Home:exceptionInController");
    }

    /**
     * http://localhost:8080/home/exceptionInControllerWithDomain?throwExceptionInErrorController=true
         * Default        : Infinite loop
         * Exception   Fix: Displays hailMary,
         * Dispatcher  Fix: hailMary
     * http://localhost:8080/home/exceptionInControllerWithDomain?throwExceptionInErrorView=true
         * Default        : Infinite loop
         * Exception   Fix: Displays Tomcat Servlet error page
         * Dispatcher  Fix: hailMary
     * http://localhost:8080/home/exceptionInControllerWithDomain
         * Default        : Displays error.gsp  "NO LAZYLOAD EXCEPTION"
         * Exception   Fix: Displays error.gsp  "NO LAZYLOAD EXCEPTION"
         * Dispatcher  Fix: Displays error.gsp  "NO LAZYLOAD EXCEPTION"
     * @return
     */
    def exceptionInControllerWithDomain() {
        MyMember m = springCacheService.findFirstMember()
        request.setAttribute("app.current.member",m)

        // on error, the error.gsp tries to access "addresses" this should cause exception
        throw new Exception("from Home:actionWithException");
    }

    /**
     * http://localhost:8080/home/exceptionInViewWithDomain?throwExceptionInErrorController=true
         * Default        : Infinite loop
         * Exception   Fix: Displays hailMary,
         * Dispatcher  Fix: hailMary
     * http://localhost:8080/home/exceptionInViewWithDomain?throwExceptionInErrorView=true
         * Default        : Infinite loop
         * Exception   Fix: Displays Tomcat Servlet error page
         * Dispatcher  Fix: hailMary
     * http://localhost:8080/home/exceptionInViewWithDomain
         * Default        : Tomcat error page with LazyLoad exception; would have expected infinite loop
         * Exception   Fix: Tomcat error page with LazyLoad exception
         * Dispatcher  Fix: Displays error.gsp "NO LAZYLOAD EXCEPTION"
     * @return
     */
    def exceptionInViewWithDomain() {
        MyMember m = springCacheService.findFirstMember()
        request.setAttribute("app.current.member",m)

        // on error, the error.gsp tries to access "addresses" this should cause exception
        render view:"/viewWithException", model:[member:m, sf:sessionFactory]
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
