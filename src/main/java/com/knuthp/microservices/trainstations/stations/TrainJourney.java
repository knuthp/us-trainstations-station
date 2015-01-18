package com.knuthp.microservices.trainstations.stations;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.knuthp.microservices.trainstations.rt.domain.RtStop;

public class TrainJourney {

	private final RtStop rtStop;

	public TrainJourney(final RtStop rtStop) {
		this.rtStop = rtStop;
	}

	public RtStop getRtStop() {
		return rtStop;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
