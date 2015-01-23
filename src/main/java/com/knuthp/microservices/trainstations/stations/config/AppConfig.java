package com.knuthp.microservices.trainstations.stations.config;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.knuthp.microservices.trainstations.stations.AtStationStore;
import com.knuthp.microservices.trainstations.stations.MessageListenerImplementation;
import com.knuthp.microservices.trainstations.stations.RtStationListener;
import com.knuthp.microservices.trainstations.stations.StationCache;
import com.knuthp.microservices.trainstations.stations.TrainAtStationListener;
import com.knuthp.microservices.trainstations.stations.TrainAtStationStore;

@Configuration
public class AppConfig {
	private static final String DEFAULT_AMQP_RX = "amqp://UITrHgLT:yGYKAijJ5OLNrY5ob0gXgFXN5oViPLGz@slow-vervain-44.bigwig.lshift.net:10923/ix2hR1tr2hmP";

	@Bean
	public ConnectionFactory connectionFactory() {
		final URI rabbitMqUrl;
		try {
			rabbitMqUrl = new URI(DEFAULT_AMQP_RX);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		CachingConnectionFactory cf = new CachingConnectionFactory();
		cf.setUsername(rabbitMqUrl.getUserInfo().split(":")[0]);
		cf.setPassword(rabbitMqUrl.getUserInfo().split(":")[1]);
		cf.setHost(rabbitMqUrl.getHost());
		cf.setPort(rabbitMqUrl.getPort());
		cf.setVirtualHost(rabbitMqUrl.getPath().substring(1));
		return cf;
	}

	@Bean
	public AmqpAdmin amqpAdmin() {
		return new RabbitAdmin(connectionFactory());
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		return new RabbitTemplate(connectionFactory());
	}

	@Bean
	public Queue myQueue() {
		return new Queue("train.stations.rt.station");
	}

	@Bean
	public SimpleMessageListenerContainer messageListenerContainer() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory());
		container.setQueues(myQueue());
		container.setMessageListener(exampleListener());
		return container;
	}

	@Bean
	public MessageListenerImplementation exampleListener() {
		return new MessageListenerImplementation(rtStationListener());
	}

	@Bean
	public StationCache stationCache() {
		return new StationCache();
	}

	@Bean
	public AtStationStore atStationStore() {
		AtStationStore atStationStore = new AtStationStore();
		atStationStore.addTrainAtStationListener(trainAtStationStore());
		return atStationStore;
	}

	@Bean
	public TrainAtStationStore trainAtStationStore() {
		return new TrainAtStationStore();
	}

	@Bean
	public RtStationListener rtStationListener() {
		return new RtStationListener(stationCache(), atStationStore());
	}

}
