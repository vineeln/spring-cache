package com.spring

import grails.plugin.springsecurity.web.filter.GrailsAnonymousAuthenticationFilter
import org.hibernate.Session
import org.hibernate.SessionFactory
import spring.MyUser
import spring.SpringCacheService

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

class CustomAnonymousAuthenticationFilter extends GrailsAnonymousAuthenticationFilter {

    SessionFactory sessionFactory
    SpringCacheService springCacheService

    @Override
    void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        /*
        //Q. do we have a session here ?
        MyUser.withSession {
            // Q. do we have a session here ?  ANS: exception will not enter into closure.
        }
        MyUser.withTransaction {
            // Q. do we have a session here ?  ANS: Yes & will use currentSession if exists, else creates newSession
            // Q. what happens the domains loaded here & used outside the scope ??  ANS: ok if a session already esists..
        }
        MyUser.withNewSession {
            // Q. do we have a session here ?
            // Q. what happens to the domains loaded here & used outside the scope ??  ANS: LazyLoad
        }
        */
        println "In Filter: ------"
        String context="filter"
        //findSession()
        CustomUtil.testSessionInfo(context)
        springCacheService.loadMemberReadOnly(context,req,"filterMember")
        springCacheService.logSessionNoTxn(context)
        springCacheService.logSessionWithTransaction(context)
        MyUser user = springCacheService.findLoggedInUser(context)  //@Transactional.  Q1. does the session exist before the call ??
        CustomUtil.isAttached(context,user)                         //is the user domain attached to session
        try {
            println "${context}: Trying to attach"
            user.attach()
            println "${context}: Trying to attach: successful"
        } catch(Exception x) {
            println "${context}: unable to attach: ${x.toString()}"
        }
        CustomUtil.testSessionInfo(context)
        println "In Filter: -------"

        super.doFilter(req,res,chain)
    }

    Session findSession() {
        try {
            Session s = sessionFactory.currentSession
            //println "filter: session found: flushMode:${s.flushMode}"
            return s;
        } catch(Exception x) {
            Session s = sessionFactory.openSession();
            //println "filter: session NOT found: creating NEW one, flushMode: ${s.flushMode}"
            return s
        }
    }
}
