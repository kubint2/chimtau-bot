package ati.player.rest.api.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import ati.player.rest.api.entity.Coordinate;
import ati.player.rest.api.request.ShipRequest;

public class BotPlayer {
	public int[][] board;
	public int boardWidth;
	public int boardHeight;
	public String player1;
	public String player2;
	public String winner;
	public String loser;
	public int maxShots;
	public List<ShipRequest> ships;
	
	
	
	// private BotPlayer instance;

	public BotPlayer() {
	}
	
	
//	public static synchronized BotPlayer getInstance() {
//		if(instance == null) {
//			instance = new BotPlayer();
//		}
//		return instance;
//	}
	
	public BotPlayer(int width, int height, List<ShipRequest> ships) {
		// instance = new BotPlayer();
		this.boardWidth = width;
		this.boardHeight = height;
		this.ships = ships;

		for (ShipRequest shipRequest : ships) {
			this.shipMap.put(shipRequest.getType(), shipRequest.getQuantity());
		}
		
		// init board
		this.board = new int[width][height];
	}

	// implement 
    public List<Coordinate> hitCoordinateList = new ArrayList<>();
    public Coordinate previousHit;
    public int typeCheck = 0 ; // 0: random , 1: typeA , 2: type B, 3: type C, >3 other 
    private Boolean vertical = null;
    public Map<String, Integer> shipMap = new HashMap<>();
    

    public void resetCalculator() {
    	typeCheck = 0;
    	previousHit = null;
    	vertical = null;
    	// hitCoordinateList = new ArrayList<>();
    }
    
	public List<int[]> getShotsTurnResult() {
		List<int[]> result = new ArrayList<>();
		List<Coordinate> showTurns = this.getshotsTurn();
		if(CollectionUtils.isEmpty(showTurns)) {
			// return random shot
			Coordinate Coordinate = makeRandomShot();
			result.add(new int[] {Coordinate.getX(), Coordinate.getY()});
		} else {
			for (Coordinate coordinate : showTurns) {
				result.add(new int[] {coordinate.getX(), coordinate.getY()});
				if(--maxShots <= 0) {
					break;
				}
			}
		}
		//
		return result;
	}
	
	public List<Coordinate> getshotsTurn() {
		if (hitCoordinateList.size() == 0) {
			typeCheck = 0;
			// Random shot
			List<Coordinate> shotsTurn = new ArrayList<>();
			shotsTurn.add(makeRandomShot());
			return shotsTurn;
		} else if (hitCoordinateList.size() == 1) {
			typeCheck = 1;
			// Shot neightBour previousHit
			return makeShotNeighbors();
		} else if (hitCoordinateList.size() == 2) {
			// Shot vertical or Not
			Coordinate first = hitCoordinateList.get(0);
			Coordinate second = hitCoordinateList.get(1);

			List<Coordinate> neightBour2point = new ArrayList<>();
			// check vertical
			if (first.getY() == second.getY()) {
				typeCheck = 2;
				neightBour2point = getNeightBourTypeA(hitCoordinateList, false);

			} else if (first.getX() == second.getX()) {
				typeCheck = 3;
				neightBour2point = getNeightBourTypeA(hitCoordinateList, true);
			}

			if (CollectionUtils.isNotEmpty(neightBour2point)) {
				return neightBour2point;
			} else {
				typeCheck = 4;
				// Shot neightBour previousHit
				return makeShotNeighbors();
			}

		} else if (hitCoordinateList.size() == 3) {
			Coordinate first = hitCoordinateList.get(0);
			Coordinate second = hitCoordinateList.get(1);
			Coordinate third = hitCoordinateList.get(2);

			List<Coordinate> neightBour3point = new ArrayList<>();
			// check vertical
			if (first.getY() == second.getY() && first.getY() == third.getY()) {
				typeCheck = 3;
				vertical = false;
				// lấy 1 điểm trong 2 điểm đầu và cuối để shot
				neightBour3point = getNeightBourTypeA(hitCoordinateList, false);
			} else if (first.getX() == second.getX() && first.getX() == third.getX()) {
				vertical = true;
				neightBour3point = getNeightBourTypeA(hitCoordinateList, true);
			} else {
				vertical = null;
				// 3 điểm không thẳng hàng -> tìm điểm góc vuông
				Coordinate coordinateFourth = findFourthCoordinate(first, second, third);
				// Check valid coordinateFourth
				if (isValidForShot(coordinateFourth)) {
					neightBour3point.add(coordinateFourth);
				} else {
					// shot 
					neightBour3point = makeShotNeighbors(findSquareCorner(hitCoordinateList));
				}
			}

			if (CollectionUtils.isNotEmpty(neightBour3point)) {
				return neightBour3point;
			} else {
				// Shot neightBour previousHit
				return makeShotNeighbors();
			}
		} else if (hitCoordinateList.size() == 4) {
			Coordinate first = hitCoordinateList.get(0);
			Coordinate second = hitCoordinateList.get(1);
			Coordinate third = hitCoordinateList.get(2);
			Coordinate four = hitCoordinateList.get(3);
			List<Coordinate> neightBour4point = new ArrayList<>();
			Coordinate remainPoin;

			if (first.getY() == second.getY() && first.getY() == third.getY()&& first.getY() == four.getY()) {
				vertical = false;
				Coordinate minCoordinateColX = hitCoordinateList.stream().min(Comparator.comparing(Coordinate::getX))
						.orElseThrow(NoSuchElementException::new);
				remainPoin = new Coordinate(minCoordinateColX.getX()+1, minCoordinateColX.getY()-1);
				if (isValidForShot(remainPoin)) {
					neightBour4point.add(remainPoin);
				}
			} else if (first.getX() == second.getX() && first.getX() == third.getX()&& first.getX() == four.getX()) {
				vertical = true;
				Coordinate minCoordinateRowY = hitCoordinateList.stream().min(Comparator.comparing(Coordinate::getY))
						.orElseThrow(NoSuchElementException::new);
				remainPoin = new Coordinate(minCoordinateRowY.getX() - 1, minCoordinateRowY.getY() + 1);
				if (isValidForShot(remainPoin)) {
					neightBour4point.add(remainPoin);
				}
			} else {
				List<Coordinate> listCoordinateInline = removeNonCollinearCoordinates(hitCoordinateList);
				if ((listCoordinateInline.get(0).getX() == listCoordinateInline.get(1).getX())
						&& (listCoordinateInline.get(0).getX() == listCoordinateInline.get(2).getX())) {
					Coordinate maxCoordinateRowY = hitCoordinateList.stream().max(Comparator.comparing(Coordinate::getY))
							.orElseThrow(NoSuchElementException::new);
					remainPoin = new Coordinate(maxCoordinateRowY.getX(), maxCoordinateRowY.getY()+1);
					if (isValidForShot(remainPoin)) {
						neightBour4point.add(remainPoin);
					}
				} else {
					Coordinate maxCoordinateColX = hitCoordinateList.stream().max(Comparator.comparing(Coordinate::getX))
							.orElseThrow(NoSuchElementException::new);
					remainPoin = new Coordinate(maxCoordinateColX.getX()+1, maxCoordinateColX.getY());
					if (isValidForShot(remainPoin)) {
						neightBour4point.add(remainPoin);
					}
				}
			}

			if (CollectionUtils.isNotEmpty(neightBour4point)) {
				return neightBour4point;
			} else {
				// Shot neightBour previousHit
				return makeShotNeighbors();
			}
		} else {
			// Shot neightBour previousHit
			return makeShotNeighbors();
		}
		// return shotsTurn;
	}
    
