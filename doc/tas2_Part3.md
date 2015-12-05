# TSA2

The ticketServiceApp2 represents the first pass (there was no '1') through the ticketServiceApplication

By the end of [Part2](tas2_Part2.md) we had a first pass at the core model, and a description of the algorithm
at a high level

## Filling things out : Domain

In Grails and other frameworks, most of the domain is automatically mapped and persisted to the database.
This is a big time and complexity saver: if you have the domain model, you know what the database model
will look like by default, and vice-versa.  This also means though that you have to make sure your class
is really meant to be "in-database" vs. an intelligent transient object or a simple transfer object.

After adding a few more classes, the auto-generated diagram looks pretty reasonable:

See [Wal1_AutoDataModel2_mlf15a1.png](tas2_Part3/Wal1_AutoDataModel2_mlf15a1.png)
<img src="https://raw.githubusercontent.com/markfussell/walrepo1_fluffy-octo-kidney/master/doc/tas2_Part3/Wal1_AutoDataModel2_mlf15a1.png" />

We have the static venue information with relatively static event information matching in hierarchy.

Note that the domain does not have to be this expanded in the database (things like Seats could be dynamically 
generated or pure referenced by index) but by keeping everything as entities, we both make things simpler and
enable 'pivots' in perspective (like how many edge-seats are not occupied in a show is an easy thing to answer).

### SeatHold

A SeatHold has now been expanded a bit.  It has constraints that enable some of it to be temporarily null,
and it has declared the 'seatCursor' to be transient so this active object is clearly not intended to be stored
in the database.  If we needed to persist the seatCursor for the SeatHold, we would do it by some simple identity.

```groovy
/**
 * A SeatHold represents a customer requesting to book a particular set of seats
 * at a venue before they formally reserve them.  A SeatHold expires
 * (becomes invalid) if not reserved within a particular amount
 * of time from the hold.
 *
 * Basically a SeatHold starts as a request, moves into an 'offer', and then the offer expires or the reservation
 * is made
 */
class SeatHold {
    static constraints = {
        customerEmail blank: false

        reservation nullable: true
        reservationDate nullable: true
        firstSeat nullable: true
        holdDate nullable: true
    }
    static transients = ['seatCursor']

    public boolean isSatisfied() {
        return firstSeat != null
    }

    public boolean isReserved() {
        return reservation !=null
    }

    String customerEmail;
    int numSeats;

    int minLevel;
    int maxLevel;

    /**
     * The cursor that gave out the seats
     */
    transient SeatCursor seatCursor
    EventSeat firstSeat
    Date holdDate

    SeatReservation reservation = null
    Date reservationDate
}
```

### Data Population

To get the data into the program, we could associate it with the 'test' but that doesn't help us get the system
up and running.  A better approach is to have the data be auto-populated as part of bootstrapping the system.
Auto-populated data would normally be constants or slow-changing (system-release) data vs. data that is 
more like real production maintained information, but we can also use it for 'demo' data and testing data
is an excellent example of that.  We can simply disable the auto-population (or wipe it out) in the environments
we don't want it.

As part of the bootstrap, we call a 'DomainLoaderService' which loads the domain.  For more complicated loading,
we would delegate this out to multiple classes.

```groovy
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
            (1..eachLevel.rows).each{ eachRow ->
                VenueRow venueRow = new VenueRow(level: venueLevel);
                venueRow.save();
                EventRow eventRow = new EventRow(level: eventLevel, venueRow: venueRow);
                eventRow.save();

                (1..eachLevel.seats).each { eachSeat ->
                    VenueSeat venueSeat = new VenueSeat(row: venueRow, rowSeatNumber: eachSeat);
                    venueSeat.save();
                    EventSeat eventSeat = new EventSeat(row: eventRow, venueSeat: venueSeat);
                    eventSeat.save();
                }
            }

        }
    }




}
```

By the end of the bootstrap, the launched application can be directly interacted with:

See [Wal1_AutoDataModel2_mlf15a1.png](tas2_Part3/Wal1_DomainDataLoad_mlf15a1.png)
<img src="https://raw.githubusercontent.com/markfussell/walrepo1_fluffy-octo-kidney/master/doc/tas2_Part3/Wal1_DomainDataLoad_mlf15a1.png" />



   

   