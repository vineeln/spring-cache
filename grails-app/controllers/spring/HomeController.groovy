package spring

// while processing an error
//   : exception in view displays "Tomcat Internal error"
//   : exception in a controller:
//       will go thru this flow again & causes a recursion
class HomeController {

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
}
