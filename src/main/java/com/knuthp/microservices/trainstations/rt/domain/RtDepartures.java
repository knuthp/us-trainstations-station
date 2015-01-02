package com.knuthp.microservices.trainstations.rt.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class RtDepartures {

	private String placeId;
	private List<RtStop> rtStopList;
	
	public RtDepartures() {
		setRtStopList(new ArrayList<RtStop>());
	}

	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	public void addStop(RtStop rtStop) {
		getRtStopList().add(rtStop);
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

	public List<RtStop> getRtStopList() {
		return rtStopList;
	}

	public void setRtStopList(List<RtStop> rtStopList) {
		this.rtStopList = rtStopList;
	}
}
