package com.emenar.wal.tsa2.domain.model

/**
 * A EventRow is a row within a level
 */
class EventRow {
    static hasMany = [seats: EventSeat]

    EventLevel level
    VenueRow venueRow
}
