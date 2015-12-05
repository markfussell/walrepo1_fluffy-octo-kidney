package com.emenar.wal.tsa2.domain.model

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(PerformanceEvent)
class PerformanceEventSpec extends Specification implements DomainDataFactory {

	PerformanceEvent domainObject = defaultPeformance()


	def 'a valid domainObject has no errors'() {
		when:
		domainObject.validate()

		then:
		!domainObject.hasErrors()
	}


}
