package com.spring

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class MySpringTagLibSpec extends Specification implements TagLibUnitTest<MySpringTagLib> {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        expect:"fix me"
            true == false
    }
}
