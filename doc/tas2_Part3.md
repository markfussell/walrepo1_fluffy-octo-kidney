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

We have the static venue information with relatively static event information matching in hierarchy.

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



   

   