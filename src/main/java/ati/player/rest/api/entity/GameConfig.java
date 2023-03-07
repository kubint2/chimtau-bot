package ati.player.rest.api.entity;

import java.util.ArrayList;
import java.util.List;

public class GameConfig {
	public Boolean flagPlaceShipDDCAOnBorder = false;
    public Boolean flagPlaceShipOROnBorder = false;
    private Boolean flagCanHaveNeighbour = false;
	private Boolean flagPlaceVertical = null;
	private int timeOut = 1000;
	private boolean modeEasy = false;
	
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

	private List<int[]> ignorePlaceShip = new ArrayList<>() ;
	private List<int[]> priorityShotsList = new ArrayList<>() ;

}
