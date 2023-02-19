package ati.player.rest.api.utils;

public class TestMain {

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
	
	
}
