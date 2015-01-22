package com.knuthp.microservices.trainstations.stations;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.knuthp.microservices.trainstations.rt.domain.RtDepartures;
import com.knuthp.microservices.trainstations.rt.domain.RtStop;

public class StationCache {
	private static final Logger LOG = LoggerFactory
			.getLogger(StationCache.class);

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
			LOG.info("New place, adding all: placeId="
					+ rtDepartures.getPlaceId());
			placeStops = new HashMap<String, RtStop>();
			for (RtStop rtStop : rtDepartures.getRtStopList()) {
				placeStops.put(rtStop.getJourneyId(), rtStop);
			}
			newList.addAll(rtDepartures.getRtStopList());
			cache.put(rtDepartures.getPlaceId(), placeStops);
		} else {
			for (RtStop rtStop : rtDepartures.getRtStopList()) {
				if (rtStop.isMonitored()) {
					RtStop oldRtStop = placeStops.get(rtStop.getJourneyId());
					if (oldRtStop == null) {
						LOG.info("New journey: placeId="
								+ rtDepartures.getPlaceId() + ", journeyId="
								+ rtStop.getJourneyId() + ", line="
								+ rtStop.getPublishedLineName());
						placeStops.put(rtStop.getJourneyId(), rtStop);
						newList.add(rtStop);
					} else if (!oldRtStop.getExpectedArrivalTime().equals(
							rtStop.getExpectedArrivalTime())) {
						LOG.info("Updated journey: placeId="
								+ rtDepartures.getPlaceId() + ", journeyId="
								+ rtStop.getJourneyId() + ", line="
								+ rtStop.getPublishedLineName());
						LOG.info("Diff: old="
								+ oldRtStop.getExpectedArrivalTime()
								+ ", new="
								+ rtStop.getExpectedArrivalTime()
								+ ", diff="
								+ Duration.between(
										oldRtStop.getExpectedArrivalTime(),
										rtStop.getExpectedArrivalTime()));
						placeStops.put(rtStop.getJourneyId(), rtStop);
						updateList.add(rtStop);
					} else {
						LOG.debug("Equal journey: placeId="
								+ rtDepartures.getPlaceId() + ", journeyId="
								+ rtStop.getJourneyId() + ", line="
								+ rtStop.getPublishedLineName());
					}
				} else {
					LOG.debug("Not monitored: placeId="
							+ rtDepartures.getPlaceId() + ", destination="
							+ rtStop.getDestinationName() + ", line="
							+ rtStop.getPublishedLineName());
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
