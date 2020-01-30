package spring

import groovy.util.logging.Slf4j
import org.hibernate.SessionFactory

import javax.el.MethodNotFoundException

@Slf4j
class ErrorController {
    SessionFactory sessionFactory

    def handleError() {
        //throw new MethodNotFoundException("from errorController")
        log.info("in handleError")
        MyMember m = request.getAttribute("app.current.member")
        log.info("session: ${sessionFactory.currentSession}")
        log.info("is Attached: ${m.isAttached()}");
        render view:"/error", model:[sf:sessionFactory]
    }
}
