package ati.player.rest.api.request;

import java.util.List;

public class GameInviteRequest {
    private int boardWidth;
    private int boardHeight;
    private List<ShipRequest> ships;
    
    public int getBoardWidth() {
        return boardWidth;
    }
    
    public void setBoardWidth(int boardWidth) {
        this.boardWidth = boardWidth;
    }
    
    public int getBoardHeight() {
        return boardHeight;
    }
    
    public void setBoardHeight(int boardHeight) {
        this.boardHeight = boardHeight;
    }
    
    public List<ShipRequest> getShips() {
        return ships;
    }
    
    public void setShips(List<ShipRequest> ships) {
        this.ships = ships;
    }
}


