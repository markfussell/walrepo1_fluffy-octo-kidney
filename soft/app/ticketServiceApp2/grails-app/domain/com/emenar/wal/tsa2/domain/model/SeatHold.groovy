package com.emenar.wal.tsa2.domain.model

import com.emenar.wal.tsa2.domain.SeatCursor
import com.emenar.wal.tsa2.domain.model.EventSeat
import com.emenar.wal.tsa2.domain.model.SeatReservation

/**
 * A SeatHold represents a customer requesting to book a particular set of seats
 * at a venue before they formally reserve them.  A SeatHold expires
 * (becomes invalid) if not reserved within a particular amount
 * of time from the hold.
 *
 * Basically a SeatHold starts as a request, moves into an 'offer', and then the offer expires or the reservation
 * is made
 */
class SeatHold {
    static constraints = {
        customerEmail blank: false

        reservation nullable: true
        reservationDate nullable: true
        firstSeat nullable: true
        holdDate nullable: true
    }
    static transients = ['seatCursor']

    public boolean isSatisfied() {
        return firstSeat != null
    }

    public boolean isReserved() {
        return reservation !=null
    }

    String customerEmail;
    int numSeats;

    int minLevel;
    int maxLevel;

    /**
     * The cursor that gave out the seats
     */
    transient SeatCursor seatCursor
    EventSeat firstSeat
    Date holdDate

    SeatReservation reservation = null
    Date reservationDate
}
