import com.spring.CustomAnonymousAuthenticationFilter
import com.spring.CustomOpenSessionInViewFilter
import com.spring.CustomSimpleFilter
import grails.plugin.springsecurity.BeanTypeResolver
import grails.plugin.springsecurity.web.filter.GrailsAnonymousAuthenticationFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.core.Ordered
import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter
import spring.MyUserPasswordEncoderListener
import com.spring.CustomDispatcherServlet
import com.spring.OncePerRequestExceptionResolver
import grails.config.Settings
import org.grails.web.servlet.mvc.GrailsDispatcherServlet
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.util.ClassUtils

import javax.servlet.DispatcherType

// Place your Spring DSL code here
beans = {
    myUserPasswordEncoderListener(MyUserPasswordEncoderListener)

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


    def conf = config.grails.plugin.springsecurity
//    Class beanTypeResolverClass = conf.beanTypeResolverClass ?: BeanTypeResolver
//    BeanTypeResolver beanTypeResolver = (BeanTypeResolver)BeanTypeResolver.newInstance(conf, grailsApplication)

    // filter after remember me..
    anonymousAuthenticationFilter(CustomAnonymousAuthenticationFilter) { bean ->
        bean.autowire = "byName"
        authenticationDetailsSource = ref('authenticationDetailsSource')
        sessionFactory = ref('sessionFactory')
        key = conf.anon.key
    }

    //OSIV servlet filter
    openSessionInViewServletFilter(CustomOpenSessionInViewFilter) { bean->
        bean.autowire="byName"
    }
    openSessionInViewServletFilterRegistrionBean(FilterRegistrationBean) {
        filter = ref('openSessionInViewServletFilter')
        urlPatterns = ['/*']
        dispatcherTypes = EnumSet.of(DispatcherType.ERROR, DispatcherType.REQUEST)

        // since Spring FilterChain was 100, this should run before Spring FilterChain
        order = Ordered.HIGHEST_PRECEDENCE + 99
    }


    //simple helper filter
    simpleCustomFilter(CustomSimpleFilter) { bean ->
        bean.autowire="byName"
    }

    customFilterServletFilterRegistrionBean(FilterRegistrationBean) {
        filter = ref('simpleCustomFilter')
        urlPatterns = ['/*']
        dispatcherTypes = EnumSet.of(DispatcherType.ERROR, DispatcherType.REQUEST)

        order = Ordered.HIGHEST_PRECEDENCE + 98 // since Spring FilterChain was 100
    }


}

