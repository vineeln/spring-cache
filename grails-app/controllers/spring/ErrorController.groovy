package spring

import groovy.util.logging.Slf4j

import javax.el.MethodNotFoundException

@Slf4j
class ErrorController {

    def handleError() {
        //throw new MethodNotFoundException("from errorController")
        log.info("in handleError")
        MyMember m = request.getAttribute("app.current.member")
        log.info("is Attached: ${m.isAttached()}");
        render view:"/error"
    }
}
