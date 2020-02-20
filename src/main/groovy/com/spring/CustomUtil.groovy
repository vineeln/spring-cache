package com.spring;

import org.hibernate.Session;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionStatus;
import spring.MyUser;

public class CustomUtil {
    public static void testSessionInfo(String context) {
        try {
            MyUser.withSession { Session session ->
                MyUser user = MyUser.findByUsername('filteruser')
                println "${context}:              withSession: id:${session.hashCode()}, flushMode: ${session.flushMode}"// value:${session}"
            }
        } catch(Exception x) {
            println "${context}: withSession exception"
        }
        try {
            MyUser.withTransaction { TransactionStatus status ->
                def txm = ((DefaultTransactionStatus)status).getTransaction()
                Session session = txm.sessionHolder.session
                MyUser user = MyUser.findByUsername('filteruser')
                println "${context}: withTransaction: success: id:${session.hashCode()}, flushMode: ${session.flushMode}"// value:${session}"
            }
        } catch(Exception x) {
            x.printStackTrace()
            println "${context}: withTransaction exception"
        }
        try {
            MyUser.withNewSession { Session session ->
                MyUser user = MyUser.findByUsername('filteruser')
                println "${context}:           withNewSession: id:${session.hashCode()}, flushMode: ${session.flushMode}"// value:${session}"
            }
        } catch(Exception x) {
            println "${context}: withSession exception"
        }

    }

    public static void isAttached( String context, Object domain ) {
        if( domain != null ) {
            try {
                MyUser.withSession { Session session ->
                    println "${context}:              withSession: id:${session.hashCode()}, flushMode: ${session.flushMode}, domain:${domain.class.name}, isAttached:${session.contains(domain)}"// value:${session}"
                }
                return
            } catch(Exception x) {
                println "${context}: withSession exception: while checking isAttached:${domain.class.name}"
            }
            MyUser.withNewSession { Session session ->
                println "${context}:           withNewSession: id:${session.hashCode()}, flushMode: ${session.flushMode}, domain:${domain.class.name}, isAttached:${session.contains(domain)}"// value:${session}"
            }
        } else {
            println "${context}: null domain"
        }
    }
}
