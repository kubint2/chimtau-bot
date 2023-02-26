package ati.player.rest.api.utils;

import java.util.List;
import java.util.Map;

import ati.player.rest.api.entity.Coordinate;

class CalculateProbabilityTask implements Runnable
{
	int [][] boardEnemy ;
	List<Coordinate> coordinatesShotted;
	public Map<String, Integer> shipEnemyMap;
	int width;
	int heigh;
	public int count=0;
	
	
	CalculateProbabilityTask (int width, int heigh, List<Coordinate> coordinatesShotted, Map<String, Integer> shipEnemyMap) {
		this.width = width;
		this.heigh = heigh;
		this.boardEnemy = new int[width][heigh] ;
		this.coordinatesShotted = coordinatesShotted;
		this.shipEnemyMap = shipEnemyMap;
		
	}
	
	@Override
	public void run() {
		//int count = 1;
		while (true) {
			Board board = new Board(width, heigh, coordinatesShotted, true, true);

			shipEnemyMap.forEach((shipType, quanlity) -> {
				while (quanlity-- > 0) {
					board.addShip(new Ship(shipType));
				}
			});
			board.placeShipsRandomly();

			for (Ship ship : board.getShips()) {
				for (Coordinate coor : ship.coordinates) {
					boardEnemy[coor.getX()][coor.getY()]+=1;
				}
			}

			board.print();
			System.out.println(" =======  COUNT :" + count++);
			if (Thread.interrupted()) {
				return;
			}
		}
	}
}
