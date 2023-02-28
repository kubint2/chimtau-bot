package ati.player.rest.api.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
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
import ati.player.rest.api.entity.EnemyPlayInfo;
import ati.player.rest.api.entity.GameConfig;
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
import ati.player.rest.api.utils.GameUtil;
import ati.player.rest.api.utils.JsonUtil;
import ati.player.rest.api.utils.Ship;


@RestController
// @RequestMapping("/api")
public class BotServiceController {

	private static final Logger logger = LogManager.getLogger(BotServiceController.class);	
	
	private static final String RESULT_HIT = "HIT";

	private static final String RESULT_MISS = "MISS";
	
	private static final String BOT_ID = "chimtau";
	
	// public static final int TIME_OUT = 5000;

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

			//
			calculateProbailityTask(botPlayer, 1000);
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

			botPlayer.player1 = gamePlaceShipsRequest.getPlayer1();
			botPlayer.player2 = gamePlaceShipsRequest.getPlayer2();

			if (!botPlayer.player1.equals(BOT_ID)) {
				botPlayer.enemyPlayId = botPlayer.player1;
			} else {
				botPlayer.enemyPlayId = botPlayer.player2;
			}
			// for readConfig
			GameConfig gameConfig = GameUtil.readConfiguration(botPlayer.enemyPlayId) ;
			if(gameConfig== null) gameConfig = new GameConfig();

			List<Coordinate> coordinatesShotted = new ArrayList<>();
			if(CollectionUtils.isNotEmpty(gameConfig.getIgnorePlaceShip())) {
				for (int[] coordinateArr : gameConfig.getIgnorePlaceShip()) {
					coordinatesShotted.add(new Coordinate(coordinateArr[0], coordinateArr[1]));
				}
			}

			if(gameConfig.getTimeOut() > 400 && gameConfig.getTimeOut() < 8000) {
				botPlayer.timeOut = gameConfig.getTimeOut();
			}
			
			// set response
			Board board = new Board(botPlayer.boardWidth, botPlayer.boardHeight, coordinatesShotted);
			for (ShipRequest shipReq : botPlayer.ships) {
				int quantity = shipReq.getQuantity();
				while(quantity > 0) {
					board.addShip(new Ship(shipReq.getType()));
					quantity--;
				}
			}
			
			board.flagPlaceVertical = gameConfig.getFlagPlaceVertical();
			board.flagCanHaveNeighbour = gameConfig.getFlagCanHaveNeighbour();
			board.flagCanPutOnBorder = gameConfig.getFlagCanPutOnBorder();
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

			// for write log
			botPlayer.enemyShotNo2d = new int[botPlayer.boardWidth][botPlayer.boardHeight];
			botPlayer.myShotNoArr2d = new int[botPlayer.boardWidth][botPlayer.boardHeight];
			
