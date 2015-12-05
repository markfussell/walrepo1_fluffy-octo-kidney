package com.emenar.wal.tsa2.domain.loader

import com.emenar.wal.tsa2.domain.model.PerformanceEvent
import com.emenar.wal.tsa2.domain.model.PerformingVenue
import grails.transaction.Transactional

@Transactional

class PerformanceEventLoad1Service {
    def performingVenueRepoService;
    def systemDateService;

    def loadData() {
        def venue = performingVenueRepoService.findAny();
        def object = new PerformanceEvent(venue: venue, performanceDate: systemDateService.getTestDate());
        object.save();
    }

}
