	package ati.player.rest.api.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ati.player.rest.api.controller.CalculateProbabilityTask;
import ati.player.rest.api.entity.Coordinate;
import ati.player.rest.api.entity.EnemyPlayInfo;
import ati.player.rest.api.entity.GameConfig;

public class TestMain {

	private static final String PATH = "//home//binhlv//Desktop//Hackathon//enemy_info//";
	private static int height =8;
	private static int width  =20;
	
	public static void main111(String[] args) {
		List<Coordinate> listCoor = new ArrayList<>();
		listCoor.add(new Coordinate(1, 0,1));
		listCoor.add(new Coordinate(4, 2,4));
		listCoor.add(new Coordinate(3, 0,4));
		listCoor.add(new Coordinate(2, 0,4));
		listCoor.add(new Coordinate(5, 0,4));

		System.out.println("before sort");
		System.out.println(JsonUtil.objectToJson(listCoor));
		
		System.out.println("after sort");
		listCoor.sort((o1, o2) -> o2.getScore() - o1.getScore());
		System.out.println(JsonUtil.objectToJson(listCoor));
		
		System.out.println("max coordinate");
		Coordinate max =  listCoor.stream().max(Comparator.comparing(Coordinate::getScore))
				.orElseThrow(NoSuchElementException::new);
		System.out.println("max coordinate : " + JsonUtil.objectToJson(max));
		
	}
	public static void main(String[] args) throws InterruptedException { 
		List<Coordinate> cordinates = new ArrayList<>();
		cordinates.add(new Coordinate(1, 0,1));
		cordinates.add(new Coordinate(4, 2,4));
		cordinates.add(new Coordinate(4, 2,4));
		Random rand = new Random();
		while (true) {
			System.out.println("max coordinate : " + rand.nextInt(cordinates.size()));
		Thread.sleep(1000);
		}
	}
	
	
	public static void main1(String[] args) throws Exception {
		String myPlayId = "chimtau";
		String enemyPlayId = "BAOTHU";
		String tokenId = "";
		// Path filePath = Path.of(PATH + enemyPlayId + "_" + tokenId + ".log");
		EnemyPlayInfo EnemyPlayInfo = readEnemyPlayInfoFile(PATH + "BAOTHU_4c6f79ca-5956-45a6-821e-440355fa3a85.log");

		System.out.println("===== ENEMY BOARD " + enemyPlayId);
		print(EnemyPlayInfo.getEnemyPlaceShipBoard());

		System.out.println("===== My Shot Board " + myPlayId);
		print(EnemyPlayInfo.getMyShotBoard());
		
		String pathOutput = PATH + "BAOTHU.txt";
		writeBoardLog("ENEMY BOARD", EnemyPlayInfo.getEnemyPlaceShipBoard(), EnemyPlayInfo.getMyShotBoard(), pathOutput);

//		System.out.println("===== My Board " + myPlayId);
//		print(EnemyPlayInfo.getMyShotBoard());
//		
//		System.out.println("===== ENEMY Shot Board " + myPlayId);
//		print(EnemyPlayInfo.getEnemyShotBoard());
//		
//		System.out.println("===== Enemy List Ship " + enemyPlayId);
//		System.out.println(EnemyPlayInfo.getEnemyShipData());
		
		// System.out.println(JsonUtil.objectToJson(EnemyPlayInfo));

	}
	
