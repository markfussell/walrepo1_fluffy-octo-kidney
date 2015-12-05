package com.emenar.wal.tsa2.domain

import com.emenar.wal.tsa2.domain.model.DomainDataFactory
import com.emenar.wal.tsa2.domain.model.PerformanceEvent
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(DomainLoaderService)
class DomainLoaderServiceSpec extends Specification  {


	void "test ServiceExists"() {
		expect:"ServiceExists"
		"Hi" == service.sayHi()
	}


//	void "test handleInit"() {
//		expect:"handleInit works"
//		service.handleInit()
//	}


}
