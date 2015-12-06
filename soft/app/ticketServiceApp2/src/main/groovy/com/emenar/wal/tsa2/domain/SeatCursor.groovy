package com.emenar.wal.tsa2.domain

import com.emenar.wal.tsa2.domain.model.EventLevel
import com.emenar.wal.tsa2.domain.model.EventRow
import com.emenar.wal.tsa2.domain.model.EventSeat
import com.emenar.wal.tsa2.domain.model.SeatHold
import groovy.transform.Synchronized

/**
 * A SeatCursor has a starting seat and a range of seats it _owns_ and can
 * give out to a SeatHold.  Eventually there could be several different
 * models/strategies, but currently there is simply a 'row' cursor which is
 * going either right or left within the row
 */
class SeatCursor {
    /**
     * To prepare a cursor, we find the middle seat
     * of the EventRow and then prepare to go right or left
     *
     */
    void prepare() {
        EventLevel level = row.level;
        int levelId = level.venueLevel.levelId;

        //Support any number of levels, and <1000 rows per level
        int rank = levelId * 10000 + row.venueRow.rowId * 10 + (goRight ? 1 : 2)

        this.rank = rank;

        //Eventually need to split
        int rowSeats = row.seats.size();
        int halfSeats = Math.floor(rowSeats / 2)


        if (goRight) {
            totalSeats = rowSeats - halfSeats;
            startingSeat = row.seats.first().findSeatInRowOffset(halfSeats);
        } else {
            totalSeats = halfSeats;
            startingSeat = row.seats.first().findSeatInRowOffset(halfSeats-1);
        }

        remainingSeats = totalSeats;
        currentOffset = 0;
        isBusy = false;
    }

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

    /**
     * Find the leftmost seat for consistency / simplicity
     */
    EventSeat getLeftSeatFor(int numSeats) {
        if (goRight) {
            return startingSeat.findSeatInRowOffset(currentOffset);
        } else {
            return startingSeat.findSeatInRowOffset(-(currentOffset + numSeats - 1));
        }
    }

    /**
     * Is the cursor for a level that is beyond the maximum of the hold request
     */
    boolean isBeyondHold(SeatHold hold) {
        return false;
    }

    /**
     * Ultra simplified.  Needs to synchronize with the other seat change,
     * and would also mark things for compaction.
     */
    @Synchronized
    boolean releaseHold(SeatHold hold) {
        remainingSeats += hold.numSeats;

        return true;
    }



        //===============================================
    //===============================================
    //===============================================

    EventRow row
    boolean goRight

    boolean isBusy;
    boolean isFull;

    /**
     * Calculated and frozen based on the level/row/left-or-right
     */
    int rank;

    EventSeat startingSeat

    int currentOffset = 0
    int totalSeats
    int remainingSeats


}
