package com.emenar.wal.tsa2

import grails.transaction.Transactional

@Transactional
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
