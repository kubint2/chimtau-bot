package ati.player.rest.api.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import ati.player.rest.api.entity.Coordinate;
import ati.player.rest.api.entity.EnemyPlayInfo;
import ati.player.rest.api.entity.GameConfig;


public class GameUtil {

	private static final String PATH = "//home//binhlv//Desktop//Hackathon//enemy_info//";
	
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
        	Path filePath = Path.of(path + enemyPlayId + ".config");
        	if (!Files.exists(filePath)) {
    			filePath = Path.of(path + "default" + ".config");
    		}
        	
        	String content = Files.readString(filePath);
        	gameConfig = (GameConfig) JsonUtil.jsonToObject(content, GameConfig.class);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return gameConfig;
    }
    
    public static void main(String [] args) {
    	readConfiguration("baothu");
    }
    
//  public static EnemyPlayInfo readEnemyPlayInfoFile(String FilePath) throws Exception {
//		String enemyPlayId = "enemyPlayId";
//
//		Path filePath = Path.of(FilePath);
//		if (Files.exists(filePath)) {
//			String content = Files.readString(filePath);
//			EnemyPlayInfo enemyPlayInfo = (EnemyPlayInfo) JsonUtil.jsonToObject(content, EnemyPlayInfo.class);
//			System.out.println(enemyPlayInfo);
//			return enemyPlayInfo;
//		}
//		return null;
//	}
}
