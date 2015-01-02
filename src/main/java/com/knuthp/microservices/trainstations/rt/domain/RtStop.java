package com.knuthp.microservices.trainstations.rt.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class RtStop {

//	private boolean monitored;
	private String publishedLineName;
	private boolean vehicleAtStop;
	
	
//	public boolean isMonitored() {
//		return monitored;
//	}
//	public void setMonitored(boolean monitored) {
//		this.monitored = monitored;
//	}
	public String getPublishedLineName() {
		return publishedLineName;
	}
	public void setPublishedLineName(String publishedLineName) {
		this.publishedLineName = publishedLineName;
	}
	public boolean isVehicleAtStop() {
		return vehicleAtStop;
	}
	public void setVehicleAtStop(boolean vehicleAtStop) {
		this.vehicleAtStop = vehicleAtStop;
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
