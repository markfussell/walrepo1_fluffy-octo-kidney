package com.emenar.wal.tsa2.domain.model

import com.emenar.wal.tsa2.domain.model.PerformanceEvent
import com.emenar.wal.tsa2.domain.model.PerformingVenue

trait DomainDataFactory {

	PerformingVenue defaultVenue() {
		new PerformingVenue(venueName: "TheVenue")
	}

	PerformanceEvent defaultPeformance() {
		new PerformanceEvent(performanceDate: new Date(), venue: defaultVenue())
	}

}
