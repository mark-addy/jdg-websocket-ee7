package uk.co.c2b2.jdg.domain.tasks;

import java.util.Random;

import org.infinispan.Cache;

import uk.co.c2b2.jdg.domain.Match;
import uk.co.c2b2.jdg.domain.MatchList;
import uk.co.c2b2.jdg.domain.MatchOdds;
/**
 * Just for the demo - thread that updates random items in the cache...
 * @author maddy
 *
 */
public class MatchOddsUpdater implements Runnable {

	private Cache<String, Match> cache;
	private static final Random random = new Random();
	private boolean stop = false;

	public MatchOddsUpdater(Cache<String, Match> cache) {
		this.cache = cache;
	}
	
	public void stop() {
		this.stop = true;
	}

	@Override
	public void run() {

		while (!stop) {

			/* change a random price for a random match but not by too much */
			int matchCount = MatchList.selection.getData().size();
			int randomIndex = random.nextInt(matchCount);
			Match originalMatch = MatchList.selection.getData().get(randomIndex);

			Match currentMatch = cache.get(originalMatch.getKey());
			MatchOdds currentOdds = currentMatch.getMatchOdds();

			int which = random.nextInt(3);
			double currentValue = 0d;
			switch (which) {
			case 0:
				currentValue = currentOdds.getHomeWin();
				break;
			case 1:
				currentValue = currentOdds.getDraw();
				break;
			case 2:
				currentValue = currentOdds.getAwayWin();
				break;
			}

			double change = Math.floor(random.nextDouble() * 0.2 * 100) / 100;

			boolean positive = random.nextBoolean();
			if (currentValue <= (1 + change) && !positive) {
				positive = true;
			}
			
			double newValue = currentValue;
			if (positive) {
				newValue = Math.floor((currentValue + change) * 100) / 100;
			} else {
				newValue = Math.floor((currentValue - change) * 100) / 100;
			}

			switch (which) {
			case 0:
				currentOdds.setHomeWin(newValue);
				break;
			case 1:
				currentOdds.setDraw(newValue);
				break;
			case 2:
				currentOdds.setAwayWin(newValue);
				break;
			}

			currentMatch.incrementVersion();
			cache.put(currentMatch.getKey(), currentMatch);

			try {
				int sleepDelay = random.nextInt(200);
				Thread.sleep(50 + sleepDelay);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}

	}
}
