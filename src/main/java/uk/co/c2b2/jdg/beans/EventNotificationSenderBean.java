package uk.co.c2b2.jdg.beans;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.jms.client.HornetQConnectionFactory;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.jboss.logging.Logger;

import uk.co.c2b2.jdg.ResponseSerializer;
import uk.co.c2b2.jdg.domain.Match;
import uk.co.c2b2.jdg.domain.MatchUpdate;

/**
 * <pre>
 * This is our Data Grid listener
 * When an entry is modified we construct an event and produce a JMS message to send into out HornetQ topic
 * HornetQ de-duplicates the message for us
 * 
 * @author maddy
 * 
 */
@Listener
@Singleton
public class EventNotificationSenderBean {

	private static final Logger LOG = Logger.getLogger(EventNotificationSenderBean.class);

	private Connection connection = null;
	private Session session = null;
	private MessageProducer producer = null;

	@PostConstruct
	public void initialize() throws JMSException {

		LOG.info("initializing");

		Map<String, Object> connectionParams = new HashMap<String, Object>();
		connectionParams.put(org.hornetq.core.remoting.impl.netty.TransportConstants.PORT_PROP_NAME, 6445);
		connectionParams.put(org.hornetq.core.remoting.impl.netty.TransportConstants.HOST_PROP_NAME, "localhost");
		TransportConfiguration transportConfiguration = new TransportConfiguration("org.hornetq.core.remoting.impl.netty.NettyConnectorFactory",
				connectionParams);
		HornetQConnectionFactory cf = HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, transportConfiguration);
		connection = cf.createConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Topic topic = session.createTopic("InfinispanNotificationTopic");
		producer = session.createProducer(topic);
		connection.start();
		
	}

	@PreDestroy
	public void shutdown() throws JMSException {

		LOG.info("shutting down");

		if (producer != null) {
			producer.close();
			LOG.info("closed producer");
		}
		if (session != null) {
			session.close();
			LOG.info("closed session");
		}
		if (connection != null) {
			connection.close();
			LOG.info("closed connection");
		}
	}

	@CacheEntryModified
	public void logModifiedEvent(CacheEntryModifiedEvent<String, Match> event) throws IOException {
		if (!event.isPre()) {
			LOG.info("received update event");
			MatchUpdate matchUpdate = new MatchUpdate(event.getValue());
			sendMessage(matchUpdate);
		}
	}

	public void sendMessage(MatchUpdate matchUpdate) throws IOException {
		try {
			TextMessage textMessage = session.createTextMessage(ResponseSerializer.getInstance().serialize(matchUpdate));
			textMessage.setStringProperty(org.hornetq.core.message.impl.MessageImpl.HDR_DUPLICATE_DETECTION_ID.toString(),
					matchUpdate.getJmsDuplicateDetectionId());
			LOG.info("sending update event " + textMessage.getText());
			producer.send(textMessage, DeliveryMode.NON_PERSISTENT, 1, 10000);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