    public static void writeBoardLog(String title, char [][] gridBoard, int [][] gridShotno, String filePath) throws IOException {
    	StringBuffer sb = new StringBuffer();
    	sb.append("==== Title :" + title);
    	sb.append("\n");
    	String content;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
            	sb.append(gridBoard[x][y] + " (" + gridShotno[x][y] + ")\t");
            }
            sb.append("\n");
            // System.out.println();
        }
        System.out.println(sb.toString());
        
        
		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(sb.toString());
		bw.close();
		System.out.println("Done write log to " + filePath);
    }
	
    public static void main122(String[] args)  throws IOException {
    	String filePath = PATH + "//config//"+ "defaulttest" + ".config";
    	GameConfig gameConfig = new GameConfig();
		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(JsonUtil.objectToJson(gameConfig));
		bw.close();
		System.out.println("Done write log to " + filePath);
    }
    
    public static void print(int [][] grid) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(grid[x][y] + "\t");
            }
            System.out.println();
        }
        System.out.println("=====================");
    }
    
    public static void print(char [][] grid) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(grid[x][y] + "\t");
            }
            System.out.println();
        }
        System.out.println("=====================");
    }
	
  public static EnemyPlayInfo readEnemyPlayInfoFile(String FilePath) throws Exception {
		String enemyPlayId = "enemyPlayId";

		Path filePath = Path.of(FilePath);
		if (Files.exists(filePath)) {
			String content = Files.readString(filePath);
			EnemyPlayInfo enemyPlayInfo = (EnemyPlayInfo) JsonUtil.jsonToObject(content, EnemyPlayInfo.class);
			System.out.println(enemyPlayInfo);
			return enemyPlayInfo;
		}
		return null;
	}
	
	
	public static void main11(String[] args) throws InterruptedException {
		
		while (true) {
			Board board = new Board(20, 8);
			board.flagPlaceShipDDCAOnBorder = true;
			board.flagPlaceShipOROnBorder = true;
			board.addShip(new Ship("OR"));
			board.maxShipORonCorner =0;
			board.maxShipDDonCorner =0;
			
			board.addShip(new Ship("DD"));
//			//board.addShip(new Ship("DD"));
//			board.addShip(new Ship("CA"));
//			//board.addShip(new Ship("CA"));
//			board.addShip(new Ship("BB"));
			// board.addShip(new Ship("BB"));
			
			// board.addShip(new Ship("OR"));
			// board.addShip(new Ship("OR"));

			 board.addShip(new Ship("CV"));
			board.addShip(new Ship("CV"));
			//board.flagPlaceVertical = true;
			
			board.flagCanHaveNeighbour = false;
			board.placeShipsRandomly();
			board.print();
			
			System.out.println();
			Thread.sleep(800);
		}
	}
	
    private int[][] grid ;
	
    
    public static void main12(String[] args) {
    	
    	 System.out.println(java.time.LocalDateTime.now());    
    }
	
	public static void mainxx(String[] args) throws InterruptedException {
		List<Coordinate> coordinatesShotted = new ArrayList<>();
		
		int count = 1;
		
		int [][] boardEnemy = new int[20][8] ;
		
		int tryCount = 8000;
		int heigh = 8;
		int width = 20;
		
		while (tryCount-- > 0) {
			Board board = new Board(width, heigh, coordinatesShotted);
			board.addShip(new Ship("CV"));
			board.addShip(new Ship("CV"));
//			board.addShip(new Ship("DD"));
//			board.addShip(new Ship("CA"));
//			board.addShip(new Ship("CA"));
//			board.addShip(new Ship("BB"));
//			board.addShip(new Ship("BB"));
//			
//			board.addShip(new Ship("OR"));
//			board.addShip(new Ship("OR"));
//			
//			board.addShip(new Ship("CV"));
//			board.addShip(new Ship("CV"));
			board.flagPlaceVertical = true;
			// board.flagCanHaveNeighbour = false;
//			board.flagPlaceShipDDCAOnBorder = true;
//			board.flagPlaceShipOROnBorder = true;
			board.placeShipsRandomly();
			board.print();
			
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
			//Thread.sleep(300);
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
	
	
	public static void main9(String[] args) throws InterruptedException {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
		 Map<String, Integer> shipEnemyMap = new HashMap<>();
		 shipEnemyMap.put(Ship.SHIP_BB, 2);
		 shipEnemyMap.put(Ship.SHIP_CA, 2);
		 shipEnemyMap.put(Ship.SHIP_DD, 2);
		 shipEnemyMap.put(Ship.SHIP_CV, 2);
		 shipEnemyMap.put(Ship.SHIP_OR, 2);
		
		CalculateProbabilityTask task = new CalculateProbabilityTask(20,8,500, new ArrayList<>(), new ArrayList<>() ,shipEnemyMap);
		
//		Future<?> future = executor.submit(task);
//		Runnable cancelTask = () -> future.cancel(true);
//		executor.schedule(cancelTask, 5000, TimeUnit.MILLISECONDS);
//		executor.shutdown();
//		Thread.sleep(5000);
		
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
