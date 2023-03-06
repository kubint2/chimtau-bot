package ati.player.rest.api.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import ati.player.rest.api.controller.CalculateProbabilityTask;
import ati.player.rest.api.entity.Coordinate;
import ati.player.rest.api.entity.ShipData;
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
//	public List<Coordinate> neighborCoordinateList;
	
	public String enemyPlayId;
	public int[][] enemyShotNo2d;
	public int enemyShotNo = 0;
	public char[][] enemyPlaceShipBoard;
	
	public List<ShipData> enemyShipData = new ArrayList(); 
	
	public int[][] myShotNoArr2d;
	public int myShotNo = 0;
	public char[][] myPlaceShipBoard;

	private static final int TIME_OUT = 4000;
	public int timeOut = TIME_OUT;
	public boolean modeEasy = false;

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
			this.shipEnemyMap.put(shipRequest.getType(), shipRequest.getQuantity());
			this.shipRemainCount+=shipRequest.getQuantity();
		}
		
		// init board
		this.board = new int[width][height];
		this.boardEnemy = new int[width][height];

	}

	// implement 
    public List<Coordinate> hitCoordinateList = new ArrayList<>();
    public Coordinate previousHit;
    public int typeCheck = 0 ; // 0: random , 1:neigh bour, 2: typeA(DD,CA,BB), 3: type C(OR), 4: type B(CA), >5 other 
