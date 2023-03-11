package ati.player.rest.api.entity;

import java.util.ArrayList;
import java.util.List;

public class PlaceShipConfig {
	/* default mode */
	boolean modeRandom = true;
	
	public boolean isModeRandom() {
		return modeRandom;
	}
	public void setModeRandom(boolean modeRandom) {
		this.modeRandom = modeRandom;
	}
	public Boolean isVetical() {
		return vetical;
	}
	public void setVetical(Boolean vetical) {
		this.vetical = vetical;
	}
	public List<Coordinate> getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(List<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}
	public List<Boolean> getVerticals() {
		return verticals;
	}
	public void setVerticals(List<Boolean> verticals) {
		this.verticals = verticals;
	}
	public int getMaxShipOnBorder() {
		return maxShipOnBorder;
	}
	public void setMaxShipOnBorder(int maxShipOnBorder) {
		this.maxShipOnBorder = maxShipOnBorder;
	}
	public int getMaxShipOnCorner() {
		return maxShipOnCorner;
	}
	public void setMaxShipOnCorner(int maxShipOnCorner) {
		this.maxShipOnCorner = maxShipOnCorner;
	}
	
	///////////////////////////////////////////////////////////////////////////
	/* random mode */
	private int maxShipOnBorder = 0;
	private int maxShipOnCorner = 0;
	private Boolean vetical = null;
	
	/* fixed mode */ 
	private List<Coordinate> coordinates = new ArrayList<>();
	private List<Boolean> verticals = new ArrayList<>();
	public int index=0;
	///////////////////////////////////////////////////////////////////////////
}
