package com.knuthp.microservices.trainstations.stations;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.knuthp.microservices.trainstations.rt.domain.RtDepartures;
import com.knuthp.microservices.trainstations.rt.domain.RtStop;
import com.knuthp.microservices.trainstations.stations.StationCache.FilterStatus;

public class RtStationListener {
	private Logger logger = LoggerFactory.getLogger(RtStationListener.class);
	private StationCache stationCache;
	private AtStationStore atStationStore;

	public RtStationListener(StationCache stationCache,
			AtStationStore atStationStore) {
		this.stationCache = stationCache;
		this.atStationStore = atStationStore;
		Thread t = new Thread(atStationStore);
		t.start();
	}

	public void update(RtDepartures rtDepartures) {
		Map<FilterStatus, List<RtStop>> filtered = stationCache
				.filter(rtDepartures);

		List<RtStop> newList = filtered.get(FilterStatus.NEW);
		if (newList != null) {
			for (RtStop rtStop : newList) {
				atStationStore.addDeparture(rtDepartures.getPlaceId(), rtStop);
			}
		}
		List<RtStop> updateList = filtered.get(FilterStatus.UPDATE);
		if (updateList != null) {
			for (RtStop rtStop : updateList) {
				atStationStore.updateDeparture(rtDepartures.getPlaceId(),
						rtStop);
			}
		}

	}

}
