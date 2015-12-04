package com.emenar.wal.tsa2

/**
 * A EventLevel is a level at a particular performance and its
 * associated information (e.g. ticket price)
 *
 */
class EventLevel {
    static hasMany = [seats: EventSeat]

    PerformanceEvent event
    VenueLevel level

    int price
}
