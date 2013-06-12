package uk.co.c2b2.jdg.beans;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.jboss.logging.Logger;

import uk.co.c2b2.jdg.InfinispanUpdateEvent;

/**
 * <pre>
 * Consumer of de-duplicated messages from our hornetQ instance
 * When messages are received a CDI event fires 
 * The event is observered in the Websocket @ServerEndpoint implementation and pushed to the clients
 * 
 * @author maddy
 *
 */
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@MessageDriven(name = "EventNotificationMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "topic/InfinispanNotificationTopic"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "connectorClassName", propertyValue = "org.hornetq.core.remoting.impl.netty.NettyConnectorFactory"),
		@ActivationConfigProperty(propertyName = "user", propertyValue = "guest"),
		@ActivationConfigProperty(propertyName = "password", propertyValue = "guest"),
		@ActivationConfigProperty(propertyName = "connectionParameters", propertyValue = "host=localhost;port=6445")
})
public class EventNotificationMDB implements MessageListener {
	
	private static final Logger LOG = Logger.getLogger(EventNotificationMDB.class);

	/**
	 * CDI Event
	 * Fired by the onMessage method
	 */
    @Inject
    @InfinispanUpdateEvent
    private Event<Message> event;

    @Override
    public void onMessage(Message message) {
    	LOG.info("Received message : " + message.toString());
        event.fire(message);
    }
}
