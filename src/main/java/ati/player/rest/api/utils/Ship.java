package ati.player.rest.api.utils;

import java.util.ArrayList;
import java.util.List;

import ati.player.rest.api.entity.Coordinate;

public class Ship {
	public static final String SHIP_CV = "CV";
	public static final String SHIP_OR = "OR";
	public static final String SHIP_DD = "DD";
	public static final String SHIP_BB = "BB";
	public static final String SHIP_CA = "CA";
	
	
	
	private char type;
    private int length;
    private boolean isVertical;
    private int row;
    private int col;
    public String typeDesc;
    public List<Coordinate> coordinates = new ArrayList<>();
    
//	private ShipData shipData = new ShipData();
	
    public List<int[]> getCoordinates() {
    	List<int[]> result = new ArrayList<>();
    	
		for (Coordinate coordinate : coordinates) {
			result.add(new int[] {coordinate.getX(),coordinate.getY()});
		}
    	return result;
    }
    
    public Ship(String typeDesc) {
    	//this.shipData.setType(typeDesc);;
    	this.typeDesc = typeDesc;
    	
		switch (typeDesc) {
		case SHIP_DD:
			this.type = 'A';
			this.length = 2;
			break;
		case SHIP_CA:
			this.type = 'B';
			this.length = 3;
			break;
		case SHIP_BB:
			this.type = 'C';
			this.length = 4;
			break;
		case SHIP_CV:
			this.type = 'V';
			this.length = 5;
			break;
		case SHIP_OR:
			this.type = 'O';
			this.length = 4;
			break;
		default:
		}
	}
    
    
	public char getType() {
		return type;
	}
	public void setType(char type) {
		this.type = type;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public boolean isVertical() {
		return isVertical;
	}
	public void setVertical(boolean isVertical) {
		this.isVertical = isVertical;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
//	public void setPosition (int row, int col) {
//		this.row = row;
//		this.col = col;
//	}
//    public ShipData getShipData() {
//		return shipData;
//	}
//	public void setShipData(ShipData shipData) {
//		this.shipData = shipData;
//	}
}