			botPlayer.myPlaceShipBoard = new char[botPlayer.boardWidth][botPlayer.boardHeight];
			botPlayer.enemyPlaceShipBoard = new char[botPlayer.boardWidth][botPlayer.boardHeight];
	        for (int i = 0; i < botPlayer.boardWidth; i++) {
	            Arrays.fill(botPlayer.myPlaceShipBoard[i], Board.DOT);
	            Arrays.fill(botPlayer.enemyPlaceShipBoard[i], Board.DOT);
	        }
			for (Ship ship : board.getShips()) {
				for (Coordinate coordinate : ship.coordinates) {
					botPlayer.myPlaceShipBoard[coordinate.getX()][coordinate.getY()] = ship.getType();
				}
			}

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
		BotPlayer botPlayer = null;
		try {
			String sessionID = request.getHeader("X-SESSION-ID");
			botPlayer = botPlayerMap.get(sessionID);
			
			botPlayer.maxShots = gameTurnReq.getMaxShots();

			response.setCoordinates(botPlayer.getShotsTurnResult());

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			Coordinate coordinate = botPlayer.makeRandomShot();
			response.setCoordinates(List.of(new int[] {coordinate.getX(), coordinate.getY()}));
			
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
				boolean calculateProbabilityTask =  false;
				botPlayer.myShotNo++; // for write log
				List<ShotData> shotResult = gameNotifyReq.getShots();
				for (ShotData shotData : shotResult) {
					int[] coordinate = shotData.getCoordinate();
					int x = coordinate[0];
					int y = coordinate[1];
					botPlayer.myShotNoArr2d[x][y] = botPlayer.myShotNo; // for write log
					
					Coordinate coordinateObj = new Coordinate(x, y);
					
					if(shotData.getStatus().equalsIgnoreCase(RESULT_HIT)) {
						if (!botPlayer.hitCoordinateList.contains(coordinateObj)) {
							botPlayer.hitCoordinateList.add(coordinateObj);
						}
						botPlayer.board[x][y]=2;
						
						calculateProbabilityTask = true;
						
						botPlayer.enemyPlaceShipBoard[x][y] = 'X'; // for write log
					} else {
						botPlayer.board[x][y]=1;
						botPlayer.coordinatesShotted.add(coordinateObj);
						calculateProbabilityTask = true;
					}
				}
				// in case sunk ship data or [ ]
				if(CollectionUtils.isNotEmpty(gameNotifyReq.getSunkShips())) {
					calculateProbabilityTask = true;

					for (ShipData shipData : gameNotifyReq.getSunkShips()) {
						if(botPlayer.shipEnemyMap.containsKey(shipData.getType())) {
							Integer quanty = botPlayer.shipEnemyMap.get(shipData.getType()) - 1;
							botPlayer.shipEnemyMap.put(shipData.getType(), quanty);
						}

						for (int[] coordinate : shipData.getCoordinates()) {
							Coordinate coordinateObj = new Coordinate(coordinate[0], coordinate[1]);

							botPlayer.hitCoordinateList.remove(coordinateObj);
							botPlayer.coordinatesShotted.add(coordinateObj);
						}

						botPlayer.resetCalculator();
						
						// for write log
						botPlayer.enemyShipData.add(shipData);
						char typeChar = 'o';
						switch (shipData.getType()) {
						case Ship.SHIP_DD:
							typeChar= 'A';
							break;
						case Ship.SHIP_CA:
							typeChar = 'B';
							break;
						case Ship.SHIP_BB:
							typeChar = 'C';
							break;
						case Ship.SHIP_CV:
							typeChar = 'V';
							break;
						case Ship.SHIP_OR:
							typeChar = 'O';
							break;
						default:
							typeChar = 'X';
						}
						for (int[] coordinateArr : shipData.getCoordinates()) {
							botPlayer.enemyPlaceShipBoard[coordinateArr[0]][coordinateArr[1]] = typeChar;
						}
					}
				}

				// check hitList
				//if (CollectionUtils.isEmpty(botPlayer.hitCoordinateList) || botPlayer.hitCoordinateList.size() > 5) {
				if(calculateProbabilityTask) {
					calculateProbailityTask(botPlayer, botPlayer.timeOut);
				}
			} else {
				botPlayer.enemyShotNo++; // for write log
				// for write log enemy
				List<ShotData> shotResult = gameNotifyReq.getShots();
				for (ShotData shotData : shotResult) {
					int[] coordinate = shotData.getCoordinate();
					int x = coordinate[0];
					int y = coordinate[1];
					botPlayer.enemyShotNo2d[x][y] = botPlayer.enemyShotNo;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		System.out.println("Response: notify" + JsonUtil.objectToJson(response));
		return new ResponseEntity<NotifyResult>(response, HttpStatus.OK);
	}

	private void calculateProbailityTask(BotPlayer botPlayer , int timeOut) throws InterruptedException {
		CalculateProbabilityTask task = new CalculateProbabilityTask(botPlayer.boardWidth,
				botPlayer.boardHeight, botPlayer.coordinatesShotted, botPlayer.hitCoordinateList, botPlayer.shipEnemyMap);

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
		Future<?> future = executor.submit(task);
		Runnable cancelTask = () -> future.cancel(true);
		executor.schedule(cancelTask, timeOut, TimeUnit.MILLISECONDS);
		executor.shutdown();
		Thread.sleep(timeOut);
		
		botPlayer.boardEnemy = task.boardEnemy;
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
			
			// write log
			if(!StringUtils.isEmpty(botPlayer.enemyPlayId)) {
				EnemyPlayInfo enemyInfo = new EnemyPlayInfo();
				enemyInfo.setEnemyPlayId(botPlayer.enemyPlayId);

				enemyInfo.setEnemyShotBoard(botPlayer.enemyShotNo2d);
				enemyInfo.setMyPlaceShipBoard(botPlayer.myPlaceShipBoard);
				
				enemyInfo.setEnemyPlaceShipBoard(botPlayer.enemyPlaceShipBoard);
				enemyInfo.setMyShotBoard(botPlayer.myShotNoArr2d);
				enemyInfo.setEnemyShipData(botPlayer.enemyShipData);

				String fileName = enemyInfo.getEnemyPlayId() + "_" + sessionID + ".log";
				GameUtil.writeLogInfoTofile(fileName, enemyInfo);

				fileName = enemyInfo.getEnemyPlayId() + ".txt";
				String title = "==== "+ botPlayer.enemyPlayId +" shot My Board (winer:" + botPlayer.winner +" -"+botPlayer.timeOut+") GameId: " +  sessionID;
				GameUtil.writeBoardLog(title, enemyInfo.getMyPlaceShipBoard(), enemyInfo.getEnemyShotBoard(), botPlayer.boardWidth, botPlayer.boardHeight, fileName);
				fileName = enemyInfo.getEnemyPlayId() + "_shot_chimtau" + ".txt";
				title = "==== chimtau shot "+ botPlayer.enemyPlayId +" Board (winer:" + botPlayer.winner +" -"+botPlayer.timeOut+") GameId: " +  sessionID;
				GameUtil.writeBoardLog(title, enemyInfo.getEnemyPlaceShipBoard(), enemyInfo.getMyShotBoard(), botPlayer.boardWidth, botPlayer.boardHeight, fileName);
			}
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
