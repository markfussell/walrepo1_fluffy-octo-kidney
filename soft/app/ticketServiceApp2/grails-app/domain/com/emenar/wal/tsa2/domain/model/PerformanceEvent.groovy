package com.emenar.wal.tsa2.domain.model

import com.emenar.wal.tsa2.domain.EventBooking
import groovy.transform.Synchronized

/**
 * A PerformanceEvent is a performance at a particular venue and
 * includes the specifics of the performance.  It has
 * a static component of the prices and seats, and a dynamic
 * component of the 'Booking' process which gives customers
 * a 'hold' on a block of seats and then lets them reserve
 * those seats within a limited amount of time.
 */
class PerformanceEvent {
    static transients = ['eventBooking']

    Date performanceDate

    PerformingVenue venue
    transient EventBooking eventBooking

    @Synchronized
    EventBooking findBooking() {
        if (eventBooking != null) return eventBooking;

        eventBooking = new EventBooking(event: this);
        eventBooking.prepare();

        return eventBooking;
    }


    static hasMany = [levels: EventLevel]
}
