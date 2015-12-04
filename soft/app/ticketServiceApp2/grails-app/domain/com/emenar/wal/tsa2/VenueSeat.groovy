package com.emenar.wal.tsa2

/**
 * A VenueSeat is a specific seat at a particular venue.  These are not
 * 'holdable' in and of themselves, but represent the physical layout of the venue.
 *
 * For the example: four levels of 15 to 25 rows with 50 to 100 seats per row
 *
 */
class VenueSeat {
    int levelRow
    int rowSeatNumber

    PerformingVenue venue
    VenueLevel venueLevel
}
