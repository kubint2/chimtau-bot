package ati.player.rest.api.entity;

import java.util.ArrayList;
import java.util.List;

public class GameConfig {
	public int thresholdShotConner = 1; // max 60
	public int thresholdShotBorder = 15; // max 60
	public int maxThresholdShot = 100;
	public int maxShotNoCheckDD = 70;
	public int minScoreShotConnerThreshold = 1;

	public int maxShipDDonCorner = 0;
    public int maxShipORonCorner = 1;
	
	
	
	public Boolean flagPlaceShipDDCAOnBorder = true;
    public Boolean flagPlaceShipOROnBorder = false;
    private Boolean flagCanHaveNeighbour = false;
	private Boolean flagPlaceVertical = null;
	private int timeOut = 1000;
	private boolean modeEasy = false;
	
	public int getMinScoreShotConnerThreshold() {
		return minScoreShotConnerThreshold;
	}

	public void setMinScoreShotConnerThreshold(int minScoreShotConnerThreshold) {
		this.minScoreShotConnerThreshold = minScoreShotConnerThreshold;
	}
	public int getMaxShotNoCheckDD() {
		return maxShotNoCheckDD;
	}

	public void setMaxShotNoCheckDD(int maxShotNoCheckDD) {
		this.maxShotNoCheckDD = maxShotNoCheckDD;
	}

	public int getThresholdShotConner() {
		return thresholdShotConner;
	}

	public void setThresholdShotConner(int thresholdShotConner) {
		this.thresholdShotConner = thresholdShotConner;
	}

	public int getThresholdShotBorder() {
		return thresholdShotBorder;
	}

	public void setThresholdShotBorder(int thresholdShotBorder) {
		this.thresholdShotBorder = thresholdShotBorder;
	}

	public int getMaxThresholdShot() {
		return maxThresholdShot;
	}

	public void setMaxThresholdShot(int maxThresholdShot) {
		this.maxThresholdShot = maxThresholdShot;
	}
    public Boolean getFlagPlaceShipDDCAOnBorder() {
		return flagPlaceShipDDCAOnBorder;
	}

	public void setFlagPlaceShipDDCAOnBorder(Boolean flagPlaceShipDDCAOnBorder) {
		this.flagPlaceShipDDCAOnBorder = flagPlaceShipDDCAOnBorder;
	}

	public Boolean getFlagPlaceShipOROnBorder() {
		return flagPlaceShipOROnBorder;
	}

	public void setFlagPlaceShipOROnBorder(Boolean flagPlaceShipOROnBorder) {
		this.flagPlaceShipOROnBorder = flagPlaceShipOROnBorder;
	}
	
	public Boolean getFlagCanHaveNeighbour() {
		return flagCanHaveNeighbour;
	}

	public void setFlagCanHaveNeighbour(Boolean flagCanHaveNeighbour) {
		this.flagCanHaveNeighbour = flagCanHaveNeighbour;
	}

	public Boolean getFlagPlaceVertical() {
		return flagPlaceVertical;
	}

	public void setFlagPlaceVertical(Boolean flagPlaceVertical) {
		this.flagPlaceVertical = flagPlaceVertical;
	}

	public List<int[]> getIgnorePlaceShip() {
		return ignorePlaceShip;
	}

	public void setIgnorePlaceShip(List<int[]> ignorePlaceShip) {
		this.ignorePlaceShip = ignorePlaceShip;
	}

	public List<int[]> getPriorityShotsList() {
		return priorityShotsList;
	}

	public void setPriorityShotsList(List<int[]> priorityShotsList) {
		this.priorityShotsList = priorityShotsList;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public boolean isModeEasy() {
		return modeEasy;
	}
	
	public boolean getModeEasy() {
		return modeEasy;
	}

	public void setModeEasy(boolean modeEasy) {
		this.modeEasy = modeEasy;
	}
	
    public int getMaxShipDDonCorner() {
		return maxShipDDonCorner;
	}

	public void setMaxShipDDonCorner(int maxShipDDonCorner) {
		this.maxShipDDonCorner = maxShipDDonCorner;
	}

	public int getMaxShipORonCorner() {
		return maxShipORonCorner;
	}

	public void setMaxShipORonCorner(int maxShipORonCorner) {
		this.maxShipORonCorner = maxShipORonCorner;
	}

	private List<int[]> ignorePlaceShip = new ArrayList<>() ;
	private List<int[]> priorityShotsList = new ArrayList<>() ;

}
