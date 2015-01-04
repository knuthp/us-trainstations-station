package com.knuthp.microservices.trainstations.stations;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.knuthp.microservices.trainstations.stations.config.AppConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class SpringJavaConfigTest {
	private static final String QUEUE_NAME = "test";

	private Logger logger = LoggerFactory.getLogger(SpringJavaConfigTest.class);

	@Autowired
	private AppConfig appConfig;

	private MessageProperties props;

	private Message message;

	@Test
	public void testSendAndPollUsingReceiveAndConvert() {
		appConfig.amqpAdmin().declareQueue(new Queue(QUEUE_NAME));
		appConfig.rabbitTemplate().send(QUEUE_NAME, message);

		Object receiveAndConvert = appConfig.rabbitTemplate()
				.receiveAndConvert(QUEUE_NAME);

		assertEquals(String.class, receiveAndConvert.getClass());
		assertEquals("foo", receiveAndConvert);
	}

	
	@Test
	public void testSendAndPollUsingReceive() {
		appConfig.amqpAdmin().declareQueue(new Queue(QUEUE_NAME));
		appConfig.rabbitTemplate().send(QUEUE_NAME, message);

		 Message received = appConfig.rabbitTemplate()
				.receive(QUEUE_NAME);

		assertEquals("123", received.getMessageProperties().getMessageId());
		assertArrayEquals("foo".getBytes(), received.getBody().clone());
	}
		
	
	@Before
	public void setUp() {
		props = MessagePropertiesBuilder.newInstance()
				.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
				.setMessageId("123").setHeader("bar", "baz").build();
		message = MessageBuilder.withBody("foo".getBytes())
				.andProperties(props).build();
	}

	@After
	public void tearDown() {
		appConfig.amqpAdmin().deleteQueue(QUEUE_NAME);
	}
}
