package spring

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

        MyMember m = springCacheService.findMember("somename")
        request.setAttribute("app.current.member",m)

        log.info("session: ${session}")
        log.info("retrieved member")
        render view:'/index', model: [member:m, sf:sessionFactory]
        //forward(controller:'error',action:'handleError')
    }
}
