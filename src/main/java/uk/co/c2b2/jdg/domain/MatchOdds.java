package uk.co.c2b2.jdg.domain;


public class MatchOdds {

	private double homeWin;
	private double draw;
	private double awayWin;

	public MatchOdds(double homeWin, double draw, double awayWin) {
		this.setHomeWin(homeWin);
		this.setDraw(draw);
		this.setAwayWin(awayWin);
	}

	public double getHomeWin() {
		return homeWin;
	}

	public void setHomeWin(double homeWin) {
		this.homeWin = homeWin;
	}

	public double getDraw() {
		return draw;
	}

	public void setDraw(double draw) {
		this.draw = draw;
	}

	public double getAwayWin() {
		return awayWin;
	}

	public void setAwayWin(double awayWin) {
		this.awayWin = awayWin;
	}

}
