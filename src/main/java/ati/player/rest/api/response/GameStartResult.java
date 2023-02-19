package ati.player.rest.api.response;

import java.util.List;

import ati.player.rest.api.entity.ShipData;

public class GameStartResult {
	private List<ShipData> ships;

	public List<ShipData> getShips() {
		return ships;
	}

	public void setShips(List<ShipData> ships) {
		this.ships = ships;
	}
}
