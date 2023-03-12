package ati.player.rest.api.entity;

public class ThresholdConfig {
	public int getThresholdShotCorner() {
		return thresholdShotCorner;
	}
	public void setThresholdShotCorner(int thresholdShotCorner) {
		this.thresholdShotCorner = thresholdShotCorner;
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
	public int getMaxShotNoCheckDD() {
		return maxShotNoCheckDD;
	}
	public void setMaxShotNoCheckDD(int maxShotNoCheckDD) {
		this.maxShotNoCheckDD = maxShotNoCheckDD;
	}

	///////////////////////////////////////////////////////////////////////////
	private int thresholdShotCorner = 5; // max 60
	private int thresholdShotBorder = 20; // max 60
	private int maxThresholdShot = 100;
	private int maxShotNoCheckDD = 55;
	///////////////////////////////////////////////////////////////////////////

}
