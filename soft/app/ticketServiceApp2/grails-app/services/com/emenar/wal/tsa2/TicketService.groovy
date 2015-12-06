package com.emenar.wal.tsa2

import com.emenar.wal.tsa2.domain.EventBooking
import com.emenar.wal.tsa2.domain.model.PerformanceEvent
import com.emenar.wal.tsa2.domain.model.SeatHold
import com.emenar.wal.tsa2.domain.model.SeatReservation
import grails.transaction.Transactional

@Transactional
class TicketService {
    def sayHi() {
        return "Hi"
    }

    PerformanceEvent findPerformance() {
        return (PerformanceEvent) findPerformanceRaw();
    }

    def findPerformanceRaw() {
        return PerformanceEvent.first();
    }

    def findBooking() {
        return findPerformance().findBooking();
    }

    def numSeatsAvailable(int levelId = 0) {
        def booking = findBooking();
        return booking.countAvailableSeats(levelId);
    }

    def findAndHoldSeats(String customerEmail, int numSeats, int minLevel=0, int maxLevel=0) {
        EventBooking booking = findBooking();
        SeatHold request = new SeatHold(customerEmail: customerEmail, numSeats: numSeats, minLevel: minLevel, maxLevel: maxLevel)
        request.save();
        boolean success = booking.findSeatsFor(request);
        return [
                success: success,
                seatHold: request,
                seatHoldId: request.id
        ];
    }

    def releaseHold(int seatHoldId, String customerEmail) {
        EventBooking booking = findBooking();
        boolean success = booking.releaseHold(seatHoldId, customerEmail);
        return [
                success: success
        ]
    }

    def reserveSeat(int seatHoldId, String customerEmail) {
        EventBooking booking = findBooking();
        SeatReservation reservation = booking.reserveHold(seatHoldId, customerEmail);
        boolean success = null != reservation;
        return [
                success: success,
                reservation: reservation,
                reservationId: reservation.id
        ];
    }


}
