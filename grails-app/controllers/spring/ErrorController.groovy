package spring

import groovy.util.logging.Slf4j
import org.hibernate.SessionFactory

import javax.el.MethodNotFoundException

@Slf4j
class ErrorController {
    SessionFactory sessionFactory

    def handleError() {
        if( params.throwExceptionInErrorController ) {
            throw new MethodNotFoundException("from errorController")
        }
        MyMember m = request.getAttribute("app.current.member")
        if( params.throwExceptionInErrorView != null ) {
            render view:"/errorWithException", model:[member:m, sf:sessionFactory]
        } else {
            render view:"/error", model:[member:m, sf:sessionFactory]
        }
//        log.info("in handleError")
//        log.info("session: ${sessionFactory.currentSession}")
//        log.info("is Attached: ${m.isAttached()}");
//        m.attach()
//        log.info("is Attached: ${m.isAttached()}");
//        render view:"/error", model:[sf:sessionFactory]
    }
}
