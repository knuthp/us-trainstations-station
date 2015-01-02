package com.knuthp.microservices;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

@Component
public class AppReceiver {
	private static final String DEFAULT_AMQP_RX = "amqp://UITrHgLT:yGYKAijJ5OLNrY5ob0gXgFXN5oViPLGz@slow-vervain-44.bigwig.lshift.net:10923/ix2hR1tr2hmP";
	private static final Logger logger = LoggerFactory
			.getLogger(AppReceiver.class);

	public AppReceiver() throws Exception {
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

		// set up the listener and container
		final SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(
				cf);
		Object listener = new Object() {
			@SuppressWarnings("unused")
			public void handleMessage(byte[] foo)
					throws UnsupportedEncodingException {
				String str = new String(foo, "UTF-8");
				System.out.println(foo.getClass().getCanonicalName() + " : "
						+ foo + str);
			}
		};
		MessageListenerAdapter adapter = new MessageListenerAdapter(listener);
		container.setMessageListener(adapter);
		container.setQueueNames("train.stations.rt.station");

		// register a shutdown hook with the JVM
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				logger.info("Shutting down message listener");
				container.shutdown();
			}
		});
		container.start();
	}

}
