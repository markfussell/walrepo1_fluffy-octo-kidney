package com.emenar.wal.tsa2.domain.model

/**
 * A EventLevel is a level at a particular performance and its
 * associated information (e.g. ticket price)
 *
 */
class EventLevel {
    static hasMany = [rows: EventRow]

    PerformanceEvent event
    VenueLevel venueLevel

    int price
}
