package uk.co.c2b2.jdg.beans;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.infinispan.Cache;
import org.jboss.logging.Logger;

import uk.co.c2b2.jdg.domain.Match;
import uk.co.c2b2.jdg.domain.MatchList;
import uk.co.c2b2.jdg.domain.tasks.MatchOddsUpdater;

/**
 * <pre>
 * Singleton EJB responsible for 
 * - populating the cache with some arbitrary matches
 * - starting a back-ground update thread to generate changes to match odds
 *   this thread generates the events that get pushed to the client
 *   Only runs on one node - just for the demo
 * 
 * @author maddy
 * 
 */
@Startup
@Singleton
public class CacheUpdaterSingleton {

	private static final Logger LOG = Logger.getLogger(CacheUpdaterSingleton.class);

	@EJB
	private CacheManagerSingleton cacheManagerSingleton;

	private MatchOddsUpdater updater;

	@PostConstruct
	public void init() {

		if (System.getProperty("jboss.server.name").equals("server-one")) {

			LOG.info("initializing CacheUpdaterSingleton");

			Cache<String, Match> cache = cacheManagerSingleton.getCache();

			for (Match match : MatchList.selection.getData()) {
				LOG.info("putting match into cache: " + match.toString());
				cache.put(match.getKey(), match);
			}

			LOG.info("populated cache with " + cache.size() + " events");

			updater = new MatchOddsUpdater(cache);
			Thread oddsUpdaterThread = new Thread(updater);
			oddsUpdaterThread.start();
			LOG.info("started background event odds update");
		
		}

	}

	@PreDestroy
	public void shutdown() {
		LOG.info("shutting down");

		if (updater != null) {
			updater.stop();
			LOG.info("stopped background event odds update");
		}

	}
}
