package com.emenar.wal.tsa2

/**
 * A VenueLevel is a level and its associated information at a particular venue.
 *
 * Currently this is just a 'constant' name, but it could represent different
 * default preferences of what customers would like about the level (front and center,
 * middle and center, etc.)
 */
class VenueLevel {
    static hasMany = [seats: VenueSeat]

    PerformingVenue venue

    int levelId
    String levelName

    int rows
    int seatsInRow //Assuming all rows are equal in length
}
