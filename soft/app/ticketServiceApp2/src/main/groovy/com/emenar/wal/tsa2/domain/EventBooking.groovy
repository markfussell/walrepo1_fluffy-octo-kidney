package com.emenar.wal.tsa2.domain

import com.emenar.wal.tsa2.domain.model.EventLevel
import com.emenar.wal.tsa2.domain.model.EventRow
import com.emenar.wal.tsa2.domain.model.PerformanceEvent
import com.emenar.wal.tsa2.domain.model.SeatHold
import com.emenar.wal.tsa2.domain.model.SeatReservation

/**
 * The EventBooking represents the dynamic reservation for
 * an event: the available seats (remaining in the SeatCursors),
 * the holds on those seats, and the reservations that are fully committed
 */
class EventBooking {

    //===============================================
    //===============================================
    //===============================================

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

    protected void addCursor(SeatCursor cursor) {
        cursor.prepare();

        cursors.add(cursor)

        //Ranking makes sure that no cursors are exactly the same
        rankToCursor[cursor.rank] = cursor;

        //This list is kept in-order
        ranks.add(cursor.rank);
    }

    boolean findASeatFor(SeatHold hold) {
        int startLevel = hold.minLevel;
        int startIndex = 0;
        if (startLevel > 0) {
            startIndex = levelToFirstRank[startLevel];
        }

        return findASeatFor_at(hold, startIndex);
    }

    protected boolean findASeatFor_at(SeatHold hold, int index) {
        int rank = ranks[index];
        SeatCursor cursor = rankToCursor[rank];
        if (cursor.canSatisfyHold(hold)) {
            return true;
        }

        if (index++ < totalCursors) {
            return findASeatFor_at(hold, index);
        }
        return false;
    }

    SeatReservation reserveHold(int holdId, String email) {
        return null;
    }

    int countAvailableSeats(int level) {

    }

    //===============================================
    //===============================================
    //===============================================

    PerformanceEvent event

//    static hasMany = [holds: SeatHold, reservations: SeatReservation]

    Map<Integer, SeatCursor> rankToCursor;
    List<Integer> ranks;
    int totalCursors;

    List<SeatCursor> cursors;

    /**
     * For each of the level, what is the lowest rank index (e.g. Level-1 starts at 0, but Level-2 might start at 100)
     */
    List<Integer> levelToFirstRank;

    //===============================================
    //===============================================
    //===============================================
}
