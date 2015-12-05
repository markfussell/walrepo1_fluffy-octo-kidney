package com.emenar.wal.tsa2.domain.model

import grails.transaction.Transactional

@Transactional
class PerformanceEventRepoService {

    PerformanceEvent findAny() {
        return null; //(PerformanceEvent) PerformanceEvent.find()
    }

}
