package ati.player.rest.api.request;

public class GameTurnRequest {
    private int turn;
	private int maxShots;
    
    public int getTurn() {
        return turn;
    }
    
    public void setTurn(int turn) {
        this.turn = turn;
    }
    
    public int getMaxShots() {
        return maxShots;
    }

    public void setMaxShots(int maxShots) {
		this.maxShots = maxShots;
	}
}
