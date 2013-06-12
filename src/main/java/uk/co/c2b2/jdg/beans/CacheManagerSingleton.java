package uk.co.c2b2.jdg.beans;

import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.logging.Logger;

import uk.co.c2b2.jdg.domain.Match;
import uk.co.c2b2.jdg.externalizers.MatchExternalizer;

/**
 * <pre>
 * Singleton EJB responsible for 
 * - configuring JBoss Enterprise Data Grid CacheManager in Library Mode
 * - creating a distributed cache to hold the events
 * 
 * @author maddy
 * 
 */
@Startup
@Singleton
public class CacheManagerSingleton {

	private static final Logger LOG = Logger.getLogger(CacheManagerSingleton.class);

	private static final String CACHE_NAME = "jdg-event-cache";
	private static final String CLUSTER_NAME = "jdg-websocket-cluster";

	private static EmbeddedCacheManager manager;
	private static Cache<String, Match> cache;

	@EJB
	private EventNotificationSenderBean queueSenderSessionBean;

	@PostConstruct
	public void init() {

		LOG.info("initializing CacheManager");
		try {

			GlobalConfiguration configuration = new GlobalConfigurationBuilder().clusteredDefault().transport().clusterName(CLUSTER_NAME).defaultTransport()
					.transport().globalJmxStatistics().enabled(true).jmxDomain(UUID.randomUUID().toString()).serialization()
					.addAdvancedExternalizer(new MatchExternalizer()).build();

			manager = new DefaultCacheManager(configuration);

			Configuration eventCacheConfiguration = new ConfigurationBuilder().indexing().enabled(false).jmxStatistics().enabled(true).clustering()
					.cacheMode(CacheMode.DIST_ASYNC).l1().disable().hash().numOwners(2).groups().enabled(true).build();

			manager.defineConfiguration(CACHE_NAME, eventCacheConfiguration);

			cache = manager.getCache(CACHE_NAME);

			LOG.info("created cache " + CACHE_NAME);

			cache.addListener(queueSenderSessionBean);

			LOG.info("associated listener");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Cache<String, Match> getCache() {
		return cache;
	}

	@PreDestroy
	public void shutdown() {

		LOG.info("shutting down");

		if (cache != null) {
			cache.stop();
			LOG.info("stopped cache " + CACHE_NAME);
		}
		if (manager != null) {
			manager.stop();
			LOG.info("stopped cache manager");
		}
	
	}

}
