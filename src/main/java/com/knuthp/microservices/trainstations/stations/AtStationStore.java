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
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.knuthp.microservices.trainstations.rt.domain.RtStop;

public class AtStationStore implements Runnable {
	private Logger logger = LoggerFactory.getLogger(AtStationStore.class);
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
		ArrivalEvent arrivalEvent = new ArrivalEvent(placeId, rtStop);
		arrivalQueue.remove(arrivalEvent);
		arrivalQueue.add(arrivalEvent);
	}

	public void run(int numberOfPolls) {
		for (int i = 0; i < numberOfPolls; i++) {
			ArrivalEvent arrivalEvent = arrivalQueue.poll();
			if (arrivalEvent != null) {
				for (TrainAtStationListener trainAtStationListener : trainAtStationListeners) {
					trainAtStationListener.arriveStation(
							new Place(arrivalEvent.getPlaceId()),
							new TrainJourney(arrivalEvent.getRtStop()));
				}
			}
		}
	}

	class ArrivalEvent implements Delayed {
		private final String placeId;
		private final OffsetDateTime expectedArrivalTime;
		private final OffsetDateTime aimedArrival;
		private final String journeyId;
		private final RtStop rtStop;

		public ArrivalEvent(final String placeId, final RtStop rtStop) {
			this.placeId = placeId;
			this.expectedArrivalTime = rtStop.getExpectedArrivalTime();
			this.aimedArrival = rtStop.getAimedArrivalTime();
			this.journeyId = rtStop.getJourneyId();
			this.rtStop = rtStop;
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

		public OffsetDateTime getArrival() {
			return expectedArrivalTime;
		}

		public OffsetDateTime getAimedArrival() {
			return aimedArrival;
		}

		public String getJourneyId() {
			return journeyId;
		}

		public RtStop getRtStop() {
			return rtStop;
		}

		@Override
		public int hashCode() {
			return HashCodeBuilder.reflectionHashCode(this,
					"expectedArrivalTime", "rtStop");
		}

		@Override
		public boolean equals(Object obj) {
			return EqualsBuilder.reflectionEquals(this, obj,
					"expectedArrivalTime", "rtStop");
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				ArrivalEvent arrivalEvent = arrivalQueue.take();
				if (arrivalEvent != null) {
					for (TrainAtStationListener trainAtStationListener : trainAtStationListeners) {
						trainAtStationListener.arriveStation(new Place(
								arrivalEvent.getPlaceId()), new TrainJourney(
								arrivalEvent.getRtStop()));
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error("Thread interrupted", e);
			}
		}
	}

}
