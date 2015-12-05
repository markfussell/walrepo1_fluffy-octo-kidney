package com.emenar.wal.tsa2

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
class TicketService2Spec extends Specification  {
    @Autowired
    TicketService ticketService

    /**
     * Conventional name for compatibility
     */
    TicketService service

    def setup() {
          service = ticketService
    }

    def cleanup() {
    }

    void "test ServiceExists"() {
        expect:"ServiceExists"
            "Hi" == service.sayHi()
    }

    @Ignore("do not need to test this right now")
    void "test findPerformanceRaw"() {
        expect:"findPerformanceRaw"
            "Hi" == service.findPerformanceRaw()
    }

    @Ignore("do not need to test this right now")
    def "test numSeatsAvailable with level #level"() {
        given:
        def numberOfSeats = service.numSeatsAvailable(level)

        expect: "Matches the level"
        numberOfSeats == level

        where:
        level || error
        1     || null
    }

    @Ignore("do not need to test this right now")
    def "test findAndHoldSeats with email #email"() {
        given:
        SeatHold hold = service.findAndHoldSeats(email, 2)

        expect: "The hold is always successful"
        hold.customerEmail == email

        where:
        email                      || error
        'mark.fussell@emenar.com'  || null
    }

    @Ignore("do not need to test this right now")
    def "test reserveSeat with id #id email #email"() {
        given:
        String confirmationCode = service.reserveSeat(id, email)

        expect: "The hold is always successful"
        confirmationCode == "hi_"+id+"_"+email

        where:
        id | email                      || error
        1  | 'mark.fussell@emenar.com'  || null
    }


}
