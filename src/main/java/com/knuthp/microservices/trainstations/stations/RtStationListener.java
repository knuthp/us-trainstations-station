package com.knuthp.microservices.trainstations.stations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.knuthp.microservices.trainstations.rt.domain.RtDepartures;
import com.knuthp.microservices.trainstations.rt.domain.RtStop;

public class RtStationListener {
	private Logger logger = LoggerFactory.getLogger(RtStationListener.class);

	public void update(RtDepartures rtDepartures) {
		boolean isDelay = false;
		for (RtStop rtStop : rtDepartures.getRtStopList()) {
			if (rtStop.getDelay() != null && !"PT0S".equals(rtStop.getDelay())) {
				logger.info("Place: " + rtDepartures.getPlaceId() + ", id " + rtStop.getJourneyId() + ", line " + rtStop.getPublishedLineName() + " delayed " + rtStop.getDelay());
				isDelay = true;
			}
		}
		
		if (!isDelay) {
			logger.info("Place: " + rtDepartures.getPlaceId() + " no delays");
		}
		
	}

}
