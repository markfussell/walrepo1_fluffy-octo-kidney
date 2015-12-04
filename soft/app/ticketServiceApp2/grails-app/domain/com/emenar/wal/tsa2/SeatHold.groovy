package com.emenar.wal.tsa2

/**
 * A SeatHold represents a customer booking a particular set of seats
 * at a venue
 */
class SeatHold {
    static constraints = {
        customerEmail blank: false
    }

    static hasMany = [seats: EventSeat]

    /**
     * A gukey is a globally unique key that allows cross-persistence unique identification
     */
    String gukey;

    /**
     * A primary key for the seatHold
     */
    int seatHoldId;

    String customerEmail;
    int numSeats;

    int minLevel;
    int maxLevel;
}
