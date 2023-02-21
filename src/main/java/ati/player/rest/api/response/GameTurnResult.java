package ati.player.rest.api.response;

import java.util.List;

public class GameTurnResult {
    private List<int[]> coordinates;
    
    public List<int[]> getCoordinates() {
        return coordinates;
    }
    
    public void setCoordinates(List<int[]> coordinates) {
        this.coordinates = coordinates;
    }
}
