package ati.player.rest.api.response;

import java.util.List;

import ati.player.rest.api.entity.Coordinate;

public class GameTurnResult {
    private List<Coordinate> coordinates;
    
    public List<Coordinate> getCoordinates() {
        return coordinates;
    }
    
    public void setCoordinates(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }
}
