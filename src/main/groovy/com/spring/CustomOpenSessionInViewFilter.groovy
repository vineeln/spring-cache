package com.spring

import org.hibernate.FlushMode
import org.hibernate.HibernateException
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.dao.DataAccessResourceFailureException
import org.springframework.orm.hibernate5.SessionFactoryUtils
import org.springframework.orm.hibernate5.SessionHolder
import org.springframework.orm.hibernate5.support.AsyncRequestInterceptor
import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.web.context.request.async.WebAsyncManager
import org.springframework.web.context.request.async.WebAsyncUtils
import spring.SpringCacheService

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CustomOpenSessionInViewFilter extends OpenSessionInViewFilter{

    FlushMode defaultFlushMode

    protected Session openSession(SessionFactory sessionFactory) throws DataAccessResourceFailureException {
        try {
            Session session = sessionFactory.openSession();
            session.setFlushMode( defaultFlushMode ?: FlushMode.COMMIT);
            return session;
        }
        catch (HibernateException ex) {
            throw new DataAccessResourceFailureException("Could not open Hibernate Session", ex);
        }
    }
}
