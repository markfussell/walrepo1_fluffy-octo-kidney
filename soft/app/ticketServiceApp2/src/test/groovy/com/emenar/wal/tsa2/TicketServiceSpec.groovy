package com.emenar.wal.tsa2

import com.emenar.wal.tsa2.domain.EventBookingService
import com.emenar.wal.tsa2.domain.model.DomainDataFactory
import com.emenar.wal.tsa2.domain.model.PerformanceEvent
import com.emenar.wal.tsa2.domain.model.SeatHold
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Ignore
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(TicketService)
@Mock([PerformanceEvent])
class TicketServiceSpec extends Specification implements DomainDataFactory {

    def setup() {
//        def eventBookingService = Mock(EventBookingService)
//        service.eventBookingService = eventBookingService
    }

    def cleanup() {
    }

    void "test ServiceExists"() {
        expect:"ServiceExists"
        "Hi" == service.sayHi()
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

        expect: "The hold is always returned"
            hold.customerEmail == email

        where:
        email                      || error
        'mark.fussell@emenar.com'  || null
    }

    @Ignore("do not need to test this right now")
    def "test reserveSeat with id #id email #email"() {
        given:
        String confirmationCode = service.reserveSeat(id, email)

        expect: "The reserve is always successful"
          confirmationCode == "hi_"+id+"_"+email

        where:
        id | email                      || error
        1  | 'mark.fussell@emenar.com'  || null
    }


}
