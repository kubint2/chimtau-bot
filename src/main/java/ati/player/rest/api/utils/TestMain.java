package ati.player.rest.api.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ati.player.rest.api.controller.CalculateProbabilityTask;
import ati.player.rest.api.entity.Coordinate;

public class TestMain {

	public static void main1(String[] args) throws InterruptedException {
		
		while (true) {
			Board board = new Board(20, 8);
			board.addShip(new Ship("DD"));
			//board.addShip(new Ship("DD"));
			board.addShip(new Ship("CA"));
			//board.addShip(new Ship("CA"));
			board.addShip(new Ship("BB"));
			// board.addShip(new Ship("BB"));
			
			board.addShip(new Ship("OR"));
			// board.addShip(new Ship("OR"));

			// board.addShip(new Ship("CV"));
			board.addShip(new Ship("CV"));
			board.placeShipsRandomly();
			board.print();
			
			System.out.println();
			Thread.sleep(2000);
		}
	}
	
    private int[][] grid ;
	
	
	public static void main7(String[] args) throws InterruptedException {
		List<Coordinate> coordinatesShotted = new ArrayList<>();
		
		int count = 1;
		
		int [][] boardEnemy = new int[20][8] ;
		
		int tryCount = 20;
		int heigh = 8;
		int width = 20;
		
		while (tryCount-- > 0) {
			Board board = new Board(width, heigh, coordinatesShotted, true, true);
			board.addShip(new Ship("DD"));
//			board.addShip(new Ship("DD"));
			board.addShip(new Ship("CA"));
			board.addShip(new Ship("CA"));
			board.addShip(new Ship("BB"));
			board.addShip(new Ship("BB"));
			
			board.addShip(new Ship("OR"));
			board.addShip(new Ship("OR"));
			
			board.addShip(new Ship("CV"));
//			board.addShip(new Ship("CV"));
			board.placeShipsRandomly();
//			board.print();
			
			System.out.println(" =======  COUNT :" + count++);

			coordinatesShotted = board.getListDot();
			
			
			int x;
			int y;
			for (Ship ship : board.getShips()) {
				for (Coordinate coor : ship.coordinates) {
					boardEnemy[coor.getX()][coor.getY()]+=1;
				}
			}

			System.out.println();
			Thread.sleep(2000);
		}
		
		
		List<Coordinate> coordinates = new ArrayList<>();
        for (int y = 0; y < heigh; y++) {
            for (int x = 0; x < width; x++) {
            	coordinates.add(new Coordinate(x, y, boardEnemy[x][y]));
                System.out.print(boardEnemy[x][y] + "  ");
            }
            System.out.println();
        }
        
		Coordinate maxScore = coordinates.stream().max(Comparator.comparing(Coordinate::getScore))
				.orElseThrow(NoSuchElementException::new);
        
		 System.out.println("MaxScore: " + JsonUtil.objectToJson(maxScore));
        
	}
	
	
	public static void main(String[] args) throws InterruptedException {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
		 Map<String, Integer> shipEnemyMap = new HashMap<>();
		 shipEnemyMap.put(Ship.SHIP_BB, 2);
		 shipEnemyMap.put(Ship.SHIP_CA, 2);
		 shipEnemyMap.put(Ship.SHIP_DD, 2);
		 shipEnemyMap.put(Ship.SHIP_CV, 2);
		 shipEnemyMap.put(Ship.SHIP_OR, 2);
		
		CalculateProbabilityTask task = new CalculateProbabilityTask(20,8, new ArrayList<>(),shipEnemyMap);
		
		Future<?> future = executor.submit(task);
		Runnable cancelTask = () -> future.cancel(true);
		executor.schedule(cancelTask, 5000, TimeUnit.MILLISECONDS);
		executor.shutdown();
		Thread.sleep(5000);
		
		int [][] boardEnemy = task.boardEnemy;
		List<Coordinate> coordinates = new ArrayList<>();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 20; x++) {
            	coordinates.add(new Coordinate(x, y, boardEnemy[x][y]));
                System.out.print(boardEnemy[x][y] + "  ");
            }
            System.out.println();
        }
        

		Coordinate maxScore = coordinates.stream().max(Comparator.comparing(Coordinate::getScore))
				.orElseThrow(NoSuchElementException::new);
        
		 System.out.println("MaxScore: " + JsonUtil.objectToJson(maxScore));
		 System.out.println("Count" + task.count);
		 
		 
	     coordinates.sort((o1, o2) -> o2.getScore() - o1.getScore());
		 System.out.println("MaxScore In array (0): " + JsonUtil.objectToJson(coordinates.get(0)));
		 coordinates.remove(new Coordinate(maxScore.getX(), maxScore.getY()));
		 System.out.println("MaxScore In array (1): " + JsonUtil.objectToJson(coordinates.get(0)));
		 
		 
		 
	}
	
	public static void main5(String[] args) {
		List<Coordinate> listCoor = List.of (
				new Coordinate(0, 0),
				new Coordinate(1, 0)
				) ;
		System.out.println(JsonUtil.objectToJson(listCoor));
		
	}
	
	public static void main2(String[] args) {
		List<Coordinate> hitList = new ArrayList<>();
		hitList.add(new Coordinate(1, 1));
		hitList.add(new Coordinate(1, 2));
		hitList.add(new Coordinate(2, 2));	
	
		Coordinate fourthCoordinate = findFourthCoordinate(hitList);
        // In tọa độ của đỉnh còn lại
        System.out.println("(" + fourthCoordinate.getX() + ", " + fourthCoordinate.getY() + ")");
    }
	
    public static Coordinate findFourthCoordinate(List<Coordinate> coordinates) {
        // Lấy tọa độ các đỉnh đã biết
        Coordinate first = coordinates.get(0);
        Coordinate second = coordinates.get(1);
        Coordinate third = coordinates.get(2);

        // Tìm tọa độ còn lại
        int x4, y4;
        if (first.getX() == second.getX()) {
            // Trường hợp đường chéo chính nằm dọc
            x4 = third.getX();
            y4 = first.getY() + third.getY() - second.getY();
        } else {
            // Trường hợp đường chéo chính nằm ngang
            x4 = first.getX() + third.getX() - second.getX();
            y4 = third.getY();
        }

        // Tạo đối tượng Coordinate mới để lưu tọa độ của đỉnh còn lại
        Coordinate fourth = new Coordinate(x4, y4);
        return fourth;
    }
	
    public static void removeCoordinates(List<Coordinate> hitCoordinateList, List<Coordinate> toRemoveList) {
        hitCoordinateList.removeAll(toRemoveList);
    }
	
	public static void main3(String[] args) {
    	
    	List<Coordinate> hitCoordinateList = new ArrayList<>();
    	hitCoordinateList.add(new Coordinate(1, 1));
    	hitCoordinateList.add(new Coordinate(2, 2));
    	
//    	List<Coordinate> toRemoveList = new ArrayList<>();
//    	toRemoveList.add(new Coordinate(1, 1));
//    	
		int x = 1;
		int y = 3;
		hitCoordinateList.remove(new Coordinate(x, y));
    	
    	System.out.println("size " + hitCoordinateList.size());
    	
    }
}
