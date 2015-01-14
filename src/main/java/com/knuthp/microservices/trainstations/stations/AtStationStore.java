package com.knuthp.microservices.trainstations.stations;

import java.util.ArrayList;
import java.util.List;

import com.knuthp.microservices.trainstations.rt.domain.RtDepartures;

public class AtStationStore {
	private List<TrainAtStationListener> trainAtStationListeners;
	
	public AtStationStore() {
		trainAtStationListeners = new ArrayList<TrainAtStationListener>();
	}

	public void addTrainAtStationListener(
			TrainAtStationListener trainAtStationListener) {
		trainAtStationListeners.add(trainAtStationListener);
	}

	public void updateDepartures(RtDepartures rtDepartures) {
		// TODO Auto-generated method stub
		
	}

	public void run(int numberOfPolls) {
		// TODO Auto-generated method stub
		
	}
	
	

}