    public Coordinate makeRandomShot() {
        Random random = new Random();
        int x, y;
        do {
        	x = random.nextInt(boardWidth);
            y = random.nextInt(boardHeight);
        } while (board[x][y] != 0) ;

        return new Coordinate(x, y);
    }
    
    public List<Coordinate> makeShotNeighbors(Coordinate shot) {
        Set<Coordinate> neighborCells = new HashSet<>();
        int x = shot.getX();
        int y = shot.getY();
        neighborCells.add(new Coordinate(x-1, y));
        neighborCells.add(new Coordinate(x+1, y));
        neighborCells.add(new Coordinate(x, y-1));
        neighborCells.add(new Coordinate(x, y+1));

        // Tìm kiếm tất cả các ô hàng xóm chưa bị bắn
        List<Coordinate> unshotNeighbors = new ArrayList<>();
        for (Coordinate neighbor : neighborCells) {
            if (this.isValidForShot(neighbor)) {
                unshotNeighbors.add(neighbor);
            }
        }
        return unshotNeighbors;
    }
    
    public List<Coordinate> makeShotNeighbors() {
        Set<Coordinate> neighborCells = new HashSet<>();
        for (Coordinate shot : hitCoordinateList) {
            int x = shot.getX();
            int y = shot.getY();
            neighborCells.add(new Coordinate(x-1, y));
            neighborCells.add(new Coordinate(x+1, y));
            neighborCells.add(new Coordinate(x, y-1));
            neighborCells.add(new Coordinate(x, y+1));
        }

        // Tìm kiếm tất cả các ô hàng xóm chưa bị bắn
        List<Coordinate> unshotNeighbors = new ArrayList<>();
        for (Coordinate neighbor : neighborCells) {
            if (this.isValidForShot(neighbor)) {
                unshotNeighbors.add(neighbor);
            }
        }
        return unshotNeighbors;
    }
    
