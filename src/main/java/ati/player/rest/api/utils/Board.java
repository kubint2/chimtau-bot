package ati.player.rest.api.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.collections.CollectionUtils;

import ati.player.rest.api.entity.Coordinate;

public class Board {
	
    private int width;
    private int height;
    private char[][] grid;
    private List<Ship> ships;
    public Boolean flagCanPutOnBorder = null;
    public Boolean flagCanHaveNeighbour = null;
    public Boolean flagPlaceVertical = null;
    
    int tryCountCheckNeghbour = 0;
    public static final char DOT = '.';
    private static final int MAX_TRY_COUNT = 300;

    
    
    List<Coordinate> coordinatesShotted = new ArrayList<>();
    
    List<Coordinate> coordinatesPutted = new ArrayList<>();

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.ships = new ArrayList<>();
        
        this.grid = new char[width][height];
        for (int i = 0; i < width; i++) {
            Arrays.fill(grid[i], DOT);
        }
    }
    
    public Board(int width, int height, List<Coordinate> coordinatesShotted) {
        this.width = width;
        this.height = height;
        this.ships = new ArrayList<>();
        
        this.grid = new char[width][height];
        for (int i = 0; i < width; i++) {
            Arrays.fill(grid[i], DOT);
        }
        
        this.coordinatesShotted = coordinatesShotted;
        if(CollectionUtils.isNotEmpty(coordinatesShotted)) {
        	for (Coordinate coordinate : coordinatesShotted) {
        		grid[coordinate.getX()][coordinate.getY()] = 'x';
			}
        }
    }

    public void resetBoard() {
        this.grid = new char[width][height];
        for (int i = 0; i < width; i++) {
            Arrays.fill(grid[i], DOT);
        }

        if(CollectionUtils.isNotEmpty(coordinatesShotted)) {
        	for (Coordinate coordinate : coordinatesShotted) {
        		grid[coordinate.getX()][coordinate.getY()] = 'x';
			}
        }
        
        
        this.flagCanPutOnBorder = null;
        this.flagCanHaveNeighbour = null;
    }
    
    
    public void print() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(grid[x][y] + " ");
            }
            System.out.println();
        }
    }
    
    public List<Coordinate> getListDot() {
    	List<Coordinate> coordinates = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
            	if (grid[x][y] == DOT) {
            		coordinates.add(new Coordinate(x, y));
            	}
            }
        }
		return coordinates;
    }

    
    public void addShip(Ship ship) {
        ships.add(ship);
    }

    public List<Ship> getShips() {
        return ships;
    }
    
    public boolean validPlaceShip (List<Coordinate> coordinates) {
    	for (Coordinate coordinate : coordinates) {
        	int colX = coordinate.getX();
        	int rowY = coordinate.getY();
            if (rowY < 0 || colX < 0 || rowY >= height || colX >= width) {
                return false;
            }
        	
            if(this.flagCanPutOnBorder!= null && !this.flagCanPutOnBorder) {
    			if (colX == 0 || colX == width - 1 || rowY == 0 || rowY == height - 1) {
    				return false;
    			}
            }

    		if(grid[colX][rowY] != DOT ) {
    			return false ;
    		}
    		
            if (flagCanHaveNeighbour!=null && !flagCanHaveNeighbour && tryCountCheckNeghbour < 300) {
            	List<Coordinate> neighbours = GameUtil.getCoordinateNeighbours(coordinate, width, height);
            	if (CollectionUtils.isNotEmpty(coordinates)) {
            		for (Coordinate neighbour : neighbours) {
            			if(grid[neighbour.getX()][neighbour.getY()] != DOT) {
                        	tryCountCheckNeghbour++;
            				return false;
            			}
					}
            	}
            }
		}
    	return true;
    }
    
	public List<Coordinate> placeShipDDCABB(Ship ship) {
		Random rand = new Random();
		int rowY, colX;
		List<Coordinate> coordinates;
		int tryCount = MAX_TRY_COUNT;
		boolean vertical;

		while (tryCount-- > 0) {
			rowY = rand.nextInt(height);
			colX = rand.nextInt(width);
			//vertical = rand.nextBoolean();
			if (this.flagPlaceVertical == null) {
				vertical = rand.nextBoolean();
			} else {
				vertical = this.flagPlaceVertical;
			}
			ship.setVertical(vertical);
			coordinates = new ArrayList<>();

			if (ship.isVertical()) {
				for (int r = rowY; r < rowY + ship.getLength(); r++) {
					coordinates.add(new Coordinate(colX, r));
				}
			} else {
				for (int c = colX; c < colX + ship.getLength(); c++) {
					coordinates.add(new Coordinate(c, rowY));
				}
			}

			if (!validPlaceShip(coordinates)) {
				continue;
			}

			return coordinates;
		}

		return null;
	}
    
    public List<Coordinate> placeShipCV(Ship ship) {
		Random rand = new Random();
		int rowY, colX;
		List<Coordinate> coordinates;
		int tryCount = MAX_TRY_COUNT;
		boolean vertical;
		
		while (tryCount-- > 0) {
			if (this.flagPlaceVertical == null) {
				vertical = rand.nextBoolean();
			} else {
				vertical = this.flagPlaceVertical;
			}

			if (vertical) {
				rowY = rand.nextInt(height - 4);
				colX = ThreadLocalRandom.current().nextInt(1, width - 1);

				// add coordinates
				coordinates = new ArrayList<>();
				coordinates.add(new Coordinate(colX, rowY)); //
				coordinates.add(new Coordinate(colX, rowY + 1));
				coordinates.add(new Coordinate(colX, rowY + 2));
				coordinates.add(new Coordinate(colX, rowY + 3));
				coordinates.add(new Coordinate(colX - 1, rowY + 1));

				if (!validPlaceShip(coordinates)) {
					continue;
				}
				
				return coordinates;

			} else {
				rowY = ThreadLocalRandom.current().nextInt(1, height - 1);
				colX = rand.nextInt(width - 4);

				coordinates = new ArrayList<>();
				coordinates.add(new Coordinate(colX + 1, rowY + 1));
				coordinates.add(new Coordinate(colX + 2, rowY + 1));
				coordinates.add(new Coordinate(colX + 3, rowY + 1));
				coordinates.add(new Coordinate(colX + 4, rowY + 1));
				coordinates.add(new Coordinate(colX + 2, rowY));

				if (!validPlaceShip(coordinates)) {
					continue;
				}

				return coordinates;
			}
		}
		return null;
    }

	private List<Coordinate> placeShipOR(Ship ship) {
		int rowY;
		int colX;
		Random rand = new Random();
		List<Coordinate> coordinates = new ArrayList<>();
		int tryCount = MAX_TRY_COUNT;
		
		while (tryCount-- > 0) {
			rowY = rand.nextInt(height - 1);
			colX = rand.nextInt(width - 1);

			coordinates = new ArrayList<>();
			coordinates.add(new Coordinate(colX,rowY));
			coordinates.add(new Coordinate(colX+1,rowY));
			coordinates.add(new Coordinate(colX,rowY+1));
			coordinates.add(new Coordinate(colX+1,rowY+1));

			if (!validPlaceShip(coordinates)) {
				continue;
			}

			return coordinates;
		}
		return null;
	}    

	public void placeShipsRandomly() {
		ships.sort((o1, o2) -> o2.getLength() - o1.getLength());
		boolean flagLoop = true;
		while (flagLoop) {
			flagLoop = false;

			for (Ship ship : ships) {
				List<Coordinate> coordinates = null;
				if (Ship.SHIP_DD.equalsIgnoreCase(ship.typeDesc) || Ship.SHIP_CA.equalsIgnoreCase(ship.typeDesc)
						|| Ship.SHIP_BB.equalsIgnoreCase(ship.typeDesc)) {
					coordinates = placeShipDDCABB(ship);
				} else if (Ship.SHIP_CV.equalsIgnoreCase(ship.typeDesc)) {
					coordinates = placeShipCV(ship);
				} else if (Ship.SHIP_OR.equalsIgnoreCase(ship.typeDesc)) {
					coordinates = placeShipOR(ship);
				}

				if (CollectionUtils.isEmpty(coordinates)) {
					flagCanPutOnBorder = null;
					flagCanHaveNeighbour = null;

					
					this.resetBoard();
					
					flagLoop = true;
					break;
				} else {
					for (Coordinate coordinate : coordinates) {
						grid[coordinate.getX()][coordinate.getY()] = ship.getType();
					}
					ship.coordinates = coordinates;
				}
			}
		}
	}    
}


