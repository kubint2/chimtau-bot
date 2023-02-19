package ati.player.rest.api.entity;

public class ShotData {
    private int[] coordinate;
    private String status;
    
    public int[] getCoordinate() {
        return coordinate;
    }
    
    public void setCoordinate(int[] coordinate) {
        this.coordinate = coordinate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}
