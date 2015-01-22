package com.knuthp.microservices.trainstations.stations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.knuthp.microservices.trainstations.rt.domain.RtStop;

public class TrainAtStationStore implements TrainAtStationListener {
	private Logger logger = LoggerFactory.getLogger(TrainAtStationStore.class);

	@Override
	public void arriveStation(Place place, TrainJourney trainJourney) {
		RtStop rtStop = trainJourney.getRtStop();
		logger.info("ArriveAtStation={}, line={}, expArriveTime={}, dest={}", place
				.getPlaceId(), rtStop.getPublishedLineName(), rtStop
				.getExpectedArrivalTime().toString(), rtStop.getDestinationName());
	}

	@Override
	public void leaveStation(Place place, TrainJourney trainJourney) {
		RtStop rtStop = trainJourney.getRtStop();
		logger.info(
				"DepartureAtStation={}, line={}, expDepartureTime={}, dest={}",
				place.getPlaceId(), rtStop.getPublishedLineName(), rtStop
						.getExpectedDepartureTime().toString(), rtStop
						.getDestinationName());
	}

}
