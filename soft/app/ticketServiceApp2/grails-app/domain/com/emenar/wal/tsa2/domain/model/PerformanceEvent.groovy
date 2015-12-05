package com.emenar.wal.tsa2.domain.model

/**
 * A PerformanceEvent is a performance at a particular venue and
 * includes the specifics of the performance.  It has
 * a static component of the prices and seats, and a dynamic
 * component of the 'Booking' process which gives customers
 * a 'hold' on a block of seats and then lets them reserve
 * those seats within a limited amount of time.
 */
class PerformanceEvent {
    Date performanceDate

    PerformingVenue venue

    static hasMany = [levels: EventLevel]
}
