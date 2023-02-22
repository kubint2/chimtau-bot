package ati.player.rest.api.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Board {
    private int width;
    private int height;
    private char[][] grid;
    private List<Ship> ships;

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
    
    public boolean canPlaceShip(Ship ship, int rowY, int colX) {
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

    public boolean placeShip(Ship ship, int rowY, int colX) {
        if (!canPlaceShip(ship, rowY, colX)) {
            return false;
        }
		List<int[]> coordinates = new ArrayList<>();
        if (ship.isVertical()) {
			coordinates.add(new int[]{colX+1,rowY+1});
			coordinates.add(new int[]{colX+2,rowY+1});
			coordinates.add(new int[]{colX+3,rowY+1});
			coordinates.add(new int[]{colX+4,rowY+1});
			coordinates.add(new int[]{colX+2,rowY+1});

        	
            for (int r = rowY; r < rowY + ship.getLength(); r++) {
                grid[colX][r] = ship.getType();
    			coordinates.add(new int[]{colX, r});
            }
        } else {
            for (int c = colX; c < colX + ship.getLength(); c++) {
                grid[c][rowY] = ship.getType();
                coordinates.add(new int[]{c,rowY});
            }
        }
        ship.getShipData().setCoordinates(coordinates);
        ship.setPosition(colX, rowY);
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
					rowY = rand.nextInt(height);
					colX = rand.nextInt(width);
					System.out.println("Type " + ship.getType() + "  {" + colX + ", " + rowY + "}");
					
					vertical = rand.nextBoolean();
					ship.setVertical(vertical);
					if (placeShip(ship, rowY, colX)) {
						placed = true;
					}
				}
				break;
			case 'B':
				while (!placed) {
					vertical = rand.nextBoolean();
					if(vertical) {
						rowY = rand.nextInt(height-4);
						colX = ThreadLocalRandom.current().nextInt(1, width-1);
						System.out.println("Type " + ship.getType() + "  {" + colX + ", " + rowY + "}");
						
						if (grid[colX][rowY] == '.' && grid[colX][rowY+1] == '.' && grid[colX][rowY+2] == '.'
								&& grid[colX][rowY+3] == '.' && grid[colX-1][rowY+1] == '.') {
							placed = true;
							// add coordinates
							List<int[]> coordinates = new ArrayList<>();
							coordinates.add(new int[]{colX,rowY});
							coordinates.add(new int[]{colX,rowY+1});
							coordinates.add(new int[]{colX,rowY+2});
							coordinates.add(new int[]{colX,rowY+3});
							coordinates.add(new int[]{colX-1,rowY+1});
							ship.getShipData().setCoordinates(coordinates);
							
							// draw
							grid[colX][rowY] = ship.getType();
							grid[colX][rowY+1]= ship.getType();
							grid[colX][rowY+2] = ship.getType();
							grid[colX][rowY+3] = ship.getType();
							grid[colX-1][rowY+1] = ship.getType();
						}
					}else {
						rowY = ThreadLocalRandom.current().nextInt(1, height-1);
						colX = rand.nextInt(width-4);
						System.out.println("Type " + ship.getType() + "  {" + colX + ", " +  rowY + "}");
						
						if (grid[colX+1][rowY+1] == '.' && grid[colX+2][rowY+1] == '.' && grid[colX+3][rowY+1] == '.'
								&& grid[colX+4][rowY+1] == '.' && grid[colX+2][rowY] == '.') {
							placed = true;

							List<int[]> coordinates = new ArrayList<>();
							coordinates.add(new int[]{colX+1,rowY+1});
							coordinates.add(new int[]{colX+2,rowY+1});
							coordinates.add(new int[]{colX+3,rowY+1});
							coordinates.add(new int[]{colX+4,rowY+1});
							coordinates.add(new int[]{colX+2,rowY});
							ship.getShipData().setCoordinates(coordinates);
							
							// draw
							grid[colX+1][rowY+1] = ship.getType();
							grid[colX+2][rowY+1] = ship.getType();
							grid[colX+3][rowY+1] = ship.getType();
							grid[colX+4][rowY+1] = ship.getType();
							grid[colX+2][rowY] = ship.getType();
						}
					}
				}
				break;
			case 'C':
				while (!placed) {
					rowY = rand.nextInt(height - 1);
					colX = rand.nextInt(width - 1);
					
					System.out.println("Type " + ship.getType() + "  {" + colX + ", " + rowY + "}");
					if (grid[colX][rowY] == '.' && grid[colX + 1][rowY] == '.' && grid[colX][rowY + 1] == '.'
							&& grid[colX + 1][rowY + 1] == '.') {
						placed = true;

						List<int[]> coordinates = new ArrayList<>();
						coordinates.add(new int[]{colX,rowY});
						coordinates.add(new int[]{colX+1,rowY});
						coordinates.add(new int[]{colX,rowY+1});
						coordinates.add(new int[]{colX+1,rowY+1});
						ship.getShipData().setCoordinates(coordinates);
						
						// draw
						grid[colX][rowY] = ship.getType();
						grid[colX+1][rowY] = ship.getType();
						grid[colX][rowY+1] = ship.getType();
						grid[colX+1][rowY+1] = ship.getType();
					}
				}
				break;
			default:
				break;
			}
		}
	}
    
}


