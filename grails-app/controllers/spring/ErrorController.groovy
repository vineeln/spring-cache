package spring

import javax.el.MethodNotFoundException

class ErrorController {

    def handleError() {
        throw new MethodNotFoundException("from errorController")
    }
}
