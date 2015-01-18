package com.knuthp.microservices.trainstations.stations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.knuthp.microservices.trainstations.rt.domain.RtDepartures;
import com.knuthp.microservices.trainstations.rt.domain.RtStop;

public class StationCache {
	public enum FilterStatus {
		NEW(), UPDATE(), DELETE(), EQUAL();
	}

	private Map<String, Map<String, RtStop>> cache;

	public StationCache() {
		cache = new HashMap<String, Map<String, RtStop>>();
	}

	public Map<FilterStatus, List<RtStop>> filter(RtDepartures rtDepartures) {
		List<RtStop> newList = new ArrayList<RtStop>();
		List<RtStop> updateList = new ArrayList<RtStop>();
		Map<String, RtStop> placeStops = cache.get(rtDepartures.getPlaceId());
		if (placeStops == null) {
			for (RtStop rtStop : rtDepartures.getRtStopList()) {
				placeStops = new HashMap<String, RtStop>();
				placeStops.put(rtStop.getJourneyId(), rtStop);
			}
			newList.addAll(rtDepartures.getRtStopList());
			cache.put(rtDepartures.getPlaceId(), placeStops);
		} else {
			for (RtStop rtStop : rtDepartures.getRtStopList()) {
				RtStop oldRtStop = placeStops.get(rtStop.getJourneyId());
				if (oldRtStop == null) {
					placeStops.put(rtDepartures.getPlaceId(), rtStop);
					newList.add(rtStop);
				} else if (!oldRtStop.equals(rtStop)) {
					placeStops.put(rtDepartures.getPlaceId(), rtStop);
					updateList.add(rtStop);
				}
			}
		}
		Map<FilterStatus, List<RtStop>> filteredMap = new HashMap<StationCache.FilterStatus, List<RtStop>>();
		if (!newList.isEmpty()) {
			filteredMap.put(FilterStatus.NEW, newList);
		}
		if (!updateList.isEmpty()) {
			filteredMap.put(FilterStatus.UPDATE, updateList);
		}
		return filteredMap;
	}
}
