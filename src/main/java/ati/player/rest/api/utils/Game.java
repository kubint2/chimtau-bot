package ati.player.rest.api.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import ati.player.rest.api.entity.Coordinate;
import ati.player.rest.api.response.GameTurnResult;

public class Game {
    private int[][] board;
    private int boardWidth = 20;
    private int boardHeight = 8;
    private int shotsPerTurn = 1;
    private int hitCount = 0;
    
    private int previousHitX;
    private int previousHitY;
    
    private Boolean vertical = null;
    List<Coordinate> hitList = new ArrayList<>();
    Coordinate previousHit = new Coordinate (1,1);
    
    private List<Shot> previousShots = new ArrayList<>();

    public Game(int width, int height, int shots) {
        this.boardWidth = width;
        this.boardHeight = height;
        this.shotsPerTurn = shots;
        this.board = new int[height][width];
        
    }

    public void getGameTurnResult() {
    	GameTurnResult response = new GameTurnResult();
    	int shotTurn = 2;
    	List<int[]> coordinates = new ArrayList<>();

    	List<Coordinate> shotsTurn = getshotsTurn(shotTurn);
    	for (Coordinate coordinate : shotsTurn) {
    		coordinates.add(new int[]{coordinate.getX(),coordinate.getY()} );
		}

    	response.setCoordinates(coordinates);
    	
    	System.out.println(JsonUtil.objectToJson(response));
    }
    
    
    public List<Coordinate> getshotsTurn(int shotTurn) {
    	List<Coordinate> shotsTurn = new ArrayList<>();
    	
    	if(hitList.size() == 0) {
    		// Random shot
    		List<Coordinate> randomShots = getRandomShot(shotTurn);
    	} else if (hitList.size() == 1) {
    		// Shot neightBour previousHit
    		List<Coordinate> neightBours = getNeightBours(hitList);
    	} else if (hitList.size() == 2) {
    		// Shot vertical or Not
            Coordinate first = hitList.get(0);
            Coordinate second = hitList.get(1);
            
            List<Coordinate> neightBour2point = new ArrayList<>();
            // check vertical
            if (first.getY() == second.getY()) {
            	neightBour2point = getNeightBourTypeA(hitList, true);

            } else if (first.getX() == second.getX()) {
            	neightBour2point = getNeightBourTypeA(hitList, false);
            }

        	if (CollectionUtils.isNotEmpty(neightBour2point)) {
        		return neightBour2point;
        	} else {
        		// Shot neightBours hit list
        	}

    	} else if (hitList.size() == 3) {
            Coordinate first = hitList.get(0);
            Coordinate second = hitList.get(1);
            Coordinate third = hitList.get(2);
            
            List<Coordinate> neightBour3point = new ArrayList<>();
            // check vertical
            if (first.getY() == second.getY() && first.getY() == third.getY()) {
            	vertical = true;
            	// l???y 1 ??i???m trong 2 ??i???m ?????u v?? cu???i ????? shot
            	neightBour3point = getNeightBourTypeA(hitList, true);
            	
            	
            	
            } else if (first.getX() == second.getX() && first.getX() == third.getX()) {
            	vertical = false;
            	neightBour3point = getNeightBourTypeA(hitList, false);
            } else {
            	vertical = null;
            	// 3 ??i???m kh??ng th???ng h??ng -> t??m ??i???m g??c vu??ng
            	Coordinate coordinateFourth = findFourthCoordinate(first, second, third);
            	// Check valid coordinateFourth
            	if (board[coordinateFourth.getX()][coordinateFourth.getY()] == 0) {
            		neightBour3point.add(coordinateFourth);
            	}
            }
            
        	if (CollectionUtils.isNotEmpty(neightBour3point)) {
        		return neightBour3point;
        	} else {
        		// Shot neightBours hit list
        		List<Coordinate> neightBours = getNeightBours(hitList);
        	}
    	}
    	return shotsTurn;
    }
    
    private List<Coordinate> getNeightBours(List<Coordinate> hitList) {
    	List<Coordinate> result = new ArrayList<>();
    	for (Coordinate coordinate : hitList) {
    		int x = coordinate.getX();
        	int y = coordinate.getY();
        	
        	List<Coordinate> potentialTargets = new ArrayList<>();
        	potentialTargets.add(new Coordinate(x - 1, y));
        	potentialTargets.add(new Coordinate(x + 1, y));
        	potentialTargets.add(new Coordinate(x, y - 1));
        	potentialTargets.add(new Coordinate(x, y + 1));
        	
        	for (Coordinate target : potentialTargets) {
        		int posX = target.getX();
        		int posY = target.getY();
        		if ((posX >= 0 && posY <= boardWidth) 
        			&& (posY >= 0 && posY <= boardHeight) 
        			&& (board[posX][posY] != 0)) {
        			result.add(target);
        		}
        	}
    	}
    	
		return result;
	}

