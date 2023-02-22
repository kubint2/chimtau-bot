/**********************************************************************/
/*                                                                    */
/*      Copyright (C) NEC Asia Pte Ltd. 2020                          */
/*                                                                    */
/*      NEC CONFIDENTIAL AND PROPRIETARY                              */
/*      All rights reserved by NEC Asia Pte Ltd.                      */
/*      This program must be used solely for the purpose for which    */
/*      it was furnished by NEC Asia Pte Ltd.   No part of this       */
/*      program may be reproduced or disclosed to others, in any      */
/*      form, without the prior written permission of NEC             */
/*      Asia Pte Ltd.  Use of copyright notice dose not evidence      */
/*      publication of the program.                                   */
/*                                                                    */
/**********************************************************************/
package ati.player.rest.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ati.player.rest.api.entity.Coordinate;
import ati.player.rest.api.entity.ShipData;
import ati.player.rest.api.entity.ShotData;
import ati.player.rest.api.request.GameInviteRequest;
import ati.player.rest.api.request.GameNotifyRequest;
import ati.player.rest.api.request.GameOverRequest;
import ati.player.rest.api.request.GamePlaceShipsRequest;
import ati.player.rest.api.request.GameTurnRequest;
import ati.player.rest.api.request.ShipRequest;
import ati.player.rest.api.response.GameStartResult;
import ati.player.rest.api.response.GameTurnResult;
import ati.player.rest.api.response.NotifyResult;
import ati.player.rest.api.utils.Board;
import ati.player.rest.api.utils.BotPlayer;
import ati.player.rest.api.utils.JsonUtil;
import ati.player.rest.api.utils.Ship;


@RestController
// @RequestMapping("/api")
public class BotServiceController {

	private static final Logger logger = LogManager.getLogger(BotServiceController.class);	
	
	public static final String RESULT_HIT = "HIT";

	public static final String RESULT_MISS = "MISS";
	
	public static final String BOT_ID = "chimtau";

	private Map<String, BotPlayer> botPlayerMap = new HashMap<>();
	
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String test() throws Exception {
		return "Welcome to RESTful API Services!";
	}

