package ati.player.rest.api.utils;

import ati.player.rest.api.request.GameInviteRequest;
import ati.player.rest.api.request.GamePlaceShipsRequest;

public class GameReqInfo {

	private static GameReqInfo instance;
	
	private GameReqInfo() {
		
	}

	private GameInviteRequest gameInviteRequest;
	
	private GamePlaceShipsRequest gamePlaceShipsRequest;
	
	public static synchronized GameReqInfo getInstance() {
		if(instance == null) {
			instance = new GameReqInfo();
		}
		return instance;
	}
	
	public GameInviteRequest getGameInviteRequest() {
		return gameInviteRequest;
	}

	public void setGameInviteRequest(GameInviteRequest gameInviteRequest) {
		this.gameInviteRequest = gameInviteRequest;
	}
	public GamePlaceShipsRequest getGamePlaceShipsRequest() {
		return gamePlaceShipsRequest;
	}
	public void setGamePlaceShipsRequest(GamePlaceShipsRequest gamePlaceShipsRequest) {
		this.gamePlaceShipsRequest = gamePlaceShipsRequest;
	}
	
	
	
	
}