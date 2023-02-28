package ati.player.rest.api.entity;

import java.util.List;

public class EnemyPlayInfo {
	public String getEnemyPlayId() {
		return enemyPlayId;
	}

	public void setEnemyPlayId(String enemyPlayId) {
		this.enemyPlayId = enemyPlayId;
	}

	public int[][] getEnemyShotBoard() {
		return enemyShotBoard;
	}

	public void setEnemyShotBoard(int[][] enemyShotBoard) {
		this.enemyShotBoard = enemyShotBoard;
	}

	public char[][] getEnemyPlaceShipBoard() {
		return enemyPlaceShipBoard;
	}

	public void setEnemyPlaceShipBoard(char[][] enemyPlaceShipBoard) {
		this.enemyPlaceShipBoard = enemyPlaceShipBoard;
	}

	public int[][] getMyShotBoard() {
		return myShotBoard;
	}

	public void setMyShotBoard(int[][] myShotBoard) {
		this.myShotBoard = myShotBoard;
	}

	public char[][] getMyPlaceShipBoard() {
		return myPlaceShipBoard;
	}

	public void setMyPlaceShipBoard(char[][] myPlaceShipBoard) {
		this.myPlaceShipBoard = myPlaceShipBoard;
	}
	public List<ShipData> getEnemyShipData() {
		return enemyShipData;
	}

	public void setEnemyShipData(List<ShipData> enemyShipData) {
		this.enemyShipData = enemyShipData;
	}
	
	private String enemyPlayId;
	private int[][] enemyShotBoard;
	private char[][] enemyPlaceShipBoard;
	private List<ShipData> enemyShipData; 
	
	private int[][] myShotBoard;
	private char[][] myPlaceShipBoard;
	
	public EnemyPlayInfo () {
		
	}

}
