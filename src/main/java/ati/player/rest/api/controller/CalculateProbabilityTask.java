package ati.player.rest.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import ati.player.rest.api.entity.Coordinate;
import ati.player.rest.api.utils.Board;
import ati.player.rest.api.utils.Ship;

public class CalculateProbabilityTask // implements Runnable
{
	public int [][] boardEnemy ;
	List<Coordinate> coordinatesShotted;
	List<Coordinate> coordinatesHitShotted;
	public Map<String, Integer> shipEnemyMap;
	int width;
	int heigh;
	public int count=0;
	public int timeOut = 500;
	
	
	public CalculateProbabilityTask (int width, int heigh, int timeOut, List<Coordinate> coordinatesShotted, List<Coordinate> coordinatesHitShotted, Map<String, Integer> shipEnemyMap) {
		this.width = width;
		this.heigh = heigh;
		this.boardEnemy = new int[width][heigh] ;
		this.coordinatesShotted = coordinatesShotted;
		this.coordinatesHitShotted = coordinatesHitShotted;
		this.shipEnemyMap = shipEnemyMap;
		this.timeOut = timeOut;
	}
	
	// @Override
	public int[][] run() {
		
		while (this.timeOut-->0) {
			Board board = new Board(width, heigh, coordinatesShotted);
			shipEnemyMap.forEach((shipType, quanlity) -> {
				while (quanlity-- > 0) {
					board.addShip(new Ship(shipType));
				}
			});
			board.placeShipsRandomly();

			List<Coordinate> coordinates = new ArrayList<>();
			for (Ship ship : board.getShips()) {
				coordinates.addAll(ship.coordinates);
			}

			boolean flagContainHitShotted = true;
			if (CollectionUtils.isNotEmpty(coordinatesHitShotted)) {
				for (Coordinate coordinateHitShotted : coordinatesHitShotted) {
					if (!coordinates.contains(coordinateHitShotted)) {
						flagContainHitShotted = false;
						break;
					}
				}
			}
			if (!flagContainHitShotted) {
				continue;
			}

			for (Coordinate coordinate : coordinates) {
				boardEnemy[coordinate.getX()][coordinate.getY()]+=1;
			}


			board.print();
			System.out.println(" =======  COUNT :" + count++);
		}
		
		return boardEnemy;
	}
	
	public static void main(String args[]) {
		
		
		
		
	}
}
