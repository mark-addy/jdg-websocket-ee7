package uk.co.c2b2.jdg.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MatchUpdate {

	private final MatchEventType event = MatchEventType.update;
	private final Match data;

	public MatchUpdate(Match data) {
		this.data = data;
	}

	public MatchEventType getEvent() {
		return event;
	}

	public Match getData() {
		return data;
	}
	
	public String getJmsDuplicateDetectionId() {
		return data.getJmsDuplicateDetectionId();
	}
}
