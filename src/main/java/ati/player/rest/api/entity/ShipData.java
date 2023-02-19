package ati.player.rest.api.entity;

import java.util.List;

public class ShipData {
	private List<int[]> coordinates;
	private String type;

	public List<int[]> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<int[]> coordinates) {
		this.coordinates = coordinates;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}