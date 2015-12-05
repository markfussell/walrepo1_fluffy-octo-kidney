package com.emenar.wal.tsa2.domain.model

import grails.transaction.Transactional

@Transactional

class PerformingVenueRepoService {

    PerformingVenue findAny() {
        return (PerformingVenue) PerformingVenue.find()
    }

}
