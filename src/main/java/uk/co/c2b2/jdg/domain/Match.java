package uk.co.c2b2.jdg.domain;

import java.util.concurrent.atomic.AtomicLong;

public class Match {
	
	private static final String KEY_DELIMETER = ":";

	private final String type;
	private final String home;
	private final String away;
	private final AtomicLong version;
	private final MatchOdds matchOdds;
	private final String key;

	public Match(String type, String home, String away, long version, MatchOdds matchOdds) {
		this.type = type;
		this.home = home;
		this.away = away;
		this.version = new AtomicLong(version);
		this.matchOdds = matchOdds;
		this.key = type + "-" + home + "-" + away;
	}

	public String getType() {
		return type;
	}

	public String getHome() {
		return home;
	}

	public String getAway() {
		return away;
	}

	public MatchOdds getMatchOdds() {
		return matchOdds;
	}

	public void incrementVersion() {
		version.incrementAndGet();
	}
	
	public long getVersion() {
		return version.get();
	}

	public void setVersion(long version) {
		this.version.set(version);
	}

	public String getKey() {
		return key;
	}

	public String getJmsDuplicateDetectionId() {
		StringBuilder builder = new StringBuilder(key);
		builder.append(KEY_DELIMETER);
		builder.append(version);
		return builder.toString();
	}
}




