package com.knuthp.microservices.trainstations.stations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.OffsetDateTime;

import org.junit.Before;
import org.junit.Test;

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
		RtStop stop = createStop(journeyId, exptectedArrivalTime,
				expectedDepartureTime);

		atStationStore.addDeparture(placeId, stop);

		atStationStore.run(1);
		Place asker = new Place(placeId);
		TrainJourney oslo0742 = new TrainJourney(stop);
		verify(trainAtStationListener).arriveStation(eq(asker), eq(oslo0742));
	}

	@Test
	public void testArrivesNowUpdateTriggersOneEvent() throws Exception {
		String placeId = "asker";
		String journeyId = "myJourneyId";
		OffsetDateTime exptectedArrivalTime = OffsetDateTime.now();
		OffsetDateTime expectedDepartureTime = OffsetDateTime.now().plusDays(1);
		RtStop stop = createStop(journeyId, exptectedArrivalTime,
				expectedDepartureTime);

		atStationStore.addDeparture(placeId, stop);

		atStationStore.run(1);
		Place asker = new Place(placeId);
		TrainJourney oslo0742 = new TrainJourney(stop);
		verify(trainAtStationListener).arriveStation(eq(asker), eq(oslo0742));
	}

	@Test
	public void testArrivesInOneHourTriggersNothing() throws Exception {
		String placeId = "asker";
		String journeyId = "myJourneyId";
		OffsetDateTime exptectedArrivalTime = OffsetDateTime.now().plusHours(1);
		OffsetDateTime expectedDepartureTime = OffsetDateTime.now().plusDays(1);
		RtStop stop = createStop(journeyId, exptectedArrivalTime,
				expectedDepartureTime);

		atStationStore.addDeparture(placeId, stop);
		atStationStore.run(1);

		verify(trainAtStationListener, times(0)).arriveStation(
				any(Place.class), any(TrainJourney.class));
	}

	@Test
	public void testArrivesTwiceNowTriggersTwoEvents() throws Exception {
		String placeId = "asker";
		String journeyId = "myJourneyId";
		OffsetDateTime exptectedArrivalTime = OffsetDateTime.now();
		OffsetDateTime expectedDepartureTime = OffsetDateTime.now().plusDays(1);
		RtStop stop1 = createStop(journeyId, exptectedArrivalTime,
				expectedDepartureTime);
		RtStop stop2 = createStop(journeyId, exptectedArrivalTime,
				expectedDepartureTime);

		atStationStore.addDeparture(placeId, stop1);
		atStationStore.addDeparture(placeId, stop2);
		atStationStore.run(2);

		verify(trainAtStationListener, times(2)).arriveStation(
				any(Place.class), any(TrainJourney.class));
	}

	@Test
	public void testArrivesWithUpdateTriggerOneEvent() throws Exception {
		String placeId = "asker";
		String journeyId = "myJourneyId";
		OffsetDateTime fixedTime = OffsetDateTime.now();
		OffsetDateTime expectedArrivalTime = OffsetDateTime.now();
		OffsetDateTime expectedDepartureTime = OffsetDateTime.now().plusDays(1);
		RtStop stop1 = createStop(journeyId, expectedArrivalTime,
				expectedDepartureTime);
		stop1.setAimedArrivalTime(fixedTime);
		RtStop stop1Update = createStop(journeyId, expectedArrivalTime,
				expectedDepartureTime);
		stop1Update.setAimedArrivalTime(fixedTime);

		atStationStore.addDeparture(placeId, stop1);
		atStationStore.updateDeparture(placeId, stop1Update);
		atStationStore.run(2);

		verify(trainAtStationListener, times(1)).arriveStation(
				any(Place.class), any(TrainJourney.class));
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
