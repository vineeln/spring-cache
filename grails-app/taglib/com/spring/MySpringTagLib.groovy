package com.spring

import spring.MyMember

import javax.el.MethodNotFoundException

class MySpringTagLib {
    //static defaultEncodeAs = [taglib:'html']
    static namespace = "myspring"

    def renderEx = { attrs ->
        throw new ClassNotFoundException("from renderEx")
    }

    def renderMember = { attrs ->

        MyMember m = attrs.member;
        out << "<li>Name: ${m.name}</li>"
        try{
            out << "<li>address: ${m.addresses}</li>"
        }catch(Exception x) {
            out << "<li>address: ${x.toString()}</li>"
        }catch(Throwable t) {
            out << "<li>address: ${t.toString()}</li>"
        }


    }
}
