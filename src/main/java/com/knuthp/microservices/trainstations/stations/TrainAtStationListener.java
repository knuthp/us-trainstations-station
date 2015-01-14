package com.knuthp.microservices.trainstations.stations;

public interface TrainAtStationListener {
	void arriveStation(Place place, TrainJourney trainJourney);
	void leaveStation(Place place, TrainJourney trainJourney);
}
