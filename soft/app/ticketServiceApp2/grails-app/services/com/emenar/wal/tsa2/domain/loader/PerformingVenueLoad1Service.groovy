package com.emenar.wal.tsa2.domain.loader

import grails.transaction.Transactional
import com.emenar.wal.tsa2.domain.model.*

@Transactional

class PerformingVenueLoad1Service {

    def loadData() {
        def object = new PerformingVenue(venueName: "TheVenue")
        object.save()
    }
}
