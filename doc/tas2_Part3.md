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


## Filling things out : Algorithm

The TicketService now finds the Performance and gets the EventBooking object from it.  There should only
be one booking object per performance, and it is up to the PerformanceEvent object to make sure that is true.

 
```groovy
class TicketService {
    def findBooking() {
        return findPerformance().findBooking();
    }
    
    def numSeatsAvailable(int levelId = 0) {
        def booking = findBooking();
        return booking.countAvailableSeats(levelId);
    }

    def findAndHoldSeats(String customerEmail, int numSeats, int minLevel=0, int maxLevel=0) {
        EventBooking booking = findBooking();
        SeatHold request = new SeatHold(customerEmail: customerEmail, numSeats: numSeats, minLevel: minLevel, maxLevel: maxLevel)
        boolean result = booking.findSeatsFor(request);
        return result;
    }
}
    
```

### EventBooking

EventBooking and SeatCursor contain the algorithm/strategy for reserving seats.  

```groovy
/**
 * The EventBooking represents the dynamic reservation for
 * an event: the available seats (remaining in the SeatCursors),
 * the holds on those seats, and the reservations that are fully committed
 */
class EventBooking {
```

Preparing the booking involves creating the cursors
```groovy
    /**
     * To prepare a booking, we need to expand out the dynamic
     * objects associated with the Event / Venue
     */
    void prepare() {

        //Create two cursors per row
        event.levels.each { EventLevel level ->
            level.rows.each {
                EventRow row ->
                    SeatCursor cursorR = new SeatCursor(row: row, goRight: true);
                    SeatCursor cursorL = new SeatCursor(row: row, goRight: false);

                    addCursor(cursorR);
                    addCursor(cursorL);
            }
        }

        /**
         * These don't change dynamically.  If they did, we would have to migrate
         * to new sorts (a/b flipping) without interfering with request processing
         */
        Collections.sort(ranks);
        totalCursors = ranks.size();
    }
```

And then running through the algorithm simply runs through the cursors, starting at the appropriate start point and
ending either when the hold level is too low or we run out of cursors.

```groovy
    boolean findSeatsFor(SeatHold hold) {
        int startLevel = hold.minLevel;
        int startIndex = 0;
        if (startLevel > 0) {
            startIndex = levelToFirstRank[startLevel];
        }

        return findSeatsFor_at(hold, startIndex);
    }

    protected boolean findSeatsFor_at(SeatHold hold, int index) {
        int rank = ranks[index];
        SeatCursor cursor = rankToCursor[rank];

        if (cursor.isBeyondHold(hold)) return false;

        if (cursor.canSatisfyHold(hold)) {
            return true;
        }

        if (index++ < totalCursors) {
            return findSeatsFor_at(hold, index);
        }
        return false;
    }
```

All of this algorithm can be executed with multiple threads given a single 'hold' object.  So there is no bottleneck 
yet.

### SeatCursor

Because we want to make sure that seat allocations can't collide, a SeatCursor can handle only one request at a time.  
To deal with that, we 'Synchronized' on that inner request and also let the world know this SeatCursor isBusy.

```groovy
/**
 * A SeatCursor has a starting seat and a range of seats it _owns_ and can
 * give out to a SeatHold.  Eventually there could be several different
 * models/strategies, but currently there is simply a 'row' cursor which is
 * going either right or left within the row
 */
class SeatCursor {
    /**
      * Naming is slightly misleading since this has a side-effect
      * Maybe 'satisfiedHold' instead
      */
     boolean canSatisfyHold(SeatHold hold) {
         if (hold.numSeats > remainingSeats) return false;
         
         boolean result = canSatisfyHold_Inner(hold);
         return result;
     }
 
     @Synchronized
     boolean canSatisfyHold_Inner(SeatHold hold) {
         isBusy = true;
         try {
             if (hold.numSeats > remainingSeats) return false;
 
             hold.firstSeat = getLeftSeatFor(hold.numSeats);
             hold.seatCursor = this;
             currentOffset += hold.numSeats;
             remainingSeats = totalSeats - currentOffset;
             isFull = remainingSeats < 1;
             return true;
         } finally {
             isBusy = false;
         }
     }
```


This 'Inner' vs. 'Outer' entry point enables more advanced behavior to be built around the 'guaranteed' inner
transaction.  For example, we could skip the inner call if 'isBusy' is true, rushing on to the next Curator/Cursor to 
spread out the load.

## Testing the puppy

So we have most of the model done, and now need to verify it actually works.  Unit tests are interesting for verifying
you have the basics working or for very simple / isolated systems.  With frameworks like Spring, Grails, and others
where dependency injection and many other things are occuring, the unit tests become excessive work to Mock/Stub 
functionality.  Interestingly, the original UnitTest framework was designed to run against a full operating environment,
so the 'Unit' was just the smallness of the test vs. the smallness of the system being tested.  In any case, switching
from Spock Unit to Spock Integration tests simply changes the dynamic of what is being run against.

### Can we count?

The first test is nicely simple and easy to describe.  Given we have the example stadium, how many seats are
available before anyone is seated?

```groovy
    def "test numSeatsAvailable with level #level is #seats"() {
        given:
        def numberOfSeats = service.numSeatsAvailable(level)

        expect: "Matches the level"
        numberOfSeats == seats

        where:
        level | seats || error
        0     | 6250  || null
        1     | 1250  || null
        2     | 2000  || null
        3     | 1500  || null
        4     | 1500  || null
    }
```

