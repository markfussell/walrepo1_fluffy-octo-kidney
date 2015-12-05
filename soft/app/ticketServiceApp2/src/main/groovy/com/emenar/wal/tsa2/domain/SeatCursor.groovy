package com.emenar.wal.tsa2.domain

import com.emenar.wal.tsa2.domain.model.EventLevel
import com.emenar.wal.tsa2.domain.model.EventRow
import com.emenar.wal.tsa2.domain.model.EventSeat
import com.emenar.wal.tsa2.domain.model.SeatHold
import groovy.transform.Synchronized

/**
 * A SeatCursor has a starting seat and a range of seats it _owns_ and can
 * give out to a SeatHold.  Eventually there could be several different
 * models, but currently there is simply a 'row' cursor which is
 * going either right or left within the row
 *
 * The SeatCursor represents the 'state' of the cursor.  The actual
 * functionality is in SeatCursorService.  The reason to separate the
 * two is for easier persistence of the SeatCursor state, but otherwise
 * the two should be considered equivalent
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
    }

    /**
     * Naming is slightly misleading since this has a side-effect
     * Maybe 'satisfiedHold' instead
     */
    @Synchronized
    boolean canSatisfyHold(SeatHold hold) {
        isBusy = true;
        boolean result = canSatisfyHold_Inner(hold);
        isBusy = false;
        return result;
    }

    boolean canSatisfyHold_Inner(SeatHold hold) {
        if (hold.numSeats > remainingSeats) return false;

        hold.firstSeat = getLeftSeatFor(hold.numSeats);
        hold.seatCursor = this;
        currentOffset += hold.numSeats;
        remainingSeats = totalSeats - currentOffset;
        isFull = remainingSeats < 1;
        return true;
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
