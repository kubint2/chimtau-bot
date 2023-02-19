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
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import ati.player.rest.api.entity.ShipData;
import ati.player.rest.api.request.GameInviteRequest;
import ati.player.rest.api.request.GameNotifyRequest;
import ati.player.rest.api.request.GameOverRequest;
import ati.player.rest.api.request.GamePlaceShipsRequest;
import ati.player.rest.api.request.GameTurnRequest;
import ati.player.rest.api.request.ShipRequest;
import ati.player.rest.api.response.GameStartResult;
import ati.player.rest.api.response.NotifyResult;
import ati.player.rest.api.utils.Board;
import ati.player.rest.api.utils.GameReqInfo;
import ati.player.rest.api.utils.JsonUtil;
import ati.player.rest.api.utils.Ship;


@RestController
// @RequestMapping("/api")
public class BotServiceController {

	private static final Logger logger = LogManager.getLogger(BotServiceController.class);	
	
	public static final int RESULT_PASS = 1;

	public static final int RESULT_FAIL = 0;


	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String test() throws Exception {
		return "Welcome to RESTful API Services!";
	}

	@ResponseBody
	@RequestMapping(value = "/invite", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> invite(@RequestBody GameInviteRequest gameInviteReq, HttpServletRequest request)
			throws Exception {
		System.out.println("print " + JsonUtil.objectToJson(gameInviteReq));
		logger.debug("print " + JsonUtil.objectToJson(gameInviteReq));
		NotifyResult response = new NotifyResult();
		try {
			GameReqInfo gameReqInfo = GameReqInfo.getInstance();
			gameReqInfo.setGameInviteRequest(gameInviteReq);
	
			// set response
			response.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return new ResponseEntity<NotifyResult>(response, HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "/place-ships", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> placeShips(@RequestBody GamePlaceShipsRequest gamePlaceShipsRequest, HttpServletRequest request)
			throws Exception {
		GameStartResult response = new GameStartResult();
		try {
			GameReqInfo gameReqInfo = GameReqInfo.getInstance();
			gameReqInfo.setGamePlaceShipsRequest(gamePlaceShipsRequest);

			// set response
			GameInviteRequest gameInviteReq = gameReqInfo.getGameInviteRequest();
			Board board = new Board(gameInviteReq.getBoardWidth(), gameInviteReq.getBoardHeight());
			for (ShipRequest shipReq : gameInviteReq.getShips()) {
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
				shipDatas.add(ship.getShipData());
			}

			response.setShips(shipDatas);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return new ResponseEntity<GameStartResult>(response, HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "/shoot", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?>  shoot(@RequestBody GameTurnRequest gameTurnReq, HttpServletRequest request) throws Exception {
	NotifyResult response = new NotifyResult();
	try {
		response.setSuccess(true);
	}catch (Exception e) {
		// TODO: handle exception
	}
		return new ResponseEntity<GameTurnRequest>(gameTurnReq, HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "/notify", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?>  notify(@RequestBody GameNotifyRequest gameNotifyReq, HttpServletRequest request) throws Exception {
	NotifyResult response = new NotifyResult();
	try {
		response.setSuccess(true);
	}catch (Exception e) {
		e.printStackTrace();
		logger.error(e);
	}
		return new ResponseEntity<GameNotifyRequest>(gameNotifyReq, HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "/game-over", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?>  gameOver(@RequestBody GameOverRequest gameOverReq, HttpServletRequest request) throws Exception {
	NotifyResult response = new NotifyResult();
	try {
		response.setSuccess(true);
	}catch (Exception e) {
		e.printStackTrace();
		logger.error(e);
	}
		return new ResponseEntity<GameOverRequest>(gameOverReq, HttpStatus.OK);
	}
}