//    private Boolean vertical = null;
    public Map<String, Integer> shipEnemyMap = new HashMap<>();
	public int shipRemainCount;

	private int [][] boardEnemy ;
    public List<Coordinate> priorityShotsList = new ArrayList<>();
    
    public List<Coordinate> coordinatesShotted = new ArrayList<>();
    boolean flagGetMaxShot = false;

    public void resetCalculator() {
    	typeCheck = 0;
    	previousHit = null;
//    	vertical = null;
    	// hitCoordinateList = new ArrayList<>();
    }

	private void calculateProbailityTask() throws InterruptedException {
		CalculateProbabilityTask task = new CalculateProbabilityTask(this.boardWidth,
				this.boardHeight, this.coordinatesShotted, this.hitCoordinateList, this.shipEnemyMap);

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
		Future<?> future = executor.submit(task);
		Runnable cancelTask = () -> future.cancel(true);
		executor.schedule(cancelTask, this.timeOut, TimeUnit.MILLISECONDS);
		executor.shutdown();
		Thread.sleep(this.timeOut);
		
		this.boardEnemy = task.boardEnemy;
	}
    
	public List<int[]> getShotsTurnResult() {
		List<int[]> result = new ArrayList<>();
		flagGetMaxShot = false;
		
		// main
		List<Coordinate> showTurns = new ArrayList<>();
		if (modeEasy) {
			if (hitCoordinateList.size() == 0) {
				showTurns.add(this.makeRandomShot());
			} else {
				showTurns = this.makeShotNeighbors();
			}
			result.add(new int[] { showTurns.get(0).getX(), showTurns.get(0).getY() });
			return result;
		}
		
		 /* main */
		showTurns = this.getshotsTurn();
		// remove duplicate value if exist
		showTurns = showTurns.stream().distinct().collect(Collectors.toList());

		if(CollectionUtils.isEmpty(showTurns)) {
			// return random shot
			Coordinate Coordinate = makeSmartRandomShot();
			result.add(new int[] {Coordinate.getX(), Coordinate.getY()});
		} else if (showTurns.size() ==1) {
			result.add(new int[] {showTurns.get(0).getX(), showTurns.get(0).getY()});
		} else {
			// in case size showTurns >=2
			if(flagGetMaxShot && showTurns.size() == this.maxShots) {
				// add all
				for (Coordinate coordinate : showTurns) {
					result.add(new int[] {coordinate.getX(), coordinate.getY()});
				}
			} else {
				// sort score
				try {
					this.calculateProbailityTask();
					for (Coordinate coordinate : showTurns) {
						coordinate.setScore(boardEnemy[coordinate.getX()][coordinate.getY()]);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				// order
				showTurns.sort((o1, o2) -> o2.getScore() - o1.getScore());
				if (this.maxShots < 2) {
					result.add(new int[] { showTurns.get(0).getX(), showTurns.get(0).getY() });
				} else if (flagGetMaxShot || shipRemainCount <= 2) {
						for (Coordinate coordinate : showTurns) {
							result.add(new int[] {coordinate.getX(), coordinate.getY()});
							if(--maxShots <= 0) {
								break;
							}
						}

						if (maxShots > 0) { // maxShot still exist count for shot
							List<Coordinate> neighborsCoordinate = this.makeShotNeighbors();
							for (Coordinate coordinate : neighborsCoordinate) {
								coordinate.setScore(boardEnemy[coordinate.getX()][coordinate.getY()]);
							}
							// sort 
							neighborsCoordinate.sort((o1, o2) -> o2.getScore() - o1.getScore());
							for (Coordinate coordinate : neighborsCoordinate) {
								if (!showTurns.contains(coordinate)) {
									result.add(new int[] { coordinate.getX(), coordinate.getY() });
									if (--maxShots <= 0) {
										break;
									}
								}
							}
						}
				} else {
					result.add(new int[] {showTurns.get(0).getX(), showTurns.get(0).getY()});
				}
			}
		}
		//
		System.out.println("=== Hit List : " + JsonUtil.objectToJson(hitCoordinateList));
		System.out.println("=== Type Check: " + typeCheck  +"= Shots Output: " + JsonUtil.objectToJson(showTurns));
		return result;
	}
	

	public List<Coordinate> getshotsTurn() {
		if (hitCoordinateList.size() == 0) {
			typeCheck = 0;
			// Random shot
			List<Coordinate> shotsTurn = new ArrayList<>();
			shotsTurn.add(this.makeSmartRandomShot());
			return shotsTurn;
		} else if (hitCoordinateList.size() == 1) {
			typeCheck = 1;
			return makeShotNeighbors();
		} else if (hitCoordinateList.size() == 2) {
			typeCheck = 2;

			// Shot vertical or Not
			Coordinate first = hitCoordinateList.get(0);
			Coordinate second = hitCoordinateList.get(1);

			List<Coordinate> neightBour2point = new ArrayList<>();
			// check vertical
			boolean isVetical = (first.getX() == second.getX());
//			if (shipEnemyMap.containsKey(Ship.SHIP_CA) || shipEnemyMap.containsKey(Ship.SHIP_BB)
//					|| shipEnemyMap.containsKey(Ship.SHIP_CV)) {
//				neightBour2point = getNeightBourTypeA(hitCoordinateList, isVetical);
//			}
			neightBour2point = getNeightBourTypeA(hitCoordinateList, isVetical);

			if (CollectionUtils.isNotEmpty(neightBour2point)) {
//				if (shipEnemyMap.containsKey(Ship.SHIP_BB)
//						|| shipEnemyMap.containsKey(Ship.SHIP_CV)) {
//					flagGetMaxShot = true;
//				}
				flagGetMaxShot = true;
				return neightBour2point;
			} else {
				// Shot neightBour previousHit
				return makeShotNeighbors();
			}

		} else if (hitCoordinateList.size() == 3) {
			typeCheck = 3;
			
			Coordinate first = hitCoordinateList.get(0);
			Coordinate second = hitCoordinateList.get(1);
			Coordinate third = hitCoordinateList.get(2);

			List<Coordinate> neightBour3point = new ArrayList<>();
			
			Boolean isVetical;
			if (first.getY() == second.getY() && first.getY() == third.getY()) {
				isVetical = false;
			} else if (first.getX() == second.getX() && first.getX() == third.getX()) {
				isVetical = true;
			} else {
				isVetical = null;
			}
			
			if (isVetical != null)  {
				neightBour3point = getNeightBourTypeA(hitCoordinateList, isVetical);
			} else {
				// 3 điểm không thẳng hàng -> tìm điểm góc vuông
				Coordinate coordinateFourth = findFourthCoordinate(hitCoordinateList);
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
			typeCheck = 4;

			Coordinate first = hitCoordinateList.get(0);
			Coordinate second = hitCoordinateList.get(1);
			Coordinate third = hitCoordinateList.get(2);
			Coordinate four = hitCoordinateList.get(3);
			List<Coordinate> neightBour4point = new ArrayList<>();
			
			if (this.shipEnemyMap.containsKey(Ship.SHIP_CV)) {
				Coordinate remainPoin;
				Boolean isVetical;
				if (first.getY() == second.getY() && first.getY() == third.getY() && first.getY() == four.getY()) {
					isVetical = false;
				} else if (first.getX() == second.getX() && first.getX() == third.getX()
						&& first.getX() == four.getX()) {
					isVetical = true;
				} else {
					isVetical = null;
				}

				if (isVetical != null) {
					if (isVetical) {
						Coordinate minCoordinateRowY = hitCoordinateList.stream()
								.min(Comparator.comparing(Coordinate::getY)).orElseThrow(NoSuchElementException::new);
						remainPoin = new Coordinate(minCoordinateRowY.getX() - 1, minCoordinateRowY.getY() + 1);
						if (isValidForShot(remainPoin))
							neightBour4point.add(remainPoin);
					} else {
						Coordinate minCoordinateColX = hitCoordinateList.stream()
								.min(Comparator.comparing(Coordinate::getX)).orElseThrow(NoSuchElementException::new);
						remainPoin = new Coordinate(minCoordinateColX.getX() + 1, minCoordinateColX.getY() - 1);
						if (isValidForShot(remainPoin))
							neightBour4point.add(remainPoin);
					}
				} else {
					neightBour4point = findRemainCoordinateShipCV(this.hitCoordinateList);
				}
			}

			if (CollectionUtils.isNotEmpty(neightBour4point)) {
				return neightBour4point;
			} else {
				// Shot neightBour previousHit
				return makeShotNeighbors();
			}
		} else {
			typeCheck = 5;
			// Shot neightBour previousHit
			return makeShotNeighbors();
		}
		// return shotsTurn;
	}
    
    public Coordinate makeRandomShot() {
    	int x, y;
        Random random = new Random();
        do {
        	x = random.nextInt(boardWidth);
            y = random.nextInt(boardHeight);
        } while (board[x][y] != 0) ;

        return new Coordinate(x, y);
    }
    
    public Coordinate makeSmartRandomShot() {
    	Coordinate coordinate = null;
    	if (CollectionUtils.isNotEmpty(priorityShotsList)) {
    		coordinate = priorityShotsList.get(0);
    		priorityShotsList.remove(coordinate);
    	}
    	if(coordinate != null && board[coordinate.getX()][coordinate.getY()] == 0) {
    		return coordinate;
    	}

		// sort score
		try {
			this.calculateProbailityTask();
			List<Coordinate> coordinates = new ArrayList<>();
	        for (int y = 0; y < this.boardHeight; y++) {
	            for (int x = 0; x < this.boardWidth; x++) {
	            	if(board[x][y] == 0) {
	            		coordinates.add(new Coordinate(x, y, boardEnemy[x][y]));
	            	}
	                //System.out.print(boardEnemy[x][y] + "  ");
	            }
	        }
			coordinate = coordinates.stream().max(Comparator.comparing(Coordinate::getScore))
					.orElseThrow(NoSuchElementException::new);
			if (coordinate != null && isValidForShot(coordinate)) {
				System.out.println("makeSmartRandomShot.coordinates.size: " + coordinates.size());
				System.out.println("makeSmartRandomShot.MaxScore: " + JsonUtil.objectToJson(coordinate));
				return coordinate;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return makeRandomShot();
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
    
	public List<Coordinate> getNeightBourTypeA(List<Coordinate> hitList, boolean isVetical) {
		List<Coordinate> neightBourTypeA = new ArrayList<>();
		Coordinate objMin;
		Coordinate objMax;
		if (isVetical) {
			int minRowY = hitList.stream().min(Comparator.comparing(Coordinate::getY))
					.orElseThrow(NoSuchElementException::new).getY();
			int maxRowY = hitList.stream().max(Comparator.comparing(Coordinate::getY))
					.orElseThrow(NoSuchElementException::new).getY();

			int colX = hitList.get(0).getX();
			// validate
			objMin = new Coordinate(colX, minRowY - 1);
			if (isValidForShot(objMin)) {
				neightBourTypeA.add(objMin);
			}
			objMax = new Coordinate(colX, maxRowY + 1);
			if (isValidForShot(objMax)) {
				neightBourTypeA.add(objMax);
			}
		} else {
			int minColX = hitList.stream().min(Comparator.comparing(Coordinate::getX))
					.orElseThrow(NoSuchElementException::new).getX();
			int maxColX = hitList.stream().max(Comparator.comparing(Coordinate::getX))
					.orElseThrow(NoSuchElementException::new).getX();

			int rowY = hitList.get(0).getY();
			// validate
			objMin = new Coordinate(minColX - 1, rowY);
			boolean containShipCABBCV = true;
			if (containShipCABBCV && isValidForShot(objMin)) {
				neightBourTypeA.add(objMin);
			}
			objMax = new Coordinate(maxColX + 1, rowY);
			if (containShipCABBCV && isValidForShot(objMax)) {
				neightBourTypeA.add(objMax);
			}
		}

		if (hitList.size() == 2) {
			this.flagGetMaxShot = true;
			Coordinate obj;
			if (isVetical) {
				Coordinate objMax2 = hitList.stream().max(Comparator.comparing(Coordinate::getY))
						.orElseThrow(NoSuchElementException::new);
				obj = new Coordinate(objMax2.getX() - 1, objMax2.getY());
				if (isValidForShot(obj)) {
					neightBourTypeA.add(obj);
				}
				obj = new Coordinate(objMax2.getX() + 1, objMax2.getY());
				if (isValidForShot(obj)) {
					neightBourTypeA.add(obj);
				}
			} else {
				Coordinate objMax2 = hitList.stream().max(Comparator.comparing(Coordinate::getX))
						.orElseThrow(NoSuchElementException::new);
				obj = new Coordinate(objMax2.getX(), objMax2.getY() + 1);
				if (isValidForShot(obj)) {
					neightBourTypeA.add(obj);
				}
				obj = new Coordinate(objMax2.getX(), objMax2.getY() - 1);
				if (isValidForShot(obj)) {
					neightBourTypeA.add(obj);
				}
			}
		}
		return neightBourTypeA;
	}
    
    public static Coordinate findFourthCoordinate(List<Coordinate> hitList) {
        if(hitList.size() != 3) {
        	return null;
        }
        // A B
        // C D
		int minX = hitList.stream().min(Comparator.comparing(Coordinate::getX))
				.orElseThrow(NoSuchElementException::new).getX();
		int maxX = hitList.stream().max(Comparator.comparing(Coordinate::getX))
				.orElseThrow(NoSuchElementException::new).getX();
		int minY = hitList.stream().min(Comparator.comparing(Coordinate::getY))
				.orElseThrow(NoSuchElementException::new).getY();
		int maxY = hitList.stream().max(Comparator.comparing(Coordinate::getY))
				.orElseThrow(NoSuchElementException::new).getY();
		Coordinate C = new Coordinate (minX, minY);
		if (!hitList.contains(C)) {
			return C;
		}
		Coordinate B = new Coordinate (maxX, maxY);
		if (!hitList.contains(B)) {
			return B;
		}
		Coordinate A = new Coordinate (minX, maxY);
		if (!hitList.contains(A)) {
			return A;
		}
		Coordinate D = new Coordinate (maxX, minY);
		if (!hitList.contains(D)) {
			return D;
		}
        return null;
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

    public List<Coordinate> findRemainCoordinateShipCV(List<Coordinate> fourCoordinate) {
        if (fourCoordinate.size() < 4) {
            return fourCoordinate;
        }

        List<Coordinate> result = new ArrayList<>();
        Coordinate CoordinateStartV = null;
        Coordinate CoordinateStartH = null;
        int countSameX = 0;
        int countSameY = 0;
        // tim điểm gốc
		for (Coordinate coordinate : fourCoordinate) {
			countSameX = 0;
			countSameY = 0;
			for (Coordinate coordinateCheck : fourCoordinate) {
				// check same X
				if (coordinateCheck.getX() == coordinate.getX()) {
					countSameX++;
				}
				if (coordinateCheck.getY() == coordinate.getY()) {
					countSameY++;
				}
			}
			if (countSameX == 2 && countSameY == 1) {
				CoordinateStartH = coordinate;
				break;
			} else if (countSameY == 2 && countSameX == 1) {
				CoordinateStartV = coordinate;
				break;
			}
		}
		List<Coordinate> coordinatesShipCV = new ArrayList<>();
        if (CoordinateStartV != null) {
        	int colX = CoordinateStartV.getX();
        	int rowY = CoordinateStartV.getY();
			coordinatesShipCV.add(new Coordinate(colX, rowY));
			coordinatesShipCV.add(new Coordinate(colX+1, rowY-1));
			coordinatesShipCV.add(new Coordinate(colX+1, rowY));
			coordinatesShipCV.add(new Coordinate(colX+1, rowY+1));
			coordinatesShipCV.add(new Coordinate(colX+1, rowY+2));
        }
        
        if (CoordinateStartH != null) {
        	int colX = CoordinateStartH.getX();
        	int rowY = CoordinateStartH.getY();
			coordinatesShipCV.add(new Coordinate(colX, rowY));
			coordinatesShipCV.add(new Coordinate(colX-1, rowY+1));
			coordinatesShipCV.add(new Coordinate(colX, rowY+1));
			coordinatesShipCV.add(new Coordinate(colX+1, rowY+1));
			coordinatesShipCV.add(new Coordinate(colX+2, rowY+1));
        }
        
        for (Coordinate coordinate : coordinatesShipCV) {
			if(isValidForShot(coordinate)) result.add(coordinate);
		}
        return result;
    }
    
    
    public static void main(String[] args) {
        List<Coordinate> coordinates = List.of(
                new Coordinate(6, 3),
                new Coordinate(6, 4),
                new Coordinate(5, 4)
            );
//        === Hit List : [{"x":6,"y":3,"score":0},{"x":6,"y":4,"score":0},{"x":5,"y":5,"score":0}]
//        		=== Type Check: 3= Shots Output: [{"x":5,"y":4,"score":119},{"x":7,"y":4,"score":98}]
        
        System.out.println( JsonUtil.objectToJson(findSquareCorner(coordinates)));
    }
    
}
