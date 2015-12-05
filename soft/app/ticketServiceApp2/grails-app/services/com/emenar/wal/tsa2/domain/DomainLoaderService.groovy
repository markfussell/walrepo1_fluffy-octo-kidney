package com.emenar.wal.tsa2.domain

import com.emenar.wal.tsa2.domain.model.EventLevel
import com.emenar.wal.tsa2.domain.model.EventRow
import com.emenar.wal.tsa2.domain.model.EventSeat
import com.emenar.wal.tsa2.domain.model.PerformanceEvent
import com.emenar.wal.tsa2.domain.model.PerformingVenue
import com.emenar.wal.tsa2.domain.model.VenueLevel
import com.emenar.wal.tsa2.domain.model.VenueRow
import com.emenar.wal.tsa2.domain.model.VenueSeat

class DomainLoaderService {
    static transactional = false;

    def sayHi() {
        return "Hi"
    }

    def levelArray = [
                    [name: 'Orchestra', rows: 25, seats: 50, price: 100],
                    [name: 'Main', rows: 20, seats: 100, price: 75],
                    [name: 'Balcony 1', rows: 15, seats: 100, price: 50],
                    [name: 'Balcony 2', rows: 15, seats: 100, price: 40]
            ];

    def handleInit() {
        PerformingVenue venue = new PerformingVenue(venueName: "TheVenue")
        venue.save();
        PerformanceEvent event = new PerformanceEvent(venue: venue, performanceDate: new Date())
        event.save();


        levelArray.each { eachLevel ->
            VenueLevel venueLevel = new VenueLevel(venue: venue);
            venueLevel.levelName = eachLevel.name;
            venueLevel.save();
            EventLevel eventLevel = new EventLevel(event: event, venueLevel: venueLevel, price: eachLevel.price);
            eventLevel.save();
            eachLevel.rows.each{ eachRow ->
                VenueRow venueRow = new VenueRow(level: venueLevel);
                venueRow.save();
                EventRow eventRow = new EventRow(level: eventLevel, venueRow: venueRow);
                eventRow.save();

                eachLevel.seats.each { eachSeat ->
                    VenueSeat venueSeat = new VenueSeat(row: venueRow, rowSeatNumber: eachSeat);
                    venueSeat.save();
                    EventSeat eventSeat = new EventSeat(row: eventRow, venueSeat: venueSeat);
                    eventSeat.save();
                }
            }

        }
    }




}
