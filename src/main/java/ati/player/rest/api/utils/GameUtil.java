package ati.player.rest.api.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ati.player.rest.api.entity.Coordinate;
import ati.player.rest.api.entity.EnemyPlayInfo;
import ati.player.rest.api.entity.GameConfig;
import ati.player.rest.api.entity.PlaceShipConfig;
import ati.player.rest.api.entity.ThresholdConfig;


public class GameUtil {

	private static final String PATH = "//home//binhlv//Desktop//Hackathon//enemy_info//";
	
	// private static final String PATH = "C:\\Users\\vanbinhluong92\\Desktop\\Hackathon\\enemy_info\\";
	
    public static List<Coordinate> getCoordinateNeighbours(Coordinate coordinate, int boardWidth, int boardHeight) {
        List<Coordinate> neighborCells = new ArrayList<>();
        int x = coordinate.getX();
        int y = coordinate.getY();
        if(x-1 >=0)           neighborCells.add(new Coordinate(x-1, y));
        if(x+1 < boardWidth)  neighborCells.add(new Coordinate(x+1, y));
        if(y-1 >=0)           neighborCells.add(new Coordinate(x, y-1));
        if(y+1 < boardHeight) neighborCells.add(new Coordinate(x, y+1));

        return neighborCells;
    }

	public static void writeLogInfoTofile(String fileName, EnemyPlayInfo enemyPlayInfo) throws Exception {
		String content = JsonUtil.objectToJson(enemyPlayInfo);
		File file = new File(PATH + fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();
		System.out.println("Done write log to " + fileName);
	}
	
    public static void writeBoardLog(String title, char [][] gridBoard, int [][] gridShotno, int width, int height, String fileName) throws IOException {
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
    	sb.append("=========");
        
		File file = new File(PATH + fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(sb.toString());
		bw.close();
		System.out.println("Done write log to " + fileName);
    }
	
    public static GameConfig readConfiguration(String enemyPlayId) {
    	GameConfig gameConfig = new GameConfig();
    	String path = PATH + "//config//";
    	try {
        	Path filePath = Path.of(path + enemyPlayId + ".json");
        	if (!Files.exists(filePath)) {
    			filePath = Path.of(path + "default" + ".json");
    		}
        	
        	String content = Files.readString(filePath);
        	gameConfig = (GameConfig) JsonUtil.jsonToObject(content, GameConfig.class);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return gameConfig;
    }
 
    public static List<Coordinate> getCoordinatesBorder(int width, int heigh) {
		List<Coordinate> coordinates = new ArrayList<>();

		for (int x = 0; x < width; x++) {
		    for (int y = 0; y < heigh; y++) {
		        if (x == 0 || x == (width-1) || y == 0 || y == (heigh-1)) {
		            Coordinate coord = new Coordinate(x, y);
		            coordinates.add(coord);
		        }
		    }
		}

		return coordinates;
    }
    
	public static List<Coordinate> getCoordinatesConner(int width, int heigh) {
		List<Coordinate> coordinates = new ArrayList<>();
		coordinates.add(new Coordinate(0, 0));
		coordinates.add(new Coordinate(width - 1, 0));
		coordinates.add(new Coordinate(width - 1, heigh - 1));
		coordinates.add(new Coordinate(0, heigh - 1));
		return coordinates;
	}
	
	public static List<Coordinate> getCoordinatesBoard(int width, int heigh) {
		List<Coordinate> coordinates = new ArrayList<>();
		for (int y = 0; y < heigh; y++) {
			for (int x = 0; x < width; x++) {
				coordinates.add(new Coordinate(x, y));
			}
		}
		return coordinates;
	}
    
	public static List<Coordinate> getCoordShipTypeA(int colX, int rowY, int length, boolean vetical) {
		List<Coordinate> coordinates = new ArrayList<>();
		if (vetical) {
			for (int r = rowY; r < rowY + length; r++) {
				coordinates.add(new Coordinate(colX, r));
			}
		} else {
			for (int c = colX; c < colX + length; c++) {
				coordinates.add(new Coordinate(c, rowY));
			}
		}
		return coordinates;
	}
	
	public static List<Coordinate> getCoordShipCV(int colX, int rowY, boolean vertical) {
		List<Coordinate> coordinates = new ArrayList<>();
		if (vertical) {
			coordinates.add(new Coordinate(colX, rowY)); // 1
			coordinates.add(new Coordinate(colX, rowY - 1)); // 2
			coordinates.add(new Coordinate(colX, rowY + 1)); // 3
			coordinates.add(new Coordinate(colX, rowY + 2)); // 4
			coordinates.add(new Coordinate(colX - 1, rowY)); // 5
			return coordinates;
		} else {
			coordinates.add(new Coordinate(colX, rowY)); // 1
			coordinates.add(new Coordinate(colX - 1, rowY)); // 2
			coordinates.add(new Coordinate(colX + 1, rowY)); // 3
			coordinates.add(new Coordinate(colX + 2, rowY)); // 4
			coordinates.add(new Coordinate(colX, rowY - 1)); // 5
			return coordinates;
		}
	}

	
	public static List<Coordinate> getCoordShipOR(int colX, int rowY) {
		List<Coordinate> coordinates = new ArrayList<>();
		coordinates.add(new Coordinate(colX, rowY));
		coordinates.add(new Coordinate(colX + 1, rowY));
		coordinates.add(new Coordinate(colX, rowY + 1));
		coordinates.add(new Coordinate(colX + 1, rowY + 1));
		return coordinates;
	}
	
	public static void main(String arg[]) {
		GameConfig gameConfig = readConfiguration("baothu");
		
		System.out.println(JsonUtil.objectToJson(gameConfig));
	}
	
    public static void main1(String [] args) {
    	readConfiguration("baothu");
    }
	
	public static void createGameConfigFile() {
		GameConfig gameConf = new GameConfig();
		gameConf.setModeEasy(false);
		gameConf.setFlagCanHaveNeighbour(false);
		gameConf.setIgnorePlaceShip(new ArrayList<>());
		gameConf.setPriorityShotsList(new ArrayList<>());
		
		//threshold
		ThresholdConfig thresholdConfig = new ThresholdConfig();
		gameConf.setThresholdConfig(thresholdConfig);

		//placeShipMap
		HashMap<String, PlaceShipConfig> shipConfigMap = new HashMap<>();
		gameConf.setShipConfigMap(shipConfigMap);
	
		PlaceShipConfig placeShipDD = new PlaceShipConfig();
		placeShipDD.setCoordinates(List.of(new Coordinate(0,0)));
		placeShipDD.setVerticals(List.of(true));
		shipConfigMap.put(Ship.SHIP_DD, placeShipDD);
		PlaceShipConfig placeShipCA = new PlaceShipConfig();
		placeShipCA.setCoordinates(List.of(new Coordinate(0,5)));
		placeShipCA.setVerticals(List.of(true));
		shipConfigMap.put(Ship.SHIP_CA, placeShipCA);
		PlaceShipConfig placeShipOR = new PlaceShipConfig();
		placeShipOR.setCoordinates(List.of(new Coordinate(18,6)));
		shipConfigMap.put(Ship.SHIP_OR, placeShipOR);

		System.out.println("Print Config");
		
		
		System.out.println(JsonUtil.objectToJson(gameConf));
	}
	

	
}
