package ati.player.rest.api.utils;

import java.util.ArrayList;
import java.util.List;

import ati.player.rest.api.entity.Coordinate;

public class TestMain {

	public static void main1(String[] args) throws InterruptedException {
		
		while (true) {
			Board board = new Board(20, 8);
			board.addShip(new Ship("DD"));
			board.addShip(new Ship("DD"));
			board.addShip(new Ship("CA"));
			board.addShip(new Ship("CA"));
			board.addShip(new Ship("BB"));
			board.addShip(new Ship("BB"));
			
			board.addShip(new Ship("OR"));
			board.addShip(new Ship("OR"));
			
			board.addShip(new Ship("CV"));
			board.addShip(new Ship("CV"));
			board.addShip(new Ship("CV"));
			board.addShip(new Ship("CV"));
			board.placeShipsRandomly();
			board.print();
			Thread.sleep(2000);
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		while (true) {
			Board board = new Board(20, 8);
			board.addShip(new Ship("DD"));
			board.addShip(new Ship("DD"));
			board.addShip(new Ship("CA"));
			board.addShip(new Ship("CA"));
			board.addShip(new Ship("BB"));
			board.addShip(new Ship("BB"));
			
			board.addShip(new Ship("OR"));
			board.addShip(new Ship("OR"));
			
			board.addShip(new Ship("CV"));
			board.addShip(new Ship("CV"));
			board.addShip(new Ship("CV"));
			board.addShip(new Ship("CV"));
			board.placeShipsRandomly();
			board.print();
			Thread.sleep(2000);
		}
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
