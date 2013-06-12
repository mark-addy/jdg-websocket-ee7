package uk.co.c2b2.jdg.websocket;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.infinispan.Cache;

import uk.co.c2b2.jdg.InfinispanUpdateEvent;
import uk.co.c2b2.jdg.ResponseSerializer;
import uk.co.c2b2.jdg.beans.CacheManagerSingleton;
import uk.co.c2b2.jdg.domain.Match;
import uk.co.c2b2.jdg.domain.MatchList;

/**
 * <pre>
 * JSR 356 Websocket endpoint 
 * 
 * Note that wildfly (8.0.0.Alpha1) does not currently support CDI injection into @ServerEndpoint
 * 
 * The manual InitialContext lookup should be replaced with an @Inject or @EJB dependency injection once support is added
 * For example:
 * 
 * @Inject
 * private QueueSenderSessionBean senderBean;
 * 
 * @author maddy
 * 
 */
@ServerEndpoint("/websocket")
public class WebSocketEndpoint implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1983624359866520640L;

	private static final Logger LOG = Logger.getLogger(WebSocketEndpoint.class.getName());

	private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());

	@OnOpen
	public void onOpen(final Session session) {
		try {

			sessions.add(session);

			CacheManagerSingleton cacheManagerSingleton = getBean(CacheManagerSingleton.class);
			Cache<String, Match> cache = cacheManagerSingleton.getCache();

			List<Match> matchList = new ArrayList<Match>();
			for (Match match : MatchList.selection.getData()) {
				matchList.add(cache.get(match.getKey()));
			}

			MatchList selection = new MatchList(matchList);
	
			LOG.info("onOpen sending match list: " + selection.toString());
			session.getBasicRemote().sendText(ResponseSerializer.getInstance().serialize(selection));

		} catch (Exception ex) {
			Logger.getLogger(WebSocketEndpoint.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@OnMessage
	public void onMessage(final String message, final Session session) {
		LOG.info("onMessage");
	}

	@OnClose
	public void onClose(final Session session) {
		LOG.info("onClose removing session: " + session.getId());
		;
		try {
			sessions.remove(session);
		} catch (Exception exception) {
			LOG.log(Level.SEVERE, exception.getMessage(), exception);
		}
	}

	public void onInfinispanUpdateEventMessage(@Observes @InfinispanUpdateEvent Message msg) {
		Logger.getLogger(WebSocketEndpoint.class.getName()).log(Level.INFO, "Got JMS Message at WebSocket!");
		try {
			for (Session session : sessions) {
				LOG.info("onInfinispanUpdateEventMessage sending : " + ((TextMessage) msg).getText() + " to session: " + session.getId());
				session.getBasicRemote().sendText(((TextMessage) msg).getText());
			}
		} catch (IOException | JMSException exception) {
			LOG.log(Level.SEVERE, exception.getMessage(), exception);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T getBean(Class<T> clazz) {
		try {
			String name = clazz.getName().replaceAll(clazz.getPackage().getName() + ".", "");
			InitialContext context = new InitialContext();
			T bean = (T) context.lookup("java:global/jdg-websocket/" + name);
			return bean;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
