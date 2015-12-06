package com.emenar.wal.tsa2.model

import com.emenar.wal.tsa2.TicketService
import com.emenar.wal.tsa2.domain.model.PerformanceEvent
import com.emenar.wal.tsa2.domain.model.SeatHold
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.Specification

/**
 * This basically just confirms the bootstrap process is working properly
 */
@Integration
class PerformanceEvent2Spec extends Specification  {
    def setup() {
    }

    def cleanup() {
    }

    @Ignore("do not need to test this right now")
    void "test event exists"() {
        PerformanceEvent event = PerformanceEvent.first();
        expect:"event exists"
            null != event
    }

}