	private List<Coordinate> getRandomShot(int shortTurn) {
    	List<Coordinate> result = new ArrayList<>();
    	while (result.size() != shortTurn) {
    		Random random = new Random();
            int x = random.nextInt(boardWidth);
            int y = random.nextInt(boardHeight);
            if ((x + y) % 2 == 0 && board[x][y] != 0) {
            	continue;
            } else {
            	Coordinate coordinate = new Coordinate(x, y);
            	result.add(coordinate);
            }
    	}
		return result;
	}

	public List<Coordinate> getNeightBourTypeA(List<Coordinate> hitList, boolean vertical) {
    	List<Coordinate> neightBourTypeA = new ArrayList<>();
    	Coordinate min;
    	Coordinate max;
		if (vertical) {
			Coordinate minCoordinateRow = hitList.stream().min(Comparator.comparing(Coordinate::getX))
					.orElseThrow(NoSuchElementException::new);
			Coordinate maxCoordinateRow = hitList.stream().max(Comparator.comparing(Coordinate::getX))
					.orElseThrow(NoSuchElementException::new);

			int minRow = minCoordinateRow.getX();
			int maxRow = maxCoordinateRow.getX();
			int col = minCoordinateRow.getY();
			// validate
			if ((minRow - 1) > 0 && board[minRow - 1][col] == 0) {
				neightBourTypeA.add(new Coordinate(minRow - 1, col));
			}
			if ((maxRow + 1) < boardHeight && board[maxRow + 1][col] == 0) {
				neightBourTypeA.add(new Coordinate(minRow - 1, col));
			}
		} else {
			Coordinate minCoordinateCol = hitList.stream().min(Comparator.comparing(Coordinate::getY))
					.orElseThrow(NoSuchElementException::new);
			Coordinate maxCoordinateCol = hitList.stream().max(Comparator.comparing(Coordinate::getY))
					.orElseThrow(NoSuchElementException::new);

        	int minCol = minCoordinateCol.getY();
        	int maxCol = maxCoordinateCol.getY();
        	int row = minCoordinateCol.getX();
        	// validate
        	if ((minCol-1) > 0 && board[row][minCol-1] == 0) {
        		neightBourTypeA.add(new Coordinate(row, minCol-1));
        	}
        	if ((maxCol+1) < boardWidth && board[row][maxCol+1] == 0) {
        		neightBourTypeA.add(new Coordinate(row, maxCol+1));
        	}
		}    	
    	return neightBourTypeA;
    }
    
    
    public static Coordinate findFourthCoordinate(Coordinate first, Coordinate second, Coordinate third) {
        // T??m t???a ????? c??n l???i
        int x4, y4;
        if (first.getX() == second.getX()) {
            // Tr?????ng h???p ???????ng ch??o ch??nh n???m d???c
            x4 = third.getX();
            y4 = first.getY() + third.getY() - second.getY();
        } else {
            // Tr?????ng h???p ???????ng ch??o ch??nh n???m ngang
            x4 = first.getX() + third.getX() - second.getX();
            y4 = third.getY();
        }

        // T???o ?????i t?????ng Coordinate m???i ????? l??u t???a ????? c???a ?????nh c??n l???i
        Coordinate fourth = new Coordinate(x4, y4);
        return fourth;
    }

    public List<int[]> getAdjacentCells(int x, int y) {
        List<int[]> adjacentCells = new ArrayList<int[]>();
        if (y > 0) {
            adjacentCells.add(new int[] {x, y-1});
        }
        if (y < boardHeight-1) {
            adjacentCells.add(new int[] {x, y+1});
        }
        if (x > 0) {
            adjacentCells.add(new int[] {x-1, y});
        }
        if (x < boardWidth-1) {
            adjacentCells.add(new int[] {x+1, y});
        }
        return adjacentCells;
    }

    public List<int[]> getAroundCells(int x, int y) {
        List<int[]> unexploredAdjacentCells = new ArrayList<int[]>();
        for (int[] cell : getAdjacentCells(x, y)) {
            if (board[cell[1]][cell[0]] == 0) {
                unexploredAdjacentCells.add(cell);
            }
        }
        return unexploredAdjacentCells;
    }
    
    public int[] getAroundCell(int x, int y) {
        List<int[]> unexploredAdjacentCells = getAroundCells(x, y);
        if (unexploredAdjacentCells.isEmpty()) {
            return null;
        }
        return unexploredAdjacentCells.get(0);
    }

