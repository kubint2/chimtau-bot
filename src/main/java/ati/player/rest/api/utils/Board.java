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
        
        this.grid = new char[height][width];
        for (int i = 0; i < height; i++) {
            Arrays.fill(grid[i], '.');
        }
    }

    public void print() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(grid[i][j] + " ");
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
    
    public boolean canPlaceShip(Ship ship, int row, int col) {
        if (row < 0 || col < 0 || row >= height || col >= width) {
            return false;
        }
        if (ship.isVertical()) {
            if (row + ship.getLength() > height) {
                return false;
            }
            for (int r = row; r < row + ship.getLength(); r++) {
                if (grid[r][col] != '.') {
                    return false;
                }
            }
        } else {
            if (col + ship.getLength() > width) {
                return false;
            }
            for (int c = col; c < col + ship.getLength(); c++) {
                if (grid[row][c] != '.') {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean placeShip(Ship ship, int row, int col) {
        if (!canPlaceShip(ship, row, col)) {
            return false;
        }
		List<int[]> coordinates = new ArrayList<>();
        if (ship.isVertical()) {
			coordinates.add(new int[]{row+1,col+1});
			coordinates.add(new int[]{row+1,col+2});
			coordinates.add(new int[]{row+1,col+3});
			coordinates.add(new int[]{row+1,col+4});
			coordinates.add(new int[]{row,col+2});

        	
            for (int r = row; r < row + ship.getLength(); r++) {
                grid[r][col] = ship.getType();
    			coordinates.add(new int[]{r,col});
            }
        } else {
            for (int c = col; c < col + ship.getLength(); c++) {
                grid[row][c] = ship.getType();
                coordinates.add(new int[]{row,c});
            }
        }
        ship.getShipData().setCoordinates(coordinates);
        ship.setPosition(row, col);
        return true;
    }

	public void placeShipsRandomly() {

		Random rand = new Random();
		int row;
		int col;
		boolean vertical;
		for (Ship ship : ships) {
			boolean placed = false;
			switch (ship.getType()) {
			case 'A':
				while (!placed) {
					row = rand.nextInt(height);
					col = rand.nextInt(width);
					System.out.println("Type " + ship.getType() + "  {" + row + ", " + col + "}");
					
					vertical = rand.nextBoolean();
					ship.setVertical(vertical);
					if (placeShip(ship, row, col)) {
						placed = true;
					}
				}
				break;
			case 'B':
				while (!placed) {
					vertical = rand.nextBoolean();
					if(vertical) {
						row = rand.nextInt(height-4);
						col = ThreadLocalRandom.current().nextInt(1, width-1);
						System.out.println("Type " + ship.getType() + "  {" + row + ", " + col + "}");
						
						if (grid[row][col] == '.' && grid[row+1][col] == '.' && grid[row+2][col] == '.'
								&& grid[row+3][col] == '.' && grid[row+1][col-1] == '.') {
							placed = true;
							// add coordinates
							List<int[]> coordinates = new ArrayList<>();
							coordinates.add(new int[]{row,col});
							coordinates.add(new int[]{row+1,col});
							coordinates.add(new int[]{row+2,col});
							coordinates.add(new int[]{row+3,col});
							coordinates.add(new int[]{row+1,col-1});
							ship.getShipData().setCoordinates(coordinates);
							
							// draw
							grid[row][col] = ship.getType();
							grid[row+1][col] = ship.getType();
							grid[row+2][col] = ship.getType();
							grid[row+3][col] = ship.getType();
							grid[row+1][col-1] = ship.getType();
						}
					}else {
						row = ThreadLocalRandom.current().nextInt(1, height-1);
						col = rand.nextInt(width-4);
						System.out.println("Type " + ship.getType() + "  {" + row + ", " + col + "}");
						
						if (grid[row+1][col+1] == '.' && grid[row+1][col+2] == '.' && grid[row+1][col+3] == '.'
								&& grid[row+1][col+4] == '.' && grid[row][col+2] == '.') {
							placed = true;

							List<int[]> coordinates = new ArrayList<>();
							coordinates.add(new int[]{row+1,col+1});
							coordinates.add(new int[]{row+1,col+2});
							coordinates.add(new int[]{row+1,col+3});
							coordinates.add(new int[]{row+1,col+4});
							coordinates.add(new int[]{row,col+2});
							ship.getShipData().setCoordinates(coordinates);
							
							// draw
							grid[row+1][col+1] = ship.getType();
							grid[row+1][col+2] = ship.getType();
							grid[row+1][col+3] = ship.getType();
							grid[row+1][col+4] = ship.getType();
							grid[row][col+2] = ship.getType();
						}
					}
				}
				break;
			case 'C':
				while (!placed) {
					row = rand.nextInt(height - 1);
					col = rand.nextInt(width - 1);
					
					System.out.println("Type " + ship.getType() + "  {" + row + ", " + col);
					if (grid[row][col] == '.' && grid[row][col + 1] == '.' && grid[row + 1][col] == '.'
							&& grid[row + 1][col + 1] == '.') {
						placed = true;

						List<int[]> coordinates = new ArrayList<>();
						coordinates.add(new int[]{row,col});
						coordinates.add(new int[]{row,col+1});
						coordinates.add(new int[]{row+1,col});
						coordinates.add(new int[]{row+1,col+1});
						ship.getShipData().setCoordinates(coordinates);
						
						// draw
						grid[row][col] = ship.getType();
						grid[row][col+1] = ship.getType();
						grid[row+1][col] = ship.getType();
						grid[row+1][col+1] = ship.getType();
					}
				}
				break;
			default:
				break;
			}
		}
	}
    
}


