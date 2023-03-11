package ati.player.rest.api.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameConfig {
	public boolean isModeEasy() {
		return modeEasy;
	}
	public void setModeEasy(boolean modeEasy) {
		this.modeEasy = modeEasy;
	}
	public Boolean getFlagCanHaveNeighbour() {
		return flagCanHaveNeighbour;
	}
	public void setFlagCanHaveNeighbour(Boolean flagCanHaveNeighbour) {
		this.flagCanHaveNeighbour = flagCanHaveNeighbour;
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
	public HashMap<String, PlaceShipConfig> getShipConfigMap() {
		return shipConfigMap;
	}
	public void setShipConfigMap(HashMap<String, PlaceShipConfig> shipConfigMap) {
		this.shipConfigMap = shipConfigMap;
	}
	public ThresholdConfig getThresholdConfig() {
		return thresholdConfig;
	}
	public void setThresholdConfig(ThresholdConfig thresholdConfig) {
		this.thresholdConfig = thresholdConfig;
	}
	
	///////////////////////////////////////////////////////////////////////////
	private boolean modeEasy = false;
    private Boolean flagCanHaveNeighbour = false;
	private List<int[]> ignorePlaceShip = new ArrayList<>() ;
	private List<int[]> priorityShotsList = new ArrayList<>() ;


    private HashMap<String, PlaceShipConfig> shipConfigMap = new HashMap<>();
    private ThresholdConfig thresholdConfig = new ThresholdConfig();
	///////////////////////////////////////////////////////////////////////////
}
