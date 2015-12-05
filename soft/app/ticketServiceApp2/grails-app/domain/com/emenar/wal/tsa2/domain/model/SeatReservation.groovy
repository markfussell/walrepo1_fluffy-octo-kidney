package com.emenar.wal.tsa2.domain.model
/**
 * A SeatReservation is a reserved (e.g. paid or committed) set of
 * seats.  Given it is reserved, we can now expand out the seats
 * (or not) and detangle from the SeatCursor.
 */
class SeatReservation {
    static hasMany = [seats: EventSeat]

    SeatHold hold
}
