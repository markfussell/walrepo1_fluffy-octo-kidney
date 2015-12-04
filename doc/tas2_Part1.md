# TSA2

The ticketServiceApp2 represents the first pass (there was no '1') through the ticketServiceApplication

Trying to quickly work through:
                               
 * An object model of Performance, Venues, Seats, and Holds
 * A service description for the interface
 * A set of tests working through that service
   
all done in mostly declarative code.  


### P1: Domain

The domain started with a simple SeatHold object since that was the only type explicitly identified.  But bit
by bit it expanded into two core models outside the SeatHold itself:

  * Details of the (Performing)Venue that never change (or at least not during a performance not involving Godzilla)
    * The Venue contains VenueLevels and VenueSeats
  
```groovy
/**
 * A PerformingVenue is a venue that this ticketing system can
 * reserve tickets for.  It represents the venue but not a particular performance
 * of that venue.  In general, the system would have:
 *   * Multiple Performances at Multiple Venues
 * but for this particular problem, the cardinalities happen to both be "one"
 */
class PerformingVenue {
    String venueName

    static hasMany = [levels: VenueLevel, seats: VenueSeat]

}
```

  * Details of the actual (Performance)Event at the venue
    * Again the Event contains Levels and Seats
 
```groovy
/**
 * A PerformanceEvent is a performance at a particular venue and
 * includes the specifics of the performance
 */
class PerformanceEvent {
    Date performanceDate

    PerformingVenue venue

    static hasMany = [levels: EventLevel, seats: EventSeat, availableSeats: EventSeat]
}
```

The Venue information is static:

```groovy
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
```

The Performance information is mostly static, except for the SeatHold aspect which is allowed to be 'nullable' initially

```groovy
/**
 * A EventSeat is a specific seat at a particular venue during a particular performance.
 * PerformanceSeats intersect a performance (a start and end time at a venue) with a seat at
 * that venue.  Only one person can be in a seat (unticketed lap-children exempted) at any given time
 * and for this problem, you can not timeshare a performance seat.
 *
 * If a 'SeatHold' is null then the seat is unclaimed.  At scale, the real issue is
 * matching seats to holds with:
 *
 *   * Minimal collision
 *   * Maximum success
 *
 * Or basically getting maximum throughput of the reservation process
 */
class EventSeat {
    static constraints = {
        seatHold nullable: true
    }

    PerformanceEvent event
    VenueSeat venueSeat

    SeatHold seatHold
}
```

it might be better to have the pure-dynamic aspect lifted completely off the static aspects, but the first pass
comingled them very slightly.

The final data model ends up being auto-generated to be the following, but really the parallel parentage should be 
aligned:

See [Wal1_AutoDataModel_mlf15a1.png](tas2_Part1/Wal1_AutoDataModel_mlf15a1.png)

### Service and Test

The service was basically initially mocked out completely:

```groovy
class TicketService {

    def numSeatsAvailable(int venueLevel = 0) {
        return venueLevel
    }

    def findAndHoldSeats(String customerEmail, int numSeats, int minLevel=0, int maxLevel=0) {
        return new SeatHold(customerEmail: customerEmail, numSeats: numSeats, minLevel: minLevel, maxLevel: maxLevel)
    }

    def reserveSeat(int seatHoldId, String customerEmail) {
        return "hi_"+seatHoldId+"_"+customerEmail
    }
}
```

The parameter order of findAndHoldSeats was changed to have the optional items at the end to avoid confusion (especially 
 when the data types are all the same).
 
The first test was in Spock, and leveraged the complete mocking of the responses.

```
@TestFor(TicketService)
class TicketServiceSpec extends Specification {

    def "test numSeatsAvailable"() {
        given:
        def numberOfSeats = service.numSeatsAvailable()

        expect: "Always 1"
            numberOfSeats == 1
    }

    def "test findAndHoldSeats with email #email"() {
        given:
        SeatHold hold = service.findAndHoldSeats(email, 2)

        expect: "The hold is always successful"
            hold.customerEmail == email

        where:
        email                      || error
        'mark.fussell@emenar.com'  || null
    }

    def "test reserveSeat with id #id email #email"() {
        given:
        String confirmationCode = service.reserveSeat(id, email)

        expect: "The hold is always successful"
          confirmationCode == "hi_"+id+"_"+email

        where:
        id | email                      || error
        1  | 'mark.fussell@emenar.com'  || null
    }


}
```

## The core algorithm

See [tas2_Part2](tas2_Part2.md)


