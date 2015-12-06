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

    def "test numSeatsAvailable with level #level is #seats"() {
        given:
        def numberOfSeats = service.numSeatsAvailable(level)

        expect: "Matches the level"
        numberOfSeats == seats

        where:
        level | seats || error
        0     | 6250  || null
        1     | 1250  || null
        2     | 2000  || null
        3     | 1500  || null
        4     | 1500  || null
    }

    @Ignore("do not need to test this right now")
    def "test findAndHoldSeats for #email number #holdSeats holdLevel #holdLevel has level #level with seats #seats"() {
        given:
        def result  = service.findAndHoldSeats(email, holdSeats, holdLevel, holdLevel)
        def numberOfSeats = service.numSeatsAvailable(level)
        SeatHold hold = result.seatHold;

        expect: "The hold object creation is always successful"
        hold.customerEmail == email
        result.success == true
        numberOfSeats == seats

        where:
        email                      | holdSeats | holdLevel || level | seats | error
        'mark.fussell@emenar.com'  | 10        | 0         || 1     | 1240  | null
    }

    def "test find and release for #email number #holdSeats holdLevel #holdLevel has level #level with seats #seats"() {
        given:
        def result  = service.findAndHoldSeats(email, holdSeats, holdLevel, holdLevel)
        SeatHold hold = result.seatHold;

        expect: "The hold  is successful"
        hold.customerEmail == email
        result.success == true

        when: "We release"
        int holdId = result.seatHoldId;
        def result2  = service.releaseHold(holdId, email)
        def numberOfSeats = service.numSeatsAvailable(level)

        then: "The release is successful"
        result2.success == true
        numberOfSeats == seats

        where:
        email                      | holdSeats | holdLevel || level | seats | error
        'mark.fussell@emenar.com'  | 10        | 0         || 1     | 1250  | null
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
