package com.knuthp.microservices.trainstations.stations;

import com.knuthp.microservices.trainstations.rt.domain.RtDepartures;
import com.knuthp.microservices.trainstations.rt.domain.RtStop;

public class RtStationListener {

	public void update(RtDepartures rtDepartures) {
		for (RtStop rtStop : rtDepartures.getRtStopList()) {
			if (rtStop.isVehicleAtStop()) {
				System.out.println("Place: " + rtDepartures.getPlaceId() + " " + rtStop.getPublishedLineName() + " At stop");
			}
		}
		
	}

}
