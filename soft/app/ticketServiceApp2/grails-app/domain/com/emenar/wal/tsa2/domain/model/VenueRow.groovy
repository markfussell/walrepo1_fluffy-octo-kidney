package com.emenar.wal.tsa2.domain.model

/**
 * A VenueLevel is a level and its associated information at a particular venue.
 *
 * Currently this is just a 'constant' name, but it could represent different
 * default preferences of what customers would like about the level (front and center,
 * middle and center, etc.)
 */
class VenueRow {
    static hasMany = [seats: VenueSeat]

    VenueLevel level

    int rowId
    int seatsInRow
}