	@ResponseBody
	@RequestMapping(value = "/invite", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> invite(@RequestBody GameInviteRequest gameInviteReq, HttpServletRequest request)
			throws Exception {
		System.out.println("invite requestInfo " + JsonUtil.objectToJson(gameInviteReq));
		logger.debug("invite requestInfo " + JsonUtil.objectToJson(gameInviteReq));

		NotifyResult response = new NotifyResult();
		try {
			BotPlayer botPlayer = new BotPlayer(gameInviteReq.getBoardWidth(), gameInviteReq.getBoardHeight(), gameInviteReq.getShips());
			String sessionID = request.getHeader("X-SESSION-ID");
			botPlayerMap.put(sessionID, botPlayer);
			
			// botPlayer.initInstance(gameInviteReq.getBoardWidth(), gameInviteReq.getBoardHeight(), gameInviteReq.getShips());
			// set response
			response.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		System.out.println("Response: invite" + JsonUtil.objectToJson(response));
		return new ResponseEntity<NotifyResult>(response, HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "/place-ships", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> placeShips(@RequestBody GamePlaceShipsRequest gamePlaceShipsRequest, HttpServletRequest request)
			throws Exception {
		System.out.println("place-ships requestInfo " + JsonUtil.objectToJson(gamePlaceShipsRequest));
		logger.debug("place-ships requestInfo " + JsonUtil.objectToJson(gamePlaceShipsRequest));
		
		GameStartResult response = new GameStartResult();
		try {
			String sessionID = request.getHeader("X-SESSION-ID");
			BotPlayer botPlayer = botPlayerMap.get(sessionID);
			
			// botPlayer = ChimtauPlayer.getInstance();
			botPlayer.player1 = gamePlaceShipsRequest.getPlayer1();
			botPlayer.player2 = gamePlaceShipsRequest.getPlayer2();

			// set response
			Board board = new Board(botPlayer.boardWidth, botPlayer.boardHeight);
			for (ShipRequest shipReq : botPlayer.ships) {
				int quantity = shipReq.getQuantity();
				while(quantity > 0) {
					board.addShip(new Ship(shipReq.getType()));
					quantity--;
				}
			}
			board.placeShipsRandomly();
			board.print();
			
			List<ShipData> shipDatas = new ArrayList<>();
			
			board.getShips();
			for (Ship ship : board.getShips()) {
				ShipData shipData = new ShipData();
				shipData.setType(ship.typeDesc);
				shipData.setCoordinates(ship.getCoordinates());
				shipDatas.add(shipData);
			}

			response.setShips(shipDatas);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		System.out.println("Response: place-ships" + JsonUtil.objectToJson(response));
		return new ResponseEntity<GameStartResult>(response, HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "/shoot", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> shoot(@RequestBody GameTurnRequest gameTurnReq, HttpServletRequest request)
			throws Exception {
		System.out.println("shoot requestInfo " + JsonUtil.objectToJson(gameTurnReq));
		logger.debug("shoot requestInfo " + JsonUtil.objectToJson(gameTurnReq));
		
		GameTurnResult response = new GameTurnResult();
		try {
			String sessionID = request.getHeader("X-SESSION-ID");
			BotPlayer botPlayer = botPlayerMap.get(sessionID);
			
			botPlayer.maxShots = gameTurnReq.getMaxShots();

			response.setCoordinates(botPlayer.getShotsTurnResult());

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		System.out.println("Response: shot" + JsonUtil.objectToJson(response));
		return new ResponseEntity<GameTurnResult>(response, HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "/notify", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> notify(@RequestBody GameNotifyRequest gameNotifyReq, HttpServletRequest request)
			throws Exception {
		System.out.println("notify requestInfo " + JsonUtil.objectToJson(gameNotifyReq));
		logger.debug("notify requestInfo " + JsonUtil.objectToJson(gameNotifyReq));
		
		NotifyResult response = new NotifyResult();
		try {
			response.setSuccess(true);
			String sessionID = request.getHeader("X-SESSION-ID");
			BotPlayer botPlayer = botPlayerMap.get(sessionID);
			
			
			if(gameNotifyReq.getPlayerId().equalsIgnoreCase(BOT_ID)) {
				List<ShotData> shotResult = gameNotifyReq.getShots();
				for (ShotData shotData : shotResult) {
					int[] coordinate = shotData.getCoordinate();
					int x = coordinate[0];
					int y = coordinate[1];
					if(shotData.getStatus().equalsIgnoreCase(RESULT_HIT)) {
						botPlayer.hitCoordinateList.add(new Coordinate(x, y));
						botPlayer.board[x][y]=2;
					} else {
						botPlayer.board[x][y]=1;
					}
				}
				// incase sunk ship data or [ ]
				if(CollectionUtils.isNotEmpty(gameNotifyReq.getSunkShips())) {
					for (ShipData shipData : gameNotifyReq.getSunkShips()) {
						if(botPlayer.shipMap.containsKey(shipData.getType())) {
							Integer quanty = botPlayer.shipMap.get(shipData.getType()) - 1;
							botPlayer.shipMap.put(shipData.getType(), quanty);
						}
						for (int[] coordinate : shipData.getCoordinates()) {
							int x = coordinate[0];
							int y = coordinate[1];
							botPlayer.hitCoordinateList.remove(new Coordinate(x, y));
							botPlayer.resetCalculator();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		System.out.println("Response: notify" + JsonUtil.objectToJson(response));
		return new ResponseEntity<NotifyResult>(response, HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "/game-over", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> gameOver(@RequestBody GameOverRequest gameOverReq, HttpServletRequest request)
			throws Exception {
		System.out.println("gameOver requestInfo " + JsonUtil.objectToJson(gameOverReq));
		logger.debug("gameOver requestInfo " + JsonUtil.objectToJson(gameOverReq));
		
		NotifyResult response = new NotifyResult();
		try {
			response.setSuccess(true);
			
			String sessionID = request.getHeader("X-SESSION-ID");
			BotPlayer botPlayer = botPlayerMap.get(sessionID);

			botPlayer.winner = gameOverReq.getWinner();
			botPlayer.loser = gameOverReq.getLoser();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		System.out.println("Response: game-over" + JsonUtil.objectToJson(response));
		return new ResponseEntity<NotifyResult>(response, HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "/botPlayer", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> botPlayerStatus(HttpServletRequest request)
			throws Exception {
		String sessionID = request.getHeader("X-SESSION-ID");
		BotPlayer botPlayer = botPlayerMap.get(sessionID);

		System.out.println("Response: botPlayer" + JsonUtil.objectToJson(botPlayer));
		return new ResponseEntity<BotPlayer>(botPlayer, HttpStatus.OK);
	}
	
}
