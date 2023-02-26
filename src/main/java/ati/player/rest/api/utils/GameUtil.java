package ati.player.rest.api.utils;

import java.util.ArrayList;
import java.util.List;

import ati.player.rest.api.entity.Coordinate;

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
}
