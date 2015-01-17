package com.knuthp.microservices.trainstations.stations;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.knuthp.microservices.trainstations.rt.domain.RtStop;

public class AtStationStore {
	private List<TrainAtStationListener> trainAtStationListeners;
	private DelayQueue<ArrivalEvent> arrivalQueue;

	public AtStationStore() {
		trainAtStationListeners = new ArrayList<TrainAtStationListener>();
		arrivalQueue = new DelayQueue<AtStationStore.ArrivalEvent>();
	}

	public void addTrainAtStationListener(
			TrainAtStationListener trainAtStationListener) {
		trainAtStationListeners.add(trainAtStationListener);
	}

	public void addDeparture(String placeId, RtStop rtStop) {
		ArrivalEvent arrivalEvent = new ArrivalEvent(placeId, rtStop);
		arrivalQueue.add(arrivalEvent);
	}
	
	public void updateDeparture(String placeId, RtStop rtStop) {
		ArrivalEvent arrivalEvent = new ArrivalEvent(placeId,
				rtStop);
		arrivalQueue.remove(arrivalEvent);
		arrivalQueue.add(arrivalEvent);
	}


	public void run(int numberOfPolls) {
		for (int i = 0; i < numberOfPolls; i++) {
			ArrivalEvent arrivalEvent = arrivalQueue.poll();
			if (arrivalEvent != null) {
				for (TrainAtStationListener trainAtStationListener : trainAtStationListeners) {
					trainAtStationListener.arriveStation(new Place("asker"),
							new TrainJourney());
				}
			}
		}

	}

	class ArrivalEvent implements Delayed {
		private String placeId;
		private OffsetDateTime expectedArrivalTime;
		private OffsetDateTime aimedArrival;

		public ArrivalEvent(String placeId, RtStop rtStop) {
			this.placeId = placeId;
			this.expectedArrivalTime = rtStop.getExpectedArrivalTime();
			this.setAimedArrival(rtStop.getAimedArrivalTime());
		}

		@Override
		public int compareTo(Delayed arg0) {
			if (arg0 instanceof ArrivalEvent) {
				ArrivalEvent other = (ArrivalEvent) arg0;
				return expectedArrivalTime.compareTo(other.getArrival());
			} else {
				long difference = getDelay(TimeUnit.MILLISECONDS)
						- arg0.getDelay(TimeUnit.MILLISECONDS);
				if (difference < 0) {
					return -1;
				} else if (difference == 0) {
					return 0;
				} else {
					return 1;
				}
			}

		}

		@Override
		public long getDelay(TimeUnit unit) {
			return OffsetDateTime.now().until(expectedArrivalTime,
					convertToChronoUnits(unit));
		}

		private ChronoUnit convertToChronoUnits(TimeUnit timeUnit) {
			switch (timeUnit) {
			case DAYS:
				return ChronoUnit.DAYS;
			case HOURS:
				return ChronoUnit.HOURS;
			case MICROSECONDS:
				return ChronoUnit.MICROS;
			case MILLISECONDS:
				return ChronoUnit.MILLIS;
			case MINUTES:
				return ChronoUnit.MINUTES;
			case NANOSECONDS:
				return ChronoUnit.NANOS;
			case SECONDS:
				return ChronoUnit.SECONDS;
			default:
				throw new RuntimeException(
						"Not supported time mapping to ChronoUnit: " + timeUnit);
			}
		}

		public String getPlaceId() {
			return placeId;
		}

		public void setPlace(String placeId) {
			this.placeId = placeId;
		}

		public OffsetDateTime getArrival() {
			return expectedArrivalTime;
		}

		public void setArrival(OffsetDateTime arrival) {
			this.expectedArrivalTime = arrival;
		}

		public OffsetDateTime getAimedArrival() {
			return aimedArrival;
		}

		public void setAimedArrival(OffsetDateTime aimedArrival) {
			this.aimedArrival = aimedArrival;
		}

		@Override
		public int hashCode() {
			return HashCodeBuilder.reflectionHashCode(this, "expectedArrivalTime");
		}
		
		@Override
		public boolean equals(Object obj) {
			return EqualsBuilder.reflectionEquals(this, obj, "expectedArrivalTime");
		}

	}

}
