package uk.co.c2b2.jdg;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.inject.Qualifier;

import uk.co.c2b2.jdg.websocket.WebSocketEndpoint;

/**
 * 
 * CDI Event Classifier - emitted on receipt of JMS message to notify Observers 
 * @see {@link WebSocketEndpoint}
 * 
 * @author maddy
 *
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
public @interface InfinispanUpdateEvent {
}
