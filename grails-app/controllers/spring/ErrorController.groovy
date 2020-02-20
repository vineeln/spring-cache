package spring

import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j
import org.hibernate.SessionFactory
import org.springframework.security.web.access.ExceptionTranslationFilter
import org.springframework.security.web.util.ThrowableAnalyzer

import javax.el.MethodNotFoundException
import javax.servlet.RequestDispatcher

@Slf4j
@Secured('permitAll')
class ErrorController {
    SessionFactory sessionFactory

    private ThrowableAnalyzer throwableAnalyzer = new ExceptionTranslationFilter.DefaultThrowableAnalyzer();

    @Secured('IS_AUTHENTICATED_ANONYMOUSLY')
    def handleError() {
        Throwable t = request.exception ?: request.getAttribute(RequestDispatcher.ERROR_EXCEPTION)

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
