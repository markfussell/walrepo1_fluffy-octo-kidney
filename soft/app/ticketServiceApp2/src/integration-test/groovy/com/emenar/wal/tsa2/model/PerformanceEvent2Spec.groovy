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
 */
@Integration
@Rollback
class PerformanceEvent2Spec extends Specification  {
    PerformanceEvent event

    def setup() {
          event = PerformanceEvent.first();
    }

    def cleanup() {
    }

    void "test event exists"() {
        expect:"event exists"
            null != event
    }

}
