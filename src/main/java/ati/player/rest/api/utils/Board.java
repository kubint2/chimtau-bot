package ati.player.rest.api.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.collections.CollectionUtils;

import ati.player.rest.api.entity.Coordinate;
import ati.player.rest.api.entity.PlaceShipConfig;

public class Board {
	
    private int width;
    private int height;
    private char[][] grid;
    private List<Ship> ships;
//    public Boolean flagCanPutOnBorder = null;
    public Boolean flagCanHaveNeighbour = null;
    
    int tryCountCheckNeghbour = 0;
    public static final char DOT = '.';
    private static final int MAX_TRY_COUNT = 300;
    private static final int MIN_TRY_COUNT = 40;
 
    List<Coordinate> coordinatesShotted = new ArrayList<>();
    HashMap<String, PlaceShipConfig> shipConfigMap = new HashMap<>();

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.ships = new ArrayList<>();
        
        this.grid = new char[width][height];
        for (int i = 0; i < width; i++) {
            Arrays.fill(grid[i], DOT);
        }
    }
    
    public Board(int width, int height, List<Coordinate> coordinatesShotted, HashMap<String, PlaceShipConfig> shipConfigMap) {
        this.width = width;
        this.height = height;
        this.ships = new ArrayList<>();
        
        this.grid = new char[width][height];
        for (int i = 0; i < width; i++) {
            Arrays.fill(grid[i], DOT);
        }
        
        this.shipConfigMap = shipConfigMap;
        
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

        // this.flagCanHaveNeighbour = null;
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
    	if(shipConfigMap.containsKey(ship.typeDesc)) {
    		ship.setPriorityOrder(ship.getPriorityOrder() + 20);
    		PlaceShipConfig shipConf = shipConfigMap.get(ship.typeDesc);
    		if(!shipConf.isModeRandom() && CollectionUtils.isNotEmpty(shipConf.getCoordinates())) {
    			ship.setPriorityOrder(ship.getPriorityOrder() + 10);
    		}
    	}
        ships.add(ship);
    }

    public List<Ship> getShips() {
        return ships;
    }
    
    
    private boolean isPosOnboard(int colX, int rowY) {
        if (rowY < 0 || colX < 0 || rowY >= height || colX >= width) {
            return false;
        }
        return true;
    }
    
    public boolean validPlaceShip (List<Coordinate> coordinates, int tryCount) {
    	for (Coordinate coordinate : coordinates) {
        	int colX = coordinate.getX();
        	int rowY = coordinate.getY();
            if (!this.isPosOnboard(colX, rowY)) {
                return false;
            }

    		if(grid[colX][rowY] != DOT ) {
    			return false ;
    		}

			if (flagCanHaveNeighbour != null && !flagCanHaveNeighbour && tryCount >= MIN_TRY_COUNT) {
				List<Coordinate> neighbours = GameUtil.getCoordinateNeighbours(coordinate, width, height);
				if (CollectionUtils.isNotEmpty(coordinates)) {
					for (Coordinate neighbour : neighbours) {
						if (grid[neighbour.getX()][neighbour.getY()] != DOT) {
							tryCountCheckNeghbour++;
							return false;
						}
					}
				}
			}
		}
    	return true;
    }
    

	
	public List<Coordinate> placeShipTypeA(Ship ship) {
		Random rand = new Random();
		int rowY, colX;
		List<Coordinate> coordinates;
		int tryCount = MAX_TRY_COUNT;
		Boolean vertical;

		while (tryCount-- > 0) {
			PlaceShipConfig shipConfigShipTypeA = this.shipConfigMap.get(ship.typeDesc);
			if (shipConfigShipTypeA == null) {
				shipConfigShipTypeA = new PlaceShipConfig(); // default config
				this.shipConfigMap.put(ship.typeDesc, shipConfigShipTypeA);
			}

			if (!shipConfigShipTypeA.isModeRandom()) {
				// Fixed position
				if (CollectionUtils.isNotEmpty(shipConfigShipTypeA.getCoordinates())
						&& shipConfigShipTypeA.index < shipConfigShipTypeA.getCoordinates().size()) {

					Coordinate coord = shipConfigShipTypeA.getCoordinates().get(shipConfigShipTypeA.index);
					if (shipConfigShipTypeA.index < shipConfigShipTypeA.getVerticals().size()) {
						vertical = shipConfigShipTypeA.getVerticals().get(shipConfigShipTypeA.index);
					} else {
						vertical = true;
					}

					shipConfigShipTypeA.index++;

					if (vertical == null)
						vertical = true;
					colX = coord.getX();
					rowY = coord.getY();
					coordinates = GameUtil.getCoordShipTypeA(colX, rowY, ship.getLength(), vertical);
					// validate
					if (!validPlaceShip(coordinates, tryCount)) {
						continue;
					}
					return coordinates;
				}
			}

			// OTHER
			if (shipConfigShipTypeA.isVetical() != null) {
				vertical = shipConfigShipTypeA.isVetical();
			} else {
				vertical = rand.nextBoolean();
			}

			if (shipConfigShipTypeA.getMaxShipOnCorner() > 0) {
				shipConfigShipTypeA.setMaxShipOnCorner(shipConfigShipTypeA.getMaxShipOnCorner() - 1);
				// set x y
				if (vertical) {
					int[] arrX = { 0, this.width - 1 };
					colX = arrX[rand.nextInt(2)];
					int[] arrY = { 0, this.height - 1 - 1 };
					rowY = arrY[rand.nextInt(2)];
				} else {
					int[] arrX = { 0, this.width - 1 - 1 };
					colX = arrX[rand.nextInt(2)];
					int[] arrY = { 0, this.height - 1 };
					rowY = arrY[rand.nextInt(2)];
				}
			} else if (shipConfigShipTypeA.getMaxShipOnBorder() > 0) {
				shipConfigShipTypeA.setMaxShipOnBorder(shipConfigShipTypeA.getMaxShipOnBorder() - 1);
				// set x y
				if (vertical) {
					int[] arrX = { 0, this.width - 1 };
					colX = arrX[rand.nextInt(2)];
					int[] arrY = { 0, this.height - 1 - 1 };
					rowY = arrY[rand.nextInt(2)];
				} else {
					int[] arrX = { 0, this.width - 1 - 1 };
					colX = arrX[rand.nextInt(2)];
					int[] arrY = { 0, this.height - 1 };
					rowY = arrY[rand.nextInt(2)];
				}
			} else {
				if (vertical) {
					rowY = rand.nextInt(height - ship.getLength() + 1);
					colX = rand.nextInt(width);
				} else {
					rowY = rand.nextInt(height);
					colX = rand.nextInt(width - ship.getLength() + 1);
				}
			}

			coordinates = GameUtil.getCoordShipTypeA(colX, rowY, ship.getLength(), vertical);
			// validate
			if (!validPlaceShip(coordinates, tryCount)) {
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
		Boolean vertical;

		while (tryCount-- > 0) {
			PlaceShipConfig shipConfigShipTypeCV = this.shipConfigMap.get(ship.typeDesc);
			if (shipConfigShipTypeCV == null) {
				shipConfigShipTypeCV = new PlaceShipConfig(); // default config
				this.shipConfigMap.put(ship.typeDesc, shipConfigShipTypeCV);
			}

			if (!shipConfigShipTypeCV.isModeRandom()) {
				// Fixed position
				if (CollectionUtils.isNotEmpty(shipConfigShipTypeCV.getCoordinates())
						&& shipConfigShipTypeCV.index < shipConfigShipTypeCV.getCoordinates().size()) {

					Coordinate coord = shipConfigShipTypeCV.getCoordinates().get(shipConfigShipTypeCV.index);
					if (shipConfigShipTypeCV.index < shipConfigShipTypeCV.getVerticals().size()) {
						vertical = shipConfigShipTypeCV.getVerticals().get(shipConfigShipTypeCV.index);
					} else {
						vertical = true;
					}

					shipConfigShipTypeCV.index++;

					if (vertical == null)
						vertical = true;

					colX = coord.getX();
					rowY = coord.getY();
					coordinates = GameUtil.getCoordShipCV(colX, rowY, vertical);

					// validate
					if (!validPlaceShip(coordinates, tryCount)) {
						continue;
					}
					return coordinates;
				}
			}

			// OTHER
			if (shipConfigShipTypeCV.isVetical() != null) {
				vertical = shipConfigShipTypeCV.isVetical();
			} else {
				vertical = rand.nextBoolean();
			}

			
			if (shipConfigShipTypeCV.getMaxShipOnCorner() > 0) {
				shipConfigShipTypeCV.setMaxShipOnCorner(shipConfigShipTypeCV.getMaxShipOnCorner() - 1);
				// set x y
				if (vertical) {
					colX = 1;
					rowY = 1;
				} else {
					colX = 1;
					rowY = 1;
				}
			} else if (shipConfigShipTypeCV.getMaxShipOnBorder() > 0) {
				shipConfigShipTypeCV.setMaxShipOnBorder(shipConfigShipTypeCV.getMaxShipOnBorder() - 1);
				// set x y
				if (vertical) {
					colX = 19;
					rowY = ThreadLocalRandom.current().nextInt(1, height - 2);
				} else {
					colX = ThreadLocalRandom.current().nextInt(1, width - 2);
					rowY = 7;
				}
			} else {
				// random
				if (vertical) {
					rowY = rand.nextInt(height - 2);
					colX = ThreadLocalRandom.current().nextInt(1, width);
				} else {
					rowY = ThreadLocalRandom.current().nextInt(1, height);
					colX = rand.nextInt(width - 2);
				}
			}

			// add coordinates
			coordinates = GameUtil.getCoordShipCV(colX, rowY, vertical);

			if (!validPlaceShip(coordinates, tryCount)) {
				continue;
			}

			return coordinates;

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
			PlaceShipConfig shipConfigShipTypeOR = this.shipConfigMap.get(ship.typeDesc);
			if (shipConfigShipTypeOR == null) {
				shipConfigShipTypeOR = new PlaceShipConfig(); // default config
				this.shipConfigMap.put(ship.typeDesc, shipConfigShipTypeOR);
			}

			if (!shipConfigShipTypeOR.isModeRandom()) {
				// Fixed position
				if (CollectionUtils.isNotEmpty(shipConfigShipTypeOR.getCoordinates())
						&& shipConfigShipTypeOR.index < shipConfigShipTypeOR.getCoordinates().size()) {

					Coordinate coord = shipConfigShipTypeOR.getCoordinates().get(shipConfigShipTypeOR.index);

					shipConfigShipTypeOR.index++;

					colX = coord.getX();
					rowY = coord.getY();
					coordinates = GameUtil.getCoordShipOR(colX, rowY);

					// validate
					if (!validPlaceShip(coordinates, tryCount)) {
						continue;
					}
					return coordinates;
				}
			}

			// OTHER

			if (shipConfigShipTypeOR.getMaxShipOnCorner() > 0) {
				shipConfigShipTypeOR.setMaxShipOnCorner(shipConfigShipTypeOR.getMaxShipOnCorner() - 1);
				// set x y
				int arrX[] = { 0, this.width - 2 };
				colX = arrX[rand.nextInt(2)];
				int arrY[] = { 0, this.height - 2 };
				rowY = arrY[rand.nextInt(2)];

			} else if (shipConfigShipTypeOR.getMaxShipOnBorder() > 0) {
				shipConfigShipTypeOR.setMaxShipOnBorder(shipConfigShipTypeOR.getMaxShipOnBorder() - 1);
				// set x y
				boolean vetical = rand.nextBoolean();
				if (vetical) {
					int[] arr = { 0, this.width - 2 };
					colX = arr[rand.nextInt(2)];
					rowY = rand.nextInt(height - 1);
				} else {
					int[] arr = { 0, this.height - 2 };
					rowY = arr[rand.nextInt(2)];
					colX = rand.nextInt(width - 1);
				}
			} else {
				// random mode
				rowY = rand.nextInt(height - 1);
				colX = rand.nextInt(width - 1);
			}

			coordinates = GameUtil.getCoordShipOR(colX, rowY);

			if (!validPlaceShip(coordinates, tryCount)) {
				continue;
			}

			return coordinates;
		}
		return null;
	}    

	public void placeShipsRandomly() {
		ships.sort((o1, o2) -> o2.getPriorityOrder() - o1.getPriorityOrder());
		boolean flagLoop = true;
		while (flagLoop) {
			flagLoop = false;

			for (Ship ship : ships) {
				List<Coordinate> coordinates = null;
				if (Ship.SHIP_DD.equalsIgnoreCase(ship.typeDesc) || Ship.SHIP_CA.equalsIgnoreCase(ship.typeDesc)
						|| Ship.SHIP_BB.equalsIgnoreCase(ship.typeDesc)) {
					coordinates = placeShipTypeA(ship);
				} else if (Ship.SHIP_CV.equalsIgnoreCase(ship.typeDesc)) {
					coordinates = placeShipCV(ship);
				} else if (Ship.SHIP_OR.equalsIgnoreCase(ship.typeDesc)) {
					coordinates = placeShipOR(ship);
				}

				if (CollectionUtils.isEmpty(coordinates)) {
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


