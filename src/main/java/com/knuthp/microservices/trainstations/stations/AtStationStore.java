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
	private DelayQueue<StationEvent> stationEventQueue;

	public AtStationStore() {
		trainAtStationListeners = new ArrayList<TrainAtStationListener>();
		stationEventQueue = new DelayQueue<AtStationStore.StationEvent>();
	}

	public void addTrainAtStationListener(
			TrainAtStationListener trainAtStationListener) {
		trainAtStationListeners.add(trainAtStationListener);
	}

	public void addDeparture(String placeId, RtStop rtStop) {
		ArrivalEvent arrivalEvent = new ArrivalEvent(placeId, rtStop);
		stationEventQueue.add(arrivalEvent);
		DepartureEvent departureEvent = new DepartureEvent(placeId, rtStop);
		stationEventQueue.add(departureEvent);
	}

	public void updateDeparture(String placeId, RtStop rtStop) {
		ArrivalEvent arrivalEvent = new ArrivalEvent(placeId, rtStop);
		stationEventQueue.remove(arrivalEvent);
		stationEventQueue.add(arrivalEvent);
		
		DepartureEvent departureEvent = new DepartureEvent(placeId, rtStop);
		stationEventQueue.remove(departureEvent);
		stationEventQueue.add(departureEvent);
	}

	public void run(int numberOfPolls) {
		for (int i = 0; i < numberOfPolls; i++) {
			StationEvent stationEvent = stationEventQueue.poll();
			if (stationEvent != null) {
				for (TrainAtStationListener trainAtStationListener : trainAtStationListeners) {
					stationEvent.fireListener(trainAtStationListener);
				}
			}
		}
	}

	public enum EventType {
		ARRIVAL, DEPARTURE
	}

	abstract class StationEvent implements Delayed {
		protected final String placeId;
		protected final OffsetDateTime expected;
		protected final OffsetDateTime aimed;
		protected final String journeyId;
		protected final RtStop rtStop;
		protected final EventType eventType;

		public StationEvent(final String placeId, final RtStop rtStop,
				final OffsetDateTime expected, final OffsetDateTime aimed,
				final EventType eventType) {
			this.placeId = placeId;
			this.expected = expected;
			this.aimed = aimed;
			this.journeyId = rtStop.getJourneyId();
			this.rtStop = rtStop;
			this.eventType = eventType;
		}

		public abstract void fireListener(TrainAtStationListener trainAtStationListener);

		@Override
		public int compareTo(Delayed arg0) {
			if (arg0 instanceof ArrivalEvent) {
				ArrivalEvent other = (ArrivalEvent) arg0;
				return getExpected().compareTo(other.getArrival());
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
			return OffsetDateTime.now().until(getExpected(),
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
			return getExpected();
		}

		public OffsetDateTime getAimed() {
			return aimed;
		}

		public String getJourneyId() {
			return journeyId;
		}

		public RtStop getRtStop() {
			return rtStop;
		}

		@Override
		public int hashCode() {
			return HashCodeBuilder.reflectionHashCode(this, "expected",
					"rtStop");
		}

		@Override
		public boolean equals(Object obj) {
			return EqualsBuilder.reflectionEquals(this, obj, "expected",
					"rtStop");
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}

		public OffsetDateTime getExpected() {
			return expected;
		}
	}

	class ArrivalEvent extends StationEvent {
		public ArrivalEvent(final String placeId, final RtStop rtStop) {
			super(placeId, rtStop, rtStop.getExpectedArrivalTime(), rtStop
					.getAimedArrivalTime(), EventType.ARRIVAL);
		}

		public void fireListener(TrainAtStationListener trainAtStationListener) {
			trainAtStationListener.arriveStation(new Place(getPlaceId()),
					new TrainJourney(getRtStop()));
		}

	}

	class DepartureEvent extends StationEvent {
		public DepartureEvent(final String placeId, final RtStop rtStop) {
			super(placeId, rtStop, rtStop.getExpectedDepartureTime(), rtStop
					.getAimedDepartureTime(), EventType.DEPARTURE);
		}

		public void fireListener(TrainAtStationListener trainAtStationListener) {
			trainAtStationListener.leaveStation(new Place(getPlaceId()),
					new TrainJourney(getRtStop()));
		}

	}

	@Override
	public void run() {
		while (true) {
			try {
				StationEvent stationEvent = stationEventQueue.take();
				if (stationEvent != null) {
					for (TrainAtStationListener trainAtStationListener : trainAtStationListeners) {
						stationEvent.fireListener(trainAtStationListener);
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error("Thread interrupted", e);
			}
		}
	}

}
