package com.emenar.wal.tsa2.domain.model
/**
 * A EventSeat is a specific seat at a particular venue during a particular performance.
 * PerformanceSeats intersect a performance (a start and end time at a venue) with a seat at
 * that venue.  Only one person can be in a seat (unticketed lap-children exempted) at any given time
 * and for this problem, you can not timeshare a performance seat.
 *
 * If a 'SeatHold' is null then the seat is unclaimed.  At scale, the real issue is
 * matching seats to holds with:
 *
 *   * Minimal collision
 *   * Maximum success
 *
 * Or basically getting maximum throughput of the reservation process
 */
class EventSeat {
    static constraints = {
        seatHold nullable: true
    }

    EventSeat findSeatInRowOffset(int offset) {
        return this;
    }

    EventRow row
    VenueSeat venueSeat

    SeatHold seatHold
}
