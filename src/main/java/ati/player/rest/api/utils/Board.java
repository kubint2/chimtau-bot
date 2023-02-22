package ati.player.rest.api.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.web.client.HttpClientErrorException.Forbidden;

import ati.player.rest.api.entity.Coordinate;

public class Board {
    private int width;
    private int height;
    private char[][] grid;
    private List<Ship> ships;
    
    int tryCount = 0;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.ships = new ArrayList<>();
        
        this.grid = new char[width][height];
        for (int i = 0; i < width; i++) {
            Arrays.fill(grid[i], '.');
        }
    }

    public void print() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(grid[x][y] + " ");
            }
            System.out.println();
        }
    }

    public void addShip(Ship ship) {
        ships.add(ship);
    }

    public List<Ship> getShips() {
        return ships;
    }
    
    public boolean canPlaceShipTypeA(Ship ship, int rowY, int colX) {
        if (rowY < 0 || colX < 0 || rowY >= height || colX >= width) {
            return false;
        }
        if (ship.isVertical()) {
            if (rowY + ship.getLength() > height) {
                return false;
            }
            for (int r = rowY; r < rowY + ship.getLength(); r++) {
                if (grid[colX][r] != '.') {
                    return false;
                }
            }
        } else {
            if (colX + ship.getLength() > width) {
                return false;
            }
            for (int c = colX; c < colX + ship.getLength(); c++) {
                if (grid[c][rowY] != '.') {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean placeShipTypeA(Ship ship) {
		Random rand = new Random();
		int rowY, colX;
		rowY = rand.nextInt(height);
		colX = rand.nextInt(width);
		boolean vertical = rand.nextBoolean();
		ship.setVertical(vertical);
		
        if (!canPlaceShipTypeA(ship, rowY, colX)) {
            return false;
        }
		List<Coordinate> coordinates = new ArrayList<>();
        if (ship.isVertical()) {
            for (int r = rowY; r < rowY + ship.getLength(); r++) {
    			coordinates.add(new Coordinate(colX, r));
            }
        } else {
            for (int c = colX; c < colX + ship.getLength(); c++) {
                coordinates.add(new Coordinate(c,rowY));
            }
        }
        
        // not on border
        for (Coordinate coordinate : coordinates) {
	        // not on border
			if (coordinate.getX()==0 || coordinate.getX()==width-1
					|| coordinate.getY()==0 || coordinate.getY() == height-1) {
				return false ;
			}
		}

		// draw
		for (Coordinate coordinate : coordinates) {
			grid[coordinate.getX()][coordinate.getY()] = ship.getType();
		}
        ship.coordinates = coordinates;
        return true;
    }
    
    public boolean placeShipTypeB(Ship ship, boolean vertical) {
		Random rand = new Random();
		int rowY, colX;
		if(vertical) {
			rowY = rand.nextInt(height-4);
			colX = ThreadLocalRandom.current().nextInt(1, width-1);
			
			// add coordinates
			List<Coordinate> coordinates = new ArrayList<>();
			coordinates.add(new Coordinate(colX, rowY)); //
			coordinates.add(new Coordinate(colX,rowY+1));
			coordinates.add(new Coordinate(colX,rowY+2));
			coordinates.add(new Coordinate(colX,rowY+3));
			coordinates.add(new Coordinate(colX-1,rowY+1));

	        for (Coordinate coordinate : coordinates) {
		        // not on border
				if (coordinate.getX()==0 || coordinate.getX()==width-1
						|| coordinate.getY()==0 || coordinate.getY() == height-1) {
					return false ;
				}
		        // value not is .
				if(grid[coordinate.getX()][coordinate.getY()] != '.' ) {
					return false ;
				}
			}

	        ship.coordinates = coordinates;
			// draw
			for (Coordinate coordinate : coordinates) {
				grid[coordinate.getX()][coordinate.getY()] = ship.getType();
			}
		}else {
			rowY = ThreadLocalRandom.current().nextInt(1, height-1);
			colX = rand.nextInt(width-4);
			
			List<Coordinate> coordinates = new ArrayList<>();
			coordinates.add(new Coordinate(colX+1,rowY+1));
			coordinates.add(new Coordinate(colX+2,rowY+1));
			coordinates.add(new Coordinate(colX+3,rowY+1));
			coordinates.add(new Coordinate(colX+4,rowY+1));
			coordinates.add(new Coordinate(colX+2,rowY));
			
	        for (Coordinate coordinate : coordinates) {
		        // not on border
				if (coordinate.getX()==0 || coordinate.getX()==width-1
						|| coordinate.getY()==0 || coordinate.getY() == height-1) {
					return false ;
				}
		        // value not is .
				if(grid[coordinate.getX()][coordinate.getY()] != '.' ) {
					return false ;
				}
			}

			ship.coordinates = coordinates;
			// draw
			for (Coordinate coordinate : coordinates) {
				grid[coordinate.getX()][coordinate.getY()] = ship.getType();
			}
		}
		return true;
    }
    

	public void placeShipsRandomly() {

		Random rand = new Random();
		int rowY;
		int colX;
		boolean vertical;
		for (Ship ship : ships) {
			boolean placed = false;
			switch (ship.getType()) {
			case 'A':
				while (!placed) {
					if (placeShipTypeA(ship)) {
						placed = true;
					}
				}
				break;
			case 'B':
				while (!placed) {
					vertical = rand.nextBoolean();
					if (placeShipTypeB(ship, vertical)) {
						placed = true;
					}
				}
				break;
			case 'C':
				while (!placed) {
					if (replaceShipTypeC(ship)) {
						placed = true;
					}
				}
				break;
			default:
				break;
			}
		}
	}

	private boolean replaceShipTypeC(Ship ship) {
		int rowY;
		int colX;
		Random rand = new Random();
		rowY = rand.nextInt(height - 1);
		colX = rand.nextInt(width - 1);

		List<Coordinate> coordinates = new ArrayList<>();
		coordinates.add(new Coordinate(colX,rowY));
		coordinates.add(new Coordinate(colX+1,rowY));
		coordinates.add(new Coordinate(colX,rowY+1));
		coordinates.add(new Coordinate(colX+1,rowY+1));

		for (Coordinate coordinate : coordinates) {
		    // not on border
			if (coordinate.getX()==0 || coordinate.getX()==width-1
					|| coordinate.getY()==0 || coordinate.getY() == height-1) {
				return false ;
			}
		    // value not is .
			if(grid[coordinate.getX()][coordinate.getY()] != '.' ) {
				return false ;
			}
		}
		
		ship.coordinates = coordinates;
		
		// draw
		for (Coordinate coordinate : coordinates) {
			grid[coordinate.getX()][coordinate.getY()] = ship.getType();
		}
		
		return true;
	}
    
}


