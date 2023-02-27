package ati.player.rest.api.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import ati.player.rest.api.entity.Coordinate;
import ati.player.rest.api.entity.EnemyPlayInfo;

public class GameUtil {
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

	public static void writeLogInfoTofile(String FilePath, EnemyPlayInfo enemyPlayInfo) throws Exception {
		String content = JsonUtil.objectToJson(enemyPlayInfo);
		File file = new File(FilePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();
		System.out.println("Done write log to " + FilePath);
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
