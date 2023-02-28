package ati.player.rest.api.entity;

import java.util.ArrayList;
import java.util.List;

public class GameConfig {
	private Boolean flagCanPutOnBorder = null;
    private Boolean flagCanHaveNeighbour = null;
	private Boolean flagPlaceVertical = null;

	public Boolean getFlagCanPutOnBorder() {
		return flagCanPutOnBorder;
	}

	public void setFlagCanPutOnBorder(Boolean flagCanPutOnBorder) {
		this.flagCanPutOnBorder = flagCanPutOnBorder;
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

	private List<int[]> ignorePlaceShip = new ArrayList<>() ;
	private List<int[]> priorityShotsList = new ArrayList<>() ;

}
