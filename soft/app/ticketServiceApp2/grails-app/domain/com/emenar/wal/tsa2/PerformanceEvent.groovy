package com.emenar.wal.tsa2

/**
 * A PerformanceEvent is a performance at a particular venue and
 * includes the specifics of the performance
 */
class PerformanceEvent {
    Date performanceDate

    PerformingVenue venue

    static hasMany = [levels: EventLevel, seats: EventSeat, availableSeats: EventSeat]
}
