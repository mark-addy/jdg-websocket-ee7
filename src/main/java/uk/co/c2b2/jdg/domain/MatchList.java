package uk.co.c2b2.jdg.domain;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MatchList {

	private final MatchEventType event = MatchEventType.start;
	private final List<Match> data;

	public MatchList(List<Match> data) {
		this.data = data;
	}

	public MatchEventType getEvent() {
		return event;
	}

	public List<Match> getData() {
		return data;
	}

	public static MatchList selection = new MatchList(Arrays.asList(new Match[] {
			new Match("FOOTBALL", "Manchester", "Liverpool", 0L, new MatchOdds(1.15, 4, 2.1)),
			new Match("FOOTBALL", "Coventry", "Birmingham", 0L, new MatchOdds(1.56, 3.14, 2.66)),
			new Match("FOOTBALL", "Chelsea", "Stoke", 0L, new MatchOdds(1.05, 2.25, 6.51)),
			new Match("FOOTBALL", "Bolton", "Wigan", 0L, new MatchOdds(1.67, 2.13, 1.98)),
			new Match("FOOTBALL", "Leicester", "Crewe", 0L, new MatchOdds(1.59, 3.14, 2.12)),
			new Match("FOOTBALL", "Everton", "West Ham", 0L, new MatchOdds(1.45, 5.25, 1.51)),
			new Match("FOOTBALL", "Wolverhampton", "West Brom", 0L, new MatchOdds(1.09, 2.01, 1.58)),
			new Match("CRICKET", "Warwickshire", "Lancashire", 0L, new MatchOdds(1.07, 13.24, 1.19)),
			new Match("CRICKET", "Leicestershire", "Worcestershire", 0L, new MatchOdds(1.31, 1.66, 1.05))

	}));
}
