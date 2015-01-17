package com.knuthp.microservices.trainstations.stations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

public class NsObjectMapper extends ObjectMapper {
	private static final long serialVersionUID = 1L;

	public NsObjectMapper() {
		super();
		registerModule(new JSR310Module());
		disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}
}