If this runs green, we must have some way to have seats at different levels and to be able to count them all.  The
implementation leverages our SeatCursors, but the caller does not know this:

#### TicketService

```groovy
class TicketService {
    [[...]]
    
    def numSeatsAvailable(int levelId = 0) {
        def booking = findBooking();
        return booking.countAvailableSeats(levelId);
    }
```

#### EventBooking

```groovy
class EventBooking {
    [[...]]
    int countAvailableSeats(int level) {
         int count = 0;
 
         if (level > 0) {
             levelCursors[level].each {SeatCursor eachCursor ->
                 count += eachCursor.remainingSeats;
             }
         } else {
             cursors.each {SeatCursor eachCursor ->
                 count += eachCursor.remainingSeats;
             }
         }
 
         return count;
     }
```

### Can we hold?

Next we can test whether the hold mechanism is working at all.  If we try to book 10 seats without a level restriction,
the available seat count should go down in the proper level.

```groovy
    def "test findAndHoldSeats for #email number #holdSeats holdLevel #holdLevel has level #level with seats #seats"() {
        given:
        def result  = service.findAndHoldSeats(email, holdSeats, holdLevel, holdLevel)
        def numberOfSeats = service.numSeatsAvailable(level)
        SeatHold hold = result.seatHold;

        expect: "The hold object creation is always successful"
        hold.customerEmail == email
        result.success == true
        numberOfSeats == seats

        where:
        email                      | holdSeats | holdLevel || level | seats | error
        'mark.fussell@emenar.com'  | 10        | 0         || 1     | 1240  | null
    }
```

The 'result' passed back is a flexibly Map/Hash so we can add attributes as needed.  In JSON form, this
is what the service would pass, but here we are staying within-server.

```groovy
class TicketService {
[[...]]

    def findAndHoldSeats(String customerEmail, int numSeats, int minLevel=0, int maxLevel=0) {
        EventBooking booking = findBooking();
        SeatHold request = new SeatHold(customerEmail: customerEmail, numSeats: numSeats, minLevel: minLevel, maxLevel: maxLevel)
        boolean success = booking.findSeatsFor(request);
        return [
                success: success,
                seatHold: request,
                seatHoldId: request.id
        ];
    }
```

If this runs green, we are at least holding some seats for the person.

### Releasing holds

The API did not include it, but to test the core release functionality of a 'hold' we can add it explicitly:

```groovy
    def releaseHold(int seatHoldId, String customerEmail) {
        EventBooking booking = findBooking();
        boolean success = booking.releaseHold(seatHoldId, customerEmail);
        return [
                success: success
        ]
    }
```

Now if we hold and release, we should be back to our starting value:

```groovy
    def "test find and release for #email number #holdSeats holdLevel #holdLevel has level #level with seats #seats"() {
        given:
        def result  = service.findAndHoldSeats(email, holdSeats, holdLevel, holdLevel)
        SeatHold hold = result.seatHold;

        expect: "The hold  is successful"
        hold.customerEmail == email
        result.success == true

        when: "We release"
        int holdId = result.seatHoldId;
        def result2  = service.releaseHold(holdId, email)
        def numberOfSeats = service.numSeatsAvailable(level)

        then: "The release is successful"
        result2.success == true
        numberOfSeats == seats

        where:
        email                      | holdSeats | holdLevel || level | seats | error
        'mark.fussell@emenar.com'  | 10        | 0         || 1     | 1250  | null
    }

```

### Timed based release

We can simply use 'Quartz' or a similar cron approach to look for expired holds.  Alternatively, when a row gets
full or is nearly full, we can 'compact' it, basically letting a hold stay around for a while as long as it is not a
popular part of the venue.  The advantage of this is that the work is done 'inline' as part of the SeatCursor's
core synchronized behavior, so we don't need to worry about different threads hitting the same records.  A SeatCursor
owns those records, so it should always be coming through a core request to it.

### Reservations

Turning a Hold into a Reservation is simply creating and binding a reservation to the hold.  The responsibility 
needs to be delegated to the SeatCursor to again deal with the threading issue.


## Part3 Test

The framework of Grails is built on top of Spring/Gradle, so you can drive the whole system with pure gradle.
At the point of this commit, the tests run clean using 'gradle test integrationTest'.

```bash
takakage:ticketServiceApp2 markfussell$ ./gradlew test integrationTest
:compileJava UP-TO-DATE
:compileGroovy UP-TO-DATE
:processResources UP-TO-DATE
:classes UP-TO-DATE
:compileTestJava UP-TO-DATE
:compileTestGroovy UP-TO-DATE
:processTestResources UP-TO-DATE
:testClasses UP-TO-DATE
:test
:compileIntegrationTestJava UP-TO-DATE
:compileIntegrationTestGroovy UP-TO-DATE
:processIntegrationTestResources UP-TO-DATE
:integrationTestClasses UP-TO-DATE
:integrationTest UP-TO-DATE
:mergeTestReports

BUILD SUCCESSFUL

Total time: 13.21 secs
```

## Part3 ToDo

There are a lot of things not yet done as of Part3:
  
  * The correct 'release' code is not present as of this commit
  * Testing with a lot of requests, 
  * Verifying that the seats allocated are correct
  * Implementing the reserve correctly / completely
  * etc.
  
But I believe the core of the proposed design solution and the supporting code / tests are complete enough
to evaluate and discuss.

I may work on Part4 when time allows (finishing this version), and Part5 would likely be a de-Grails of the
solution to compare pure Spring / Java vs. the framework advantages of Grails.




  




   