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
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

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

	public int timeOut = 2000;
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
		this.boardEnemyScore = new int[width][height];

		conerCodinates = List.of(
	    		new Coordinate(0, 0),
	    		new Coordinate(this.boardWidth-1, 0),
	    		new Coordinate(this.boardWidth-1, this.boardHeight-1),
	    		new Coordinate(0, this.boardHeight-1)
	    		);
	}

	// implement 
    public List<Coordinate> hitCoordinateList = new ArrayList<>();
    public List<Coordinate> hitListTemp = new ArrayList<>();
    
    public Coordinate previousHit;
    public int typeCheck = 0 ; // 0: random , 1:neigh bour, 2: typeA(DD,CA,BB), 3: type C(OR), 4: type B(CA), >5 other 
//    private Boolean vertical = null;
    public Map<String, Integer> shipEnemyMap = new HashMap<>();
	public int shipRemainCount;

	private int [][] boardEnemyScore ;
    
    public List<Coordinate> coordinatesShotted = new ArrayList<>();
    boolean flagGetMaxShot = false;

    public void resetCalculator() {
    	typeCheck = 0;
    	previousHit = null;
    }

	private int[][] calculateProbailityTask()  {
//		CalculateProbabilityTask task = new CalculateProbabilityTask(this.boardWidth, this.timeOut, this.boardHeight,
//				this.coordinatesShotted, this.hitCoordinateList, this.shipEnemyMap);

//		ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
//		Future<?> future = executor.submit(task);
//		Runnable cancelTask = () -> future.cancel(true);
//		executor.schedule(cancelTask, this.timeOut, TimeUnit.MILLISECONDS);
//		executor.shutdown();
//		Thread.sleep(this.timeOut);.
//		this.boardEnemy = task.run();

		// other way
		int[][] boardEnemyScore = new int[this.boardWidth][this.boardHeight];
		for (int y = 0; y < this.boardHeight; y++) {
			for (int x = 0; x < this.boardWidth; x++) {
				this.boardEnemyScore[x][y] = this.getScoreCoordinate(x, y, this.shipEnemyMap);
			}
			// System.out.println();
		}

		return boardEnemyScore;
	}
    
	public List<int[]> getShotsTurnResult() {
		List<int[]> result = new ArrayList<>();
		flagGetMaxShot = false;
		
		// main
		List<Coordinate> showTurns = new ArrayList<>();
		
		 /* main */
		showTurns = this.getshotsTurn();
		// remove duplicate value if exist
		// showTurns = showTurns.stream().distinct().collect(Collectors.toList());

		if(CollectionUtils.isEmpty(showTurns)) {
			// return random shot
			List<Coordinate> hitlistAll = new ArrayList<>();
			hitlistAll.addAll(this.hitCoordinateList);
			hitlistAll.addAll(this.hitListTemp);
			
			if(hitlistAll.size() > 0) {
				showTurns = this.makeShotNeighbors(hitlistAll);
			} else {
				Coordinate coordinate = makeSmartRandomShot();
				result.add(new int[] { coordinate.getX(), coordinate.getY() });
				return result;
			}
		} 

		if (flagGetMaxShot || maxShots > 1) {
			for (Coordinate coordinate : showTurns) {
				result.add(new int[] { coordinate.getX(), coordinate.getY() });
				if (--maxShots <= 0) {
					break;
				}
			}
			if (maxShots > 0) { // maxShot still exist count for shot
				Coordinate randomShot;
				while (maxShots-- > 0) {
					int tryCount = 20;
					do {
						if (tryCount-- > 0) {
							randomShot = makeSmartRandomShot(); // makeSmartRandomShot()();
						} else {
							randomShot = makeRandomShot(); // makeSmartRandomShot()();
						}
					} while (showTurns.contains(randomShot));
					showTurns.add(randomShot);
					result.add(new int[] { randomShot.getX(), randomShot.getY() });
				}
			}
			return result;
		}
		
		
		
		// calculator
		List<Coordinate> cordinates = showTurns;
		for (Coordinate coordinate : cordinates) {
			coordinate.setScore(this.getScoreCoordinate(coordinate.getX(), coordinate.getY(), this.shipEnemyMap));
		}

		if (CollectionUtils.isNotEmpty(cordinates)) {
			cordinates.sort((o1, o2) -> o2.getScore() - o1.getScore());
			Coordinate maxScrore = cordinates.stream().max(Comparator.comparing(Coordinate::getScore))
					.orElseThrow(NoSuchElementException::new);
			if (maxScrore != null) {
				this.board[maxScrore.getX()][maxScrore.getY()] = 1;
				result.add(new int[] { maxScrore.getX(), maxScrore.getY() });
				return result;
			}
		}
		
		// other else
		Coordinate randomShot = makeSmartRandomShot();
		result.add(new int[] { randomShot.getX(), randomShot.getY() });
		return result;
	}
	

	public List<Coordinate> getshotsTurn() {
		if (hitCoordinateList.size() == 0) {
			typeCheck = 0;
			// Random shot
			List<Coordinate> shotsTurn = new ArrayList<>();
			flagGetMaxShot = true;
			shotsTurn.add(this.makeSmartRandomShot());
			return shotsTurn;
		} else if (hitCoordinateList.size() == 1) {
			typeCheck = 1;
			return makeShotNeighbors(this.hitCoordinateList);
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
				// flagGetMaxShot = true;
				return neightBour2point;
			} else {
				// Shot neightBour previousHit
				typeCheck = 5;
				return makeShotNeighbors(this.hitCoordinateList);
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
				if(shipEnemyMap.containsKey(Ship.SHIP_OR)) {
					// 3 điểm không thẳng hàng -> tìm điểm góc vuông
					Coordinate coordinateFourth = findFourthCoordinate(hitCoordinateList);
					// Check valid coordinateFourth
					if (isValidForShot(coordinateFourth)) {
						neightBour3point.add(coordinateFourth);
						return neightBour3point;
					}
				}
				// EMPTY neightBour3point
				Coordinate coordinateCV = findSquareCorner(hitCoordinateList);
	        	int colX = coordinateCV.getX();
	        	int rowY = coordinateCV.getY();
				List<Coordinate> shipCvVetical = List.of (
						new Coordinate(colX, rowY ),
						new Coordinate(colX+1, rowY-1 ),
						new Coordinate(colX+1, rowY ),
						new Coordinate(colX+1, rowY+1 ),
						new Coordinate(colX+1, rowY+2 )
				);
				List<Coordinate> shipCvHor = List.of (
						new Coordinate(colX,  rowY ), // 1
						new Coordinate(colX-1,rowY ), // 2
						new Coordinate(colX+1,rowY ), // 3
						new Coordinate(colX+2,rowY ), // 4
						new Coordinate(colX,rowY-1 ) // 5
				);
				if(shipCvVetical.containsAll(this.hitCoordinateList)) {
					// shipCvVetical.removeAll(this.hitCoordinateList);
					// this.flagGetMaxShot = true;
					for (Coordinate coordinate : shipCvVetical) {
						if(!this.hitCoordinateList.contains(coordinate) && isValidForShot(coordinate)) {
							neightBour3point.add(coordinate);
						}
					}
				} else if (shipCvHor.containsAll(this.hitCoordinateList)) {
					// this.flagGetMaxShot = true;
					for (Coordinate coordinate : shipCvHor) {
						if(!this.hitCoordinateList.contains(coordinate) && isValidForShot(coordinate)) {
							neightBour3point.add(coordinate);
						}
					}
				}
			}

			if (CollectionUtils.isNotEmpty(neightBour3point)) {
				return neightBour3point;
			} else {
				// Shot neightBour previousHit
				typeCheck = 5;
				return makeShotNeighbors(this.hitCoordinateList);
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
				typeCheck = 5;
				return makeShotNeighbors(this.hitCoordinateList);
			}
		} else {
			typeCheck = 5;
			// Shot neightBour previousHit
			return makeShotNeighbors(this.hitCoordinateList);
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

        // notify shotted
		this.board[x][y] = 1;
        return new Coordinate(x, y);
    }
    
    
	public int thresholdShotConner = 1; // max 60
	public int thresholdShotBorder = 10; // max 60
	public int maxThresholdShot = 100;
	public int maxShotNoCheckDD = 70;

    private List<Coordinate> conerCodinates = List.of(
    		new Coordinate(0, 0),
    		new Coordinate(this.boardWidth-1, 0),
    		new Coordinate(this.boardWidth-1, this.boardHeight-1),
    		new Coordinate(0, this.boardHeight-1)
    		);
    
    public List<Coordinate> priorityShotsList = new ArrayList<>();
    public boolean checkPriorityFlag = true;
    
	public Coordinate makeSmartRandomShot() {
    	if (CollectionUtils.isNotEmpty(priorityShotsList)) {
    		Coordinate prioritycoordinate = priorityShotsList.get(0);
    		priorityShotsList.remove(prioritycoordinate);
    		if(isValidForShot(prioritycoordinate)) {
				this.board[prioritycoordinate.getX()][prioritycoordinate.getY()] = 1;
        		return  prioritycoordinate;
    		}
    	}

		List<Coordinate> cordinates;
		Random rand = new Random();
		int thresholdCheck = rand.nextInt(maxThresholdShot); // random number
		
		if (thresholdCheck < thresholdShotConner) {
			cordinates = new ArrayList<>();
			for (Coordinate coordinate : conerCodinates) {
				if(isValidForShot(coordinate)) {
					coordinate.setScore(this.getScoreCoordinate(coordinate.getX(), coordinate.getY(), this.shipEnemyMap));
					cordinates.add(coordinate);
				}
			}
			cordinates = cordinates.stream().filter(o -> o.getScore() > 0).collect(Collectors.toList()); 
			if (CollectionUtils.isNotEmpty(cordinates)) {
				cordinates.sort((o1, o2) -> o2.getScore() - o1.getScore());
				Coordinate maxScrore = cordinates.stream().max(Comparator.comparing(Coordinate::getScore))
						.orElseThrow(NoSuchElementException::new);
				if (maxScrore != null) {
					this.board[maxScrore.getX()][maxScrore.getY()] = 1;
					return maxScrore;
				}
			}
		}

		// check shot border
		if(thresholdCheck < thresholdShotBorder || (this.myShotNo > maxShotNoCheckDD &&  shipEnemyMap.containsKey(Ship.SHIP_DD))) {
			cordinates = new ArrayList<>();
			cordinates = this.getCoordinatesBorder();
			for (Coordinate coordinate : cordinates) {
				coordinate.setScore(this.getScoreCoordinate(coordinate.getX(), coordinate.getY(), this.shipEnemyMap));
			}

			cordinates = cordinates.stream().filter(o -> o.getScore() > 0).collect(Collectors.toList()); 
			if (CollectionUtils.isNotEmpty(cordinates)) {
				cordinates.sort((o1, o2) -> o2.getScore() - o1.getScore());
				Coordinate maxScrore = cordinates.stream().max(Comparator.comparing(Coordinate::getScore))
						.orElseThrow(NoSuchElementException::new);
				if (maxScrore != null) {
					this.board[maxScrore.getX()][maxScrore.getY()] = 1;
					return maxScrore;
				}
			}
		}
		
		// ELSE MAIN
		// calculator random board
		cordinates = new ArrayList<>();
		Coordinate coordinate;
		for (int y = 0; y < this.boardHeight; y++) {
			for (int x = 0; x < this.boardWidth; x++) {
				coordinate = new Coordinate(x, y);
				if(this.isValidForShot(coordinate)) {
					coordinate.setScore(this.getScoreCoordinate(x, y, this.shipEnemyMap));
					cordinates.add(coordinate);
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(cordinates)) {
			cordinates.sort((o1, o2) -> o2.getScore() - o1.getScore());
			Coordinate maxScrore = cordinates.stream().max(Comparator.comparing(Coordinate::getScore))
					.orElseThrow(NoSuchElementException::new);
			if (maxScrore != null) {
				this.board[maxScrore.getX()][maxScrore.getY()] = 1;
				return maxScrore;
			}
		}

		return makeRandomShot();
	}
    
    private List<Coordinate> getCoordinatesBorder() {
    	List<Coordinate> result= new ArrayList<>();
    	Coordinate coordinate;
    	// add y
    	for(int y=0; y<this.boardWidth; y++) {
    		coordinate = new Coordinate(0, y);
			if (isValidForShot(coordinate)) {
				result.add(coordinate);
			}
			coordinate = new Coordinate(this.boardHeight-1, y);
			if (isValidForShot(coordinate)) {
				result.add(coordinate);
			}
    	}
    	// add x
    	for(int x=1; x<this.boardHeight-1; x++) {
    		coordinate = new Coordinate(x, 0);
			if (isValidForShot(coordinate)) {
				result.add(coordinate);
			}
			coordinate = new Coordinate(x, this.boardWidth-1);
			if (isValidForShot(coordinate)) {
				result.add(coordinate);
			}
    	}

		return result;
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
    
    public List<Coordinate> getNeighbors(Coordinate shot) {
        Set<Coordinate> neighborCells = new HashSet<>();
        int x = shot.getX();
        int y = shot.getY();
        neighborCells.add(new Coordinate(x-1, y));
        neighborCells.add(new Coordinate(x+1, y));
        neighborCells.add(new Coordinate(x, y-1));
        neighborCells.add(new Coordinate(x, y+1));

        // Tìm kiếm tất cả các ô hàng xóm chưa bị bắn
        List<Coordinate> neighborCellValids = new ArrayList<>();
        for (Coordinate neighbor : neighborCells) {
            if (this.isValidCooordinate(neighbor)) {
                neighborCellValids.add(neighbor);
            }
        }
        return neighborCellValids;
    }
    
    public List<Coordinate> makeShotNeighbors(List<Coordinate> hitCoordinateList) {
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
    
    public boolean isValidCooordinate(Coordinate shot) {
    	try {
    		if(shot.getX() >= boardWidth || shot.getY() >= boardHeight || shot.getX() < 0  || shot.getY() < 0) {
    			return false;
    		}
    		return true;
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
			objMax = new Coordinate(colX, maxRowY + 1);
			if (isValidForShot(objMax)) {
				neightBourTypeA.add(objMax);
			}
			objMin = new Coordinate(colX, minRowY - 1);
			if (isValidForShot(objMin)) {
				neightBourTypeA.add(objMin);
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

		if (typeCheck == 2 && CollectionUtils.isEmpty(neightBourTypeA)) {
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
				obj = new Coordinate(objMax2.getX(), objMax2.getY() - 1);
				if (isValidForShot(obj)) {
					neightBourTypeA.add(obj);
				}
				obj = new Coordinate(objMax2.getX(), objMax2.getY() + 1);
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
    
    
    public static void main(String args[]) {
    	BotPlayer botPlayer =  new BotPlayer(20,8, new ArrayList<>());

    	botPlayer.shipEnemyMap = new HashMap<>();
    	botPlayer.shipEnemyMap.put("DD", 1);
    	botPlayer.shipEnemyMap.put("CA", 1);
    	botPlayer.shipEnemyMap.put("OR", 1);
    	botPlayer.shipEnemyMap.put("BB", 1);
    	botPlayer.shipEnemyMap.put("CV", 1);
    	
    	botPlayer.coordinatesShotted.add(new Coordinate(0, 0));

    	int[][] board =	new int[botPlayer.boardWidth][botPlayer.boardHeight];
      	StringBuffer sb = new StringBuffer();
    	sb.append("==== Title :" + "");
    	sb.append("\n");
    	
        for (int y = 0; y < botPlayer.boardHeight; y++) {
        	for (int x = 0; x < botPlayer.boardWidth; x++) {
        		board[x][y] = botPlayer.getScoreCoordinate(x, y, botPlayer.shipEnemyMap);
            	sb.append(board[x][y] + " (" + ")\t");
            }
            sb.append("\n");
            // System.out.println();
        }
        System.out.println(sb.toString());
    }
    
	public boolean isCellNotInShotted(Coordinate shot) {
		try {
			if (shot.getX() >= boardWidth || shot.getY() >= boardHeight || shot.getX() < 0 || shot.getY() < 0) {
				return false;
			}
			if (!this.coordinatesShotted.contains(shot)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
    
    private int getScoreCoordinate(int x, int y, Map<String, Integer> shipEnemyMap) {
    	int score = 0;
    	if(shipEnemyMap.containsKey(Ship.SHIP_DD)) {
    		score+= getScoreShipDD(x, y, shipEnemyMap.get(Ship.SHIP_DD));
    	}
    	if(shipEnemyMap.containsKey(Ship.SHIP_CA)) {
    		score+= getScoreShipCA(x, y, shipEnemyMap.get(Ship.SHIP_CA));
    	}
    	if(shipEnemyMap.containsKey(Ship.SHIP_OR)) {
    		score+= getScoreShipOR(x, y, shipEnemyMap.get(Ship.SHIP_OR));
    	}
    	if(shipEnemyMap.containsKey(Ship.SHIP_BB)) {
    		score+= getScoreShipBB(x, y, shipEnemyMap.get(Ship.SHIP_BB));
    	}
    	if(shipEnemyMap.containsKey(Ship.SHIP_CV)) {
    		score+= getScoreShipCV(x, y, shipEnemyMap.get(Ship.SHIP_CV));
    	}
		return score;
    }
    
    public int getScoreShipDD(int x, int y, int coutShip) {
		int score = 0;
		List<List<Coordinate>> listShip = new ArrayList<>();
		listShip.add(List.of(new Coordinate(x, y), new Coordinate(x + 1, y)));
		listShip.add(List.of(new Coordinate(x - 1, y), new Coordinate(x, y)));
		listShip.add(List.of(new Coordinate(x, y), new Coordinate(x, y + 1)));
		listShip.add(List.of(new Coordinate(x, y - 1), new Coordinate(x, y)));
		
		for (List<Coordinate> list : listShip) {
			if(!list.stream().anyMatch(coordinate -> !isCellNotInShotted(coordinate))) {
				score+=coutShip;
			}
		}
    	return score;
    }
    
    public int getScoreShipCA(int x, int y, int coutShip) {
		int score = 0;
		List<List<Coordinate>> listShip = new ArrayList<>();
		listShip.add(List.of(new Coordinate(x, y), new Coordinate(x + 1, y), new Coordinate(x + 2, y)));
		listShip.add(List.of(new Coordinate(x - 1, y), new Coordinate(x, y), new Coordinate(x + 1, y)));
		listShip.add(List.of(new Coordinate(x - 2, y), new Coordinate(x - 1, y), new Coordinate(x, y)));
		listShip.add(List.of(new Coordinate(x, y), new Coordinate(x, y + 1), new Coordinate(x, y + 2)));
		listShip.add(List.of(new Coordinate(x, y - 1), new Coordinate(x, y), new Coordinate(x, y + 1)));
		listShip.add(List.of(new Coordinate(x, y - 2), new Coordinate(x, y - 1), new Coordinate(x, y)));
		for (List<Coordinate> list : listShip) {
			if(!list.stream().anyMatch(coordinate -> !isCellNotInShotted(coordinate))) {
				score+=coutShip;
			}
		}
    	return score;
    }
    
    public int getScoreShipBB(int x, int y, int coutShip) {
		int score = 0;
		List<List<Coordinate>> listShip = new ArrayList<>();
		listShip.add(List.of(new Coordinate(x, y), new Coordinate(x+1, y), new Coordinate(x+2, y), new Coordinate(x+3, y)));
		listShip.add(List.of(new Coordinate(x-1, y), new Coordinate(x, y), new Coordinate(x+1, y), new Coordinate(x+2, y)));
		listShip.add(List.of(new Coordinate(x-2, y), new Coordinate(x-1, y), new Coordinate(x, y), new Coordinate(x+1, y)));
		listShip.add(List.of(new Coordinate(x-3, y), new Coordinate(x-2, y), new Coordinate(x-1, y), new Coordinate(x, y)));
		listShip.add(List.of(new Coordinate(x, y), new Coordinate(x, y+1), new Coordinate(x, y+2), new Coordinate(x, y+3)));
		listShip.add(List.of(new Coordinate(x, y-1), new Coordinate(x, y), new Coordinate(x, y+1), new Coordinate(x, y+2)));
		listShip.add(List.of(new Coordinate(x, y-2), new Coordinate(x, y-1), new Coordinate(x, y), new Coordinate(x, y+1)));
		listShip.add(List.of(new Coordinate(x, y-3), new Coordinate(x, y-2), new Coordinate(x, y-1), new Coordinate(x, y)));
		for (List<Coordinate> list : listShip) {
			if(!list.stream().anyMatch(coordinate -> !isCellNotInShotted(coordinate))) {
				score+=coutShip;
			}
		}
    	return score;
    }
    
    public int getScoreShipCV(int x, int y, int coutShip) {
		int score = 0;
		List<List<Coordinate>> listShip = new ArrayList<>();
		listShip.add(List.of(new Coordinate(x, y), new Coordinate(x+1, y), new Coordinate(x+2, y), new Coordinate(x+3, y), new Coordinate(x+1, y-1))); 
		listShip.add(List.of(new Coordinate(x-1, y), new Coordinate(x, y), new Coordinate(x+1, y), new Coordinate(x+2, y), new Coordinate(x, y-1))  );
		listShip.add(List.of(new Coordinate(x-2, y), new Coordinate(x-1, y), new Coordinate(x, y), new Coordinate(x+1, y), new Coordinate(x-1, y-1)));
		listShip.add(List.of(new Coordinate(x-3, y), new Coordinate(x-2, y), new Coordinate(x-1, y), new Coordinate(x, y), new Coordinate(x-2, y-1)));
		listShip.add(List.of(new Coordinate(x, y), new Coordinate(x, y+1), new Coordinate(x, y+2), new Coordinate(x, y+3), new Coordinate(x-1, y+1)));
		listShip.add(List.of(new Coordinate(x, y-1), new Coordinate(x, y), new Coordinate(x, y+1), new Coordinate(x, y+2), new Coordinate(x-1, y))  );
		listShip.add(List.of(new Coordinate(x, y-2), new Coordinate(x, y-1), new Coordinate(x, y), new Coordinate(x, y+1), new Coordinate(x-1, y-1)));
		listShip.add(List.of(new Coordinate(x, y-3), new Coordinate(x, y-2), new Coordinate(x, y-1), new Coordinate(x, y), new Coordinate(x-1, y-2)));
		for (List<Coordinate> list : listShip) {
			if(!list.stream().anyMatch(coordinate -> !isCellNotInShotted(coordinate))) {
				score+=coutShip;
			}
		}
		return score;	
    }
   
    public int getScoreShipOR(int x, int y, int coutShip) {
		int score = 0;
		List<List<Coordinate>> listShip = new ArrayList<>();
		listShip.add(List.of(new Coordinate(x, y), new Coordinate(x, y+1), new Coordinate(x+1, y+1), new Coordinate(x+1, y+1)));
		listShip.add(List.of(new Coordinate(x, y), new Coordinate(x-1, y), new Coordinate(x, y+1), new Coordinate(x-1, y-1)));
		listShip.add(List.of(new Coordinate(x, y), new Coordinate(x-1, y), new Coordinate(x-1, y-1), new Coordinate(x, y-1)));
		listShip.add(List.of(new Coordinate(x, y), new Coordinate(x+1, y), new Coordinate(x, y-1), new Coordinate(x-1, y-1)));
		for (List<Coordinate> list : listShip) {
			if(!list.stream().anyMatch(coordinate -> !isCellNotInShotted(coordinate))) {
				score+=coutShip;
			}
		}
    	return score;
    }
}
