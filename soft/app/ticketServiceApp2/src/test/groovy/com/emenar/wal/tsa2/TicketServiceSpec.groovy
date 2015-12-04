package com.emenar.wal.tsa2

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(TicketService)
class TicketServiceSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    def "test numSeatsAvailable"() {
        given:
        def numberOfSeats = service.numSeatsAvailable()

        expect: "Always 1"
            numberOfSeats == 1
    }

    def "test findAndHoldSeats with email #email"() {
        given:
        SeatHold hold = service.findAndHoldSeats(email, 2)

        expect: "The hold is always successful"
            hold.customerEmail == email

        where:
        email                      || error
        'mark.fussell@emenar.com'  || null
    }

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
