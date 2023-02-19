package ati.player.rest.api.request;

public class GameOverRequest {
	private String winner;
	private String loser;
	private String statistics;
	
	public String getWinner() {
		return winner;
	}
	
	public void setWinner(String winner) {
		this.winner = winner;
	}
	
	public String getLoser() {
		return loser;
	}
	
	public void setLoser(String loser) {
		this.loser = loser;
	}
	
	public String getStatistics() {
		return statistics;
	}
	
	public void setStatistics(String statistics) {
		this.statistics = statistics;
	}
}
