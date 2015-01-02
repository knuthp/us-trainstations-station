package com.knuthp.microservices.trainstations.stations;

import static org.junit.Assert.*;

import org.junit.Test;

import com.knuthp.microservices.trainstations.rt.domain.RtDepartures;
import com.knuthp.microservices.trainstations.rt.domain.RtStop;

public class RtStationListenerTest {

	@Test
	public void test() {
		RtStationListener listener = new RtStationListener();
		RtDepartures rtDepartures = new RtDepartures();
		rtDepartures.setPlaceId("a");
		RtStop rtStop = new RtStop();
		rtStop.setPublishedLineName("b");
		rtStop.setVehicleAtStop(true);
		rtDepartures.addStop(rtStop);
		
		listener.update(rtDepartures);
		
		fail("Not yet implemented");
	}

}