    public int[] getRandomCell() {
        Random random = new Random();
        int x = random.nextInt(boardWidth);
        int y = random.nextInt(boardHeight);
        while (board[y][x] != 0) {
            x = random.nextInt(boardWidth);
            y = random.nextInt(boardHeight);
        }
        return new int[] {x, y};
    }

    public boolean hasPreviousHit() {
        return previousHitX >= 0 && previousHitY >= 0;
    }

    public boolean isGameOver() {
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                if (board[y][x] == 1) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public int[] getNextShot() {
        int[] nextShot = new int[2];
        if (hasPreviousHit()) {
            nextShot = getAroundCell(previousHitX, previousHitY);
        }
        if (nextShot == null) {
            nextShot = getRandomCell();
        }
        return nextShot;
    }

    public Shot makeRandomShot(Board board) {
        Random random = new Random();
        int row = random.nextInt(boardHeight);
        int col = random.nextInt(boardWidth);
        return new Shot(row, col);
    }


    public Shot makeSmartShot(Board board) {
        List<Shot> previousShots = this.getPreviousShots();

        // T???o ra m???t t???p h???p ch???a c??c ?? h??ng x??m c???a c??c ?? ???? b??? b???n tr??ng
        Set<Coordinate> neighborCells = new HashSet<>();
        for (Shot shot : previousShots) {
            if (shot.getStatus() == "HIT") {
                int row = shot.getRow();
                int col = shot.getCol();
                neighborCells.add(new Coordinate(row - 1, col));
                neighborCells.add(new Coordinate(row + 1, col));
                neighborCells.add(new Coordinate(row, col - 1));
                neighborCells.add(new Coordinate(row, col + 1));
            }
        }

        // T??m ki???m t???t c??? c??c ?? h??ng x??m ch??a b??? b???n
        List<Coordinate> unshotNeighbors = new ArrayList<>();
        for (Coordinate neighbor : neighborCells) {
            if (!this.isShotted(neighbor)) {
                unshotNeighbors.add(neighbor);
            }
        }

        // N???u kh??ng c?? ?? h??ng x??m ch??a b??? b???n th?? tr??? v??? c?? b???n ng???u nhi??n
        if (unshotNeighbors.isEmpty()) {
            return makeRandomShot(board);
        }

        // Ng?????c l???i, ch???n m???t ?? h??ng x??m ng???u nhi??n ????? b???n
        Random random = new Random();
        int index = random.nextInt(unshotNeighbors.size());
        Coordinate unshotNeighbor = unshotNeighbors.get(index);
        return new Shot(unshotNeighbor.getX(), unshotNeighbor.getY());
    }

    private List<Shot> getPreviousShots() {
		return previousShots;
	}
    
    private boolean isShotted(Coordinate shot) {
		if(board[shot.getX()][shot.getY()] > 0) {
			return true;
		} {
			return false;
		}
	}

	// H??m n??y l???y ?????i t?????ng Board c???a ?????i th??? v?? t??m ki???m t???t c??? c??c ?? h??ng x??m c???a c??c ?? ???? b??? b???n tr??ng. Sau ????, t??m ki???m t???t c??? c??c ?? h??ng x??m ch??a b??? b???n v?? ch???n m???t ?? h??ng x

    // ????y l?? ph???n ti???p theo c???a code Java ????? ho??n th??nh thu???t to??n d?? b???n t??u ?????i ph????ng:

	/*

    // L???y t???a ????? m???i nh???t ????? b???n
	Coordinate shotCoordinate = getNextShotCoordinate();

    // Ki???m tra t???a ????? ???? ???????c b???n ch??a
    while (isShotted(shotCoordinate)) {
      // T???a ????? ???? ???????c b???n, chuy???n sang t???a ????? kh??c
      shotCoordinate = getNextShotCoordinate();
    }

    // G???i y??u c???u b???n t???i server
    String result = sendShotRequest(shotCoordinate);

    // X??? l?? k???t qu??? tr??? v???
    if (result.equals("HIT")) {
      // N???u b???n tr??ng, c???p nh???t tr???ng th??i v?? l??u t???a ?????
      updateShotStatus("HIT", shotCoordinate);
      lastShotCoordinate = shotCoordinate;
    } else if (result.equals("MISS")) {
      // N???u b???n h???t, c???p nh???t tr???ng th??i v?? l??u t???a ?????
      updateShotStatus("MISS", shotCoordinate);
      lastShotCoordinate = shotCoordinate;
    } else if (result.equals("SUNK")) {
      // N???u t??u b??? ch??m, c???p nh???t tr???ng th??i v?? x??a t???a ?????
      updateShotStatus("HIT", shotCoordinate);
      removeSunkShip(shotCoordinate);
      lastShotCoordinate = null;
    }
    
    */
}
