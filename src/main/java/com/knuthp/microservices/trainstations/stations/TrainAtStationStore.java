package com.knuthp.microservices.trainstations.stations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.knuthp.microservices.trainstations.rt.domain.RtStop;

public class TrainAtStationStore implements TrainAtStationListener {
	private Logger logger = LoggerFactory.getLogger(TrainAtStationStore.class);

	private List<RtStop> arrivalList;
	private List<RtStop> departureList;
	private Map<String, List<RtStop>> departurePerStation;

	public TrainAtStationStore() {
		arrivalList = new ArrayList<RtStop>();
		departureList = new ArrayList<RtStop>();
		departurePerStation = new HashMap<String, List<RtStop>>();
	}

	@Override
	public void arriveStation(Place place, TrainJourney trainJourney) {
		RtStop rtStop = trainJourney.getRtStop();
		logger.info("ArriveAtStation={}, line={}, expArriveTime={}, dest={}",
				place.getPlaceId(), rtStop.getPublishedLineName(), rtStop
						.getExpectedArrivalTime().toString(), rtStop
						.getDestinationName());
		arrivalList.add(trainJourney.getRtStop());
	}

	@Override
	public void leaveStation(Place place, TrainJourney trainJourney) {
		RtStop rtStop = trainJourney.getRtStop();
		logger.info(
				"DepartureAtStation={}, line={}, expDepartureTime={}, dest={}",
				place.getPlaceId(), rtStop.getPublishedLineName(), rtStop
						.getExpectedDepartureTime().toString(), rtStop
						.getDestinationName());
		departureList.add(trainJourney.getRtStop());
		List<RtStop> stationDepartureList = departurePerStation.get(place
				.getPlaceId());
		if (stationDepartureList == null) {
			stationDepartureList = new ArrayList<RtStop>();
		}
		stationDepartureList.add(trainJourney.getRtStop());
	}

	public List<RtStop> getArrivalList() {
		return arrivalList;
	}

	public void setArrivalList(List<RtStop> arrivalList) {
		this.arrivalList = arrivalList;
	}

	public List<RtStop> getDepartureList() {
		return departureList;
	}

	public void setDepartureList(List<RtStop> departureList) {
		this.departureList = departureList;
	}

	public Map<String, List<RtStop>> getDeparturePerStation() {
		return departurePerStation;
	}

	public void setDeparturePerStation(
			Map<String, List<RtStop>> departurePerStation) {
		this.departurePerStation = departurePerStation;
	}

}
