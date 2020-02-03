package com.spring

import javax.el.MethodNotFoundException

class MySpringTagLib {
    static defaultEncodeAs = [taglib:'html']
    static namespace = "myspring"

    def renderEx = { attrs ->
        throw new ClassNotFoundException("from renderEx")
    }
}
