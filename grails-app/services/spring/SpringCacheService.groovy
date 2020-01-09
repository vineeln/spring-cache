package spring

import grails.gorm.transactions.Transactional
import groovy.util.logging.Log4j
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.annotation.ComponentScan
import org.springframework.stereotype.Component

@Transactional
@Log4j
@Component
class SpringCacheService {

    def serviceMethod() {
        //log.info "In Service Method"
        println "In Service Method"
        "did something"
    }

    @Cacheable(value = "addresses", key = "#customer")
    def findCustomerName(String customer) {
        println "In findCustomerName"
        return "${customer} Name"
    }
}
