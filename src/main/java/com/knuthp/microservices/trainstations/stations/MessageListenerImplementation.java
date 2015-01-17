package com.knuthp.microservices.trainstations.stations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knuthp.microservices.trainstations.rt.domain.RtDepartures;

public class MessageListenerImplementation implements MessageListener {

	private Logger logger = LoggerFactory
			.getLogger(MessageListenerImplementation.class);
	private RtStationListener rtStationListener;
	private ObjectMapper mapper;

	public MessageListenerImplementation(RtStationListener rtStationListener) {
		this.rtStationListener = rtStationListener;
		mapper = new NsObjectMapper();
	}

	public void onMessage(Message message) {
		try {
			RtDepartures rtDepartures = mapper.readValue(message.getBody(),
					RtDepartures.class);
			rtStationListener.update(rtDepartures);
		} catch (Exception e) {
			logger.error("Failed converting body", e);
		}
	}
}