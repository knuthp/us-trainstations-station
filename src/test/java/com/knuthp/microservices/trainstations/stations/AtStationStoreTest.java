package com.knuthp.microservices.trainstations.stations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.knuthp.microservices.trainstations.rt.domain.RtDepartures;
import com.knuthp.microservices.trainstations.rt.domain.RtStop;

public class AtStationStoreTest {

	private AtStationStore atStationStore;
	private TrainAtStationListener trainAtStationListener;

	@Test
	public void testArrivesNowTriggersEvent() throws Exception {
		String placeId = "asker";
		String journeyId = "myJourneyId";
		OffsetDateTime exptectedArrivalTime = OffsetDateTime.now();
		OffsetDateTime expectedDepartureTime = OffsetDateTime.now().plusDays(1);
		RtDepartures rtDepartures = createDeparture(placeId, createStop(journeyId, exptectedArrivalTime,
				expectedDepartureTime));

		atStationStore.updateDepartures(rtDepartures);

		atStationStore.run(1);
		Place asker = new Place(placeId);
		TrainJourney oslo0742 = new TrainJourney();
		verify(trainAtStationListener).arriveStation(eq(asker), eq(oslo0742));
	}

	@Test
	public void testArrivesInOneHourTriggersNothing() throws Exception {
		String placeId = "asker";
		String journeyId = "myJourneyId";
		OffsetDateTime exptectedArrivalTime = OffsetDateTime.now().plusHours(1);
		OffsetDateTime expectedDepartureTime = OffsetDateTime.now().plusDays(1);
		RtDepartures rtDepartures = createDeparture(placeId, createStop(journeyId, exptectedArrivalTime,
				expectedDepartureTime));

		atStationStore.updateDepartures(rtDepartures);
		atStationStore.run(1);

		verify(trainAtStationListener, times(0)).arriveStation(any(Place.class), any(TrainJourney.class));
	}


	@Test
	public void testArrivesTwiceNowTriggersTwoEvents() throws Exception {
		String placeId = "asker";
		String journeyId = "myJourneyId";
		OffsetDateTime exptectedArrivalTime = OffsetDateTime.now();
		OffsetDateTime expectedDepartureTime = OffsetDateTime.now().plusDays(1);
		RtDepartures rtDepartures = createDeparture(placeId, createStop(journeyId, exptectedArrivalTime,
				expectedDepartureTime), createStop(journeyId, exptectedArrivalTime,
						expectedDepartureTime));

		atStationStore.updateDepartures(rtDepartures);
		atStationStore.run(2);

		verify(trainAtStationListener, times(2)).arriveStation(any(Place.class), any(TrainJourney.class));
	}

	
	private RtDepartures createDeparture(String placeId, RtStop ... rtStops) {
		RtDepartures rtDepartures = new RtDepartures();
		rtDepartures.setPlaceId(placeId);
		for(RtStop rtStop : rtStops) {
			rtDepartures.addStop(rtStop);
		}
		return rtDepartures;
	}

	private RtStop createStop(String journeyId,
			OffsetDateTime exptectedArrivalTime,
			OffsetDateTime expectedDepartureTime) {
		RtStop rtStop = new RtStop();
		rtStop.setJourneyId(journeyId);
		rtStop.setExpectedArrivalTime(exptectedArrivalTime);
		rtStop.setExpectedDepartureTime(expectedDepartureTime);
		return rtStop;
	}

	@Before
	public void setUp() {
		atStationStore = new AtStationStore();
		trainAtStationListener = mock(TrainAtStationListener.class);
		atStationStore.addTrainAtStationListener(trainAtStationListener);
	}

}
