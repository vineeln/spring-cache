package spring

import com.spring.CustomUtil
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.DefaultTransactionStatus

// while processing an error
//   : exception in view displays "Tomcat Internal error"
//   : exception in a controller:
//       will go thru this flow again & causes a recursion
@Slf4j
@Secured('permitAll')
class HomeController {

    SessionFactory sessionFactory
    SpringCacheService springCacheService


    def verifyLazy() {
        Session currSession, newSession
        MyMember m1 = MyMember.findByName("lazy1")
        MyMember m2
        MyMember.withSession { Session s -> currSession=s }
        println "curr session: ${currSession}"
        MyMember.withNewSession { Session s ->
            newSession=s
            m2 = MyMember.findByName("lazy2")
            println "new session:${newSession}"
        }
        println "new session: ${newSession}"


        println "m1 ${m1.id},address: ${m1.addresses}"
        m2.attach()
        println "m2 ${m2.id} address: ${m2.addresses}"

        render view:'/index', model:[sf:sessionFactory,member:m2,memberList:[]]
    }

    @Secured('ROLE_USER')
    def index() {
        println "In Controller ++++ "
        String context="controller"
        MyUser.findByUsername('controlleruser')
        MyMember m = MyMember.findByName("controllerMember")
        m.isAttached()
        CustomUtil.testSessionInfo(context)
        springCacheService.logSessionNoTxn(context)
        List<MyMember> memberList = springCacheService.loadMemberReadOnly(context, request,"controllerMember")
        CustomUtil.testSessionInfo(context)
        springCacheService.logSessionWithTransaction(context)
        CustomUtil.testSessionInfo(context)
        def user = springCacheService.findLoggedInUser(context)
        CustomUtil.testSessionInfo(context)
        CustomUtil.isAttached(context,user)
        println "In Controller ++++ "
        println ""
        render view:'/index', model:[sf:sessionFactory,member:m,memberList:memberList]
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


    void testSessionInfo() {

        try {
            MyUser.withSession { Session session ->
                MyUser user = MyUser.findByUsername('filteruser')
                println "controller: withSession: id:${session.hashCode()}, flushMode: ${session.flushMode}"// value:${session}"
            }
        } catch(Exception x) {
            println "controller: withSession exception"
        }
        try {
            MyUser.withNewSession { Session session ->
                MyUser user = MyUser.findByUsername('filteruser')
                println "controller: withNewSession: id:${session.hashCode()}, flushMode: ${session.flushMode}"// value:${session}"
            }
        } catch(Exception x) {
            println "controller: withSession exception"
        }
        try {
            MyUser.withTransaction { TransactionStatus status ->
                def txm = ((DefaultTransactionStatus)status).getTransaction()
                Session session = txm.sessionHolder.session
                MyUser user = MyUser.findByUsername('filteruser')
                println "controller: withTransaction: success: id:${session.hashCode()}, flushMode: ${session.flushMode}"// value:${session}"
            }
        } catch(Exception x) {
            println "controller: withTransaction exception"
        }

    }

}
