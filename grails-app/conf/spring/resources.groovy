import com.spring.CustomDispatcherServlet
import com.spring.OncePerRequestExceptionResolver
import grails.config.Settings
import org.grails.web.servlet.mvc.GrailsDispatcherServlet
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.util.ClassUtils

// Place your Spring DSL code here
beans = {
    exceptionHandler(OncePerRequestExceptionResolver) {
        exceptionMappings = ['java.lang.Exception': '/error']
    }

    def application = grailsApplication
    def config = application.config

    boolean isTomcat = ClassUtils.isPresent("org.apache.catalina.startup.Tomcat", application.classLoader)
    String grailsServletPath = config.getProperty(Settings.WEB_SERVLET_PATH, isTomcat ? Settings.DEFAULT_TOMCAT_SERVLET_PATH : Settings.DEFAULT_WEB_SERVLET_PATH)

    // add the dispatcher servlet
    dispatcherServlet(CustomDispatcherServlet)
    dispatcherServletRegistration(ServletRegistrationBean, ref("dispatcherServlet"), grailsServletPath) {
        loadOnStartup = 2
        asyncSupported = true
        multipartConfig = multipartConfigElement
    }
}
