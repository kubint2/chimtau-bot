package ati.player.rest.api.request;

import java.util.List;

import ati.player.rest.api.entity.ShipData;
import ati.player.rest.api.entity.ShotData;


public class GameNotifyRequest {
    private String playerId;
    private List<ShotData> shots;
    private List<ShipData> sunkShips;
    
    public String getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
    
    public List<ShotData> getShots() {
        return shots;
    }
    
    public void setShots(List<ShotData> shots) {
        this.shots = shots;
    }
    
    public List<ShipData> getSunkShips() {
        return sunkShips;
    }
    
    public void setSunkShips(List<ShipData> sunkShips) {
        this.sunkShips = sunkShips;
    }
}

