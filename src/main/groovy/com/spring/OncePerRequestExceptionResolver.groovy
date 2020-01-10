package com.spring

import grails.web.mapping.UrlMappingInfo
import grails.web.mapping.UrlMappingsHolder
import groovy.util.logging.Slf4j
import org.grails.web.errors.GrailsExceptionResolver
import org.grails.web.mapping.DefaultUrlMappingInfo
import org.grails.web.util.WebUtils
import org.springframework.web.servlet.ModelAndView

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Slf4j
class OncePerRequestExceptionResolver extends GrailsExceptionResolver {

    protected static final String PROCESSING_EXCEPTION = "grails.exception.processing"
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                         Object handler, Exception ex) {
        String reqURI = request.getRequestURI();
        String forwardURI = request.getAttribute(WebUtils.FORWARD_REQUEST_URI_ATTRIBUTE)

        log.info("processing: ${reqURI}, originalURI: ${forwardURI}")

        def processingException = request.getAttribute(PROCESSING_EXCEPTION)
        if(!processingException) {
            request.setAttribute(PROCESSING_EXCEPTION,true)
            return super.resolveException(request,response,handler,ex)
        } else {
            log.error ("error while processing ${reqURI}: exception:: ${getRequestLogMessage(ex, request)}", ex )
            log.info ("attempting to render view: /hailMary");
            ModelAndView mv = new ModelAndView("/hailMary");
            return mv;
        }
    }

    @Override
    protected void forwardRequest(UrlMappingInfo info, HttpServletRequest request, HttpServletResponse response,
                                  ModelAndView mv, String uri) throws ServletException, IOException {
        def debugInfo = info.getUrlData() ? info.toString() : "uri:${info.getURI()},controller:${info.controllerName},action:${info.actionName}"
        log.info("forwarding for uri:${uri} with info: [ ${debugInfo} ]")
        super.forwardRequest(info,request,response,mv,uri)
    }
}
