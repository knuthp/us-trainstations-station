package com.knuthp.microservices.trainstations.stations;

import static org.junit.Assert.assertEquals;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.knuthp.microservices.trainstations.rt.domain.RtDepartures;
import com.knuthp.microservices.trainstations.rt.domain.RtStop;
import com.knuthp.microservices.trainstations.stations.StationCache.FilterStatus;

public class StationCacheTest {

	private static final OffsetDateTime FIXED_DATE_TIME = OffsetDateTime.now();
	private StationCache stationCache;

	@Test
	public void testOneNew() {
		RtStop rtStop = createRtStop("myJourneyId", FIXED_DATE_TIME);
		RtDepartures rtDepartures = createDearturesDefaultStation(rtStop);

		Map<FilterStatus, List<RtStop>> filtered = stationCache
				.filter(rtDepartures);

		// One new;
		assertEquals(1, filtered.size());
		assertEquals(1, filtered.get(FilterStatus.NEW).size());
		assertEquals(rtStop, filtered.get(FilterStatus.NEW).get(0));
	}

	@Test
	public void testNewThenEqual() throws Exception {
		RtDepartures rtDepartures = new RtDepartures();
		rtDepartures.setPlaceId("myPlace");
		RtStop rtStop = new RtStop();
		rtStop.setJourneyId("myJourneyId");
		rtStop.setExpectedArrivalTime(FIXED_DATE_TIME);
		rtDepartures.addStop(rtStop);

		stationCache.filter(rtDepartures);
		Map<FilterStatus, List<RtStop>> filtered2 = stationCache
				.filter(rtDepartures);

		// Only equal on last;
		assertEquals(0, filtered2.size());
	}

	private RtDepartures createDearturesDefaultStation(RtStop rtStop) {
		RtDepartures rtDepartures = new RtDepartures();
		rtDepartures.setPlaceId("myPlace");
		rtDepartures.addStop(rtStop);
		return rtDepartures;
	}

	private RtStop createRtStop(String journeyId, OffsetDateTime arrivalTime) {
		RtStop rtStop = new RtStop();
		rtStop.setJourneyId(journeyId);
		rtStop.setExpectedArrivalTime(arrivalTime);
		return rtStop;
	}

	@Before
	public void setUp() {
		stationCache = new StationCache();
	}
}
