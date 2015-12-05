package com.emenar.wal.tsa2

import com.emenar.wal.tsa2.domain.EventBooking
import com.emenar.wal.tsa2.domain.model.PerformanceEvent
import com.emenar.wal.tsa2.domain.model.SeatHold
import grails.transaction.Transactional

@Transactional
class TicketService {
    def eventBooking

    PerformanceEvent findPerformance() {
        return (PerformanceEvent) findPerformanceRaw();
    }

    def findPerformanceRaw() {
        return PerformanceEvent.first();
    }


    def findBooking() {
        if (eventBooking) return eventBooking;

        eventBooking = new EventBooking(event: findPerformance());
        eventBooking.prepare();

        return eventBooking;
    }

    def sayHi() {
        return "Hi"
    }

    def numSeatsAvailable(int levelId = 0) {
        def booking = findBooking();
        return levelId
    }

    def findAndHoldSeats(String customerEmail, int numSeats, int minLevel=0, int maxLevel=0) {
        EventBooking booking = findBooking();
        SeatHold request = new SeatHold(customerEmail: customerEmail, numSeats: numSeats, minLevel: minLevel, maxLevel: maxLevel)
        boolean result = booking.findASeatFor(request);
        return result;
    }

    def reserveSeat(int seatHoldId, String customerEmail) {
        EventBooking booking = findBooking();
        booking.reserveHold(seatHoldId, customerEmail);
        return "hi_"+seatHoldId+"_"+customerEmail
    }


}
