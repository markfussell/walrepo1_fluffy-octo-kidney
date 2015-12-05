package com.emenar.wal.tsa2.domain.model

/**
 * A PerformingVenue is a venue that this ticketing system can
 * reserve tickets for.  It represents the venue but not a particular performance
 * of that venue.  In general, the system would have:
 *   * Multiple Performances at Multiple Venues
 * but for this particular problem, the cardinalities happen to both be "one"
 */
class PerformingVenue {
    String venueName

    static hasMany = [levels: VenueLevel]

}
