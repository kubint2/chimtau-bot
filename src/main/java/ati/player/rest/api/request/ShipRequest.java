package ati.player.rest.api.request;

public class ShipRequest {
    private String type;
    private int quantity;
    
    public ShipRequest(String type, int quantity) {
    	this.type = type;
    	this.quantity = quantity;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}