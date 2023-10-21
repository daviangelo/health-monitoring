package com.lessa.healthmonitoring

import spock.lang.Specification

class MonitoringSpec extends Specification {

    def "after sum 1 + 1 the result should be 2"() {

        when:
        var result = 1 + 1

        then:
        result == 2
    }
}