    public boolean isValidForShot(Coordinate shot) {
    	try {
    		if(shot.getX() >= boardWidth || shot.getY() >= boardHeight || shot.getX() < 0  || shot.getY() < 0) {
    			return false;
    		}
        	if(board[shot.getX()][shot.getY()] == 0) {
    			return true;
    		} {
    			return false;
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    }
    
    public List<Coordinate> getNeightBourTypeA(List<Coordinate> hitList, boolean vertical) {
    	List<Coordinate> neightBourTypeA = new ArrayList<>();
    	Coordinate min;
    	Coordinate max;
    	Coordinate obj;
		if (vertical) {
			Coordinate minCoordinateRowY = hitList.stream().min(Comparator.comparing(Coordinate::getY))
					.orElseThrow(NoSuchElementException::new);
			Coordinate maxCoordinateRowY = hitList.stream().max(Comparator.comparing(Coordinate::getY))
					.orElseThrow(NoSuchElementException::new);

			int minRowY = minCoordinateRowY.getY();
			int maxRowY = maxCoordinateRowY.getY();
			int colX = minCoordinateRowY.getX();
			// validate
			obj = new Coordinate(colX, minRowY - 1);
			if (isValidForShot(obj)) {
				neightBourTypeA.add(obj);
			}
			obj = new Coordinate(colX, maxRowY + 1);
			if (isValidForShot(obj)) {
				neightBourTypeA.add(obj);
			}
		} else {
			Coordinate minCoordinateColX = hitList.stream().min(Comparator.comparing(Coordinate::getX))
					.orElseThrow(NoSuchElementException::new);
			Coordinate maxCoordinateColX = hitList.stream().max(Comparator.comparing(Coordinate::getX))
					.orElseThrow(NoSuchElementException::new);

        	int minColX = minCoordinateColX.getX();
        	int maxColX = maxCoordinateColX.getX();
        	int rowY = minCoordinateColX.getY();
        	// validate
			obj = new Coordinate(minColX-1, rowY);
        	if (isValidForShot(obj)) {
        		neightBourTypeA.add(obj);
        	}
        	obj = new Coordinate(maxColX+1,rowY);
        	if (isValidForShot(obj)) {
        		neightBourTypeA.add(obj);
        	}
		}    	
    	return neightBourTypeA;
    }

    public static Coordinate findFourthCoordinate(Coordinate first, Coordinate second, Coordinate third) {
        int x4, y4;
        if (first.getX() == second.getX()) {
            x4 = third.getX();
            y4 = first.getY() + third.getY() - second.getY();
        } else {
            x4 = first.getX() + third.getX() - second.getX();
            y4 = third.getY();
        }
        Coordinate fourth = new Coordinate(x4, y4);
        return fourth;
    }
    
//    public static Coordinate findFourthCoordinate(Coordinate first, Coordinate second, Coordinate third) {
//        // Tính trung bình cộng của các tọa độ
//        int xAvg = (first.getX() + second.getX() + third.getX()) / 3;
//        int yAvg = (first.getY() + second.getY() + third.getY()) / 3;
//
//        // Phản xạ tọa độ qua đường chéo của hình vuông
//        int x4 = 2 * xAvg - third.getX();
//        int y4 = 2 * yAvg - third.getY();
//
//        Coordinate fourth = new Coordinate(x4, y4);
//        return fourth;
//    }
    
    public static void main(String[] args) {
        Coordinate A = new Coordinate(8, 4);
        Coordinate B = new Coordinate(8, 5);
        Coordinate C = new Coordinate(9, 5);

        Coordinate D = findFourthCoordinate(A, B, C);
        System.out.println("D: x=" + D.getX() );
        System.out.println("D: y=" + D.getY() );
    }
    
    public static Coordinate findSquareCorner(List<Coordinate> coordinates) {
        // Tính độ dài đường chéo chính của hình vuông
        double diagonalLength = Math.sqrt(2) * getDistance(coordinates.get(0), coordinates.get(1));

        // Tìm tọa độ của góc của hình vuông
        Coordinate corner = coordinates.get(0);
        for (Coordinate coordinate : coordinates) {
            if (coordinate.getX() + coordinate.getY() > corner.getX() + corner.getY()) {
                corner = coordinate;
            }
        }
        return corner;
    }
    
    public static double getDistance(Coordinate a, Coordinate b) {
        int xDiff = b.getX() - a.getX();
        int yDiff = b.getY() - a.getY();
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }
    
    public List<Coordinate> removeNonCollinearCoordinates(List<Coordinate> fourCoordinate) {
        if (fourCoordinate.size() < 4) {
            return fourCoordinate;
        }
        List<Coordinate> coordinates =  new ArrayList(fourCoordinate);
        Coordinate first = coordinates.get(0);
        double slope = (double) (coordinates.get(1).getY() - first.getY()) / (double) (coordinates.get(1).getX() - first.getX());
        
        for (int i = 2; i < coordinates.size(); i++) {
            Coordinate current = coordinates.get(i);
            double currentSlope = (double) (current.getY() - first.getY()) / (double) (current.getX() - first.getX());
            
            if (currentSlope != slope) {
                coordinates.remove(current);
            }
        }
        
        return coordinates;
    }
    
//    public static void main(String[] args) {
//        List<Coordinate> coordinates = List.of(
//                new Coordinate(5, 4),
//                new Coordinate(6, 4),
//                new Coordinate(6, 3)
//            );
//        //System.out.println( JsonUtil.objectToJson(findSquareCorner(coordinates)));
//    }
    
}
