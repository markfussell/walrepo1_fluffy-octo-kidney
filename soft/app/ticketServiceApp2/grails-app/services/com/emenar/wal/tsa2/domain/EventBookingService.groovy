package com.emenar.wal.tsa2.domain

import com.emenar.wal.tsa2.domain.model.PerformanceEvent

/**
 * The EventBooking service is the non-persistent behavior associated with booking
 *
 * I actually dislike this schism, but it has become fairly 'normal' since automated container restart and
 * data model migration
 */
class EventBookingService {
    def seatCursorService;

    EventBooking createBookingForEvent(PerformanceEvent event) {
        return new EventBooking(event: event);
    }


}
