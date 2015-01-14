package com.knuthp.microservices.trainstations.stations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;

import org.junit.Ignore;
import org.junit.Test;

import com.knuthp.microservices.trainstations.rt.domain.RtDepartures;
import com.knuthp.microservices.trainstations.rt.domain.RtStop;

public class AtStationStoreTest {

	@Test
	@Ignore
	public void test() throws Exception {
		AtStationStore atStationStore = new AtStationStore();
		RtDepartures rtDepartures = new RtDepartures();
		RtStop rtStop = new RtStop();
		rtStop.setJourneyId("myJourneyId");
		OffsetDateTime arrivalTime = OffsetDateTime.parse("2015-01-05T21:54:00+01:00"); 
		rtStop.setAimedArrivalTime(arrivalTime);
		rtDepartures.addStop(rtStop );
		TrainAtStationListener trainAtStationListener = mock(TrainAtStationListener.class);
		atStationStore.addTrainAtStationListener(trainAtStationListener);

		atStationStore.updateDepartures(rtDepartures);

		atStationStore.run(1);
		Place asker = new Place();
		TrainJourney oslo0742 = new TrainJourney();
		verify(trainAtStationListener).arriveStation(eq(asker), eq(oslo0742));
	}

}
