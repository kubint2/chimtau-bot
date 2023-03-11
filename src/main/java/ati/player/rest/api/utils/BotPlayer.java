package ati.player.rest.api.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import ati.player.rest.api.entity.Coordinate;
import ati.player.rest.api.entity.ShipData;
import ati.player.rest.api.entity.ThresholdConfig;
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

	public String enemyPlayId;
	public int[][] enemyShotNo2d;
	public int enemyShotNo = 0;
	public char[][] enemyPlaceShipBoard;
	
	public List<ShipRequest> requestShipData = new ArrayList(); 
	
	public int[][] myShotNoArr2d;
	public int myShotNo = 0;
	public char[][] myPlaceShipBoard;

	public int timeOut = 2000;
	public boolean modeEasy = false;

	

	public BotPlayer() {
	}

	// implement 
    public List<Coordinate> hitCoordinateList = new ArrayList<>();
    public List<Coordinate> hitListTemp = new ArrayList<>();
    
    public Coordinate previousHit;
    public int typeCheck = 0 ; // 0: random , 1:neighbour, 2: typeA(DD,CA,BB), 3: type C(OR), 4: type B(CA), >5 other 
//    private Boolean vertical = null;
    public Map<String, Integer> shipEnemyMap = new HashMap<>();
	public int shipRemainCount;

	public List<Coordinate> coordinatesShotted = new ArrayList<>();
    boolean flagGetMaxShot = false;

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
		cornerCoordinates =  GameUtil.getCoordinatesConner(width, height);

		// init coordinates border
		borderCoordinates = GameUtil.getCoordinatesBorder(width, height);
		
		// init coordinates board
		boardCoordinates = GameUtil.getCoordinatesBoard(width, height);
		
		requestShipData = ships;
	}

    public void resetCalculator() {
    	typeCheck = 0;
    	previousHit = null;
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

		if (flagGetMaxShot || maxShots > 2) {
			for (Coordinate coordinate : showTurns) {
				result.add(new int[] { coordinate.getX(), coordinate.getY() });
				this.board[coordinate.getX()][coordinate.getY()] = 1;
				if (--maxShots <= 0) {
					break;
				}
			}
			if (maxShots > 0) { // maxShot still exist count for shot
				Coordinate randomShot;
				while (maxShots-- > 0) {
					int tryCount = 30;
					do {
						if (tryCount-- > 0) {
							randomShot = makeSmartRandomShot(); // makeSmartRandomShot()();
						} else {
							randomShot = makeRandomShot(); // makeSmartRandomShot()();
						}
					} while (showTurns.contains(randomShot));
					showTurns.add(randomShot);
					this.board[randomShot.getX()][randomShot.getY()] = 1;
					result.add(new int[] { randomShot.getX(), randomShot.getY() });
				}
			}
			return result;
		}
		

		// calculator
		if (CollectionUtils.isNotEmpty(showTurns)) {
			for (Coordinate coordinate : showTurns) {
				coordinate.setScore(this.getScoreCoordinate(coordinate.getX(), coordinate.getY(), this.shipEnemyMap, false));
			}

			Coordinate maxScore = showTurns.stream().max(Comparator.comparing(Coordinate::getScore))
					.orElseThrow(NoSuchElementException::new);

			if (maxScore != null) {
				result.add(new int[] { maxScore.getX(), maxScore.getY() });
				return result;
			}

			// return index 0
			result.add(new int[] { showTurns.get(0).getX(), showTurns.get(0).getY() });
			return result;
		}
		
		// other else
		Coordinate randomShot = makeSmartRandomShot();
		result.add(new int[] { randomShot.getX(), randomShot.getY() });
		return result;
	}

	/******* MAIN *********/
	public List<Coordinate> getshotsTurn() {
		int hitSize = hitCoordinateList.size();
		Coordinate first, second, third, four;
		Boolean isVetical;
		List<Coordinate> shotsTurn = new ArrayList<>();
		
		switch (hitSize) {
		case 0:
			this.typeCheck = 0;
			// Random shot
			flagGetMaxShot = true;
			shotsTurn.add(this.makeSmartRandomShot());
			return shotsTurn;
		case 1:
			typeCheck = 1;
			return makeShotNeighbors(this.hitCoordinateList);
		case 2:
			typeCheck = 2;

			first = hitCoordinateList.get(0);
			second = hitCoordinateList.get(1);

			isVetical = (first.getX() == second.getX());
			shotsTurn = getNeightBourTypeA(hitCoordinateList, isVetical);

			// result
			if (CollectionUtils.isEmpty(shotsTurn)) {
				typeCheck = 5;
				shotsTurn = makeShotNeighbors(this.hitCoordinateList);
			}
			return shotsTurn;
		case 3:
			typeCheck = 3;

			first = hitCoordinateList.get(0);
			second = hitCoordinateList.get(1);
			third = hitCoordinateList.get(2);

			if (first.getY() == second.getY() && first.getY() == third.getY()) {
				isVetical = false;
			} else if (first.getX() == second.getX() && first.getX() == third.getX()) {
				isVetical = true;
			} else {
				isVetical = null;
			}

			if (isVetical != null) {
				shotsTurn = getNeightBourTypeA(hitCoordinateList, isVetical);
			} else {
				if (shipEnemyMap.containsKey(Ship.SHIP_OR)) {
					// 3 điểm không thẳng hàng -> tìm điểm góc vuông
					Coordinate coordinateFourth = findFourthCoordinate(hitCoordinateList);
					// Check valid coordinateFourth
					if (isValidForShot(coordinateFourth)) {
						shotsTurn.add(coordinateFourth);
						// return neightBour3point; // for bot detected return neightBour3point;
					}
				}

				if (shipEnemyMap.containsKey(Ship.SHIP_CV)) {
					// EMPTY neightBour3point
					Coordinate coordinateCV = findSquareCorner(hitCoordinateList);
					if (coordinateCV != null) {
						int maxColX = this.hitCoordinateList.stream().max(Comparator.comparing(Coordinate::getX))
								.orElseThrow(NoSuchElementException::new).getX();

						if (coordinateCV.getX() == maxColX) {
							List<Coordinate> neighbourCoordinateCV = makeShotNeighbors(coordinateCV);
							shotsTurn.addAll(neighbourCoordinateCV);
						}
					}
				}
			}

			// result
			if (CollectionUtils.isEmpty(shotsTurn)) {
				typeCheck = 5;
				shotsTurn = makeShotNeighbors(this.hitCoordinateList);
			}
			return shotsTurn;
		case 4:
			typeCheck = 4;
			first = hitCoordinateList.get(0);
			second = hitCoordinateList.get(1);
			third = hitCoordinateList.get(2);
			four = hitCoordinateList.get(3);

			if (this.shipEnemyMap.containsKey(Ship.SHIP_CV)) {
				Coordinate remainPoin;
				// Boolean isVetical;
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
							shotsTurn.add(remainPoin);
					} else {
						Coordinate minCoordinateColX = hitCoordinateList.stream()
								.min(Comparator.comparing(Coordinate::getX)).orElseThrow(NoSuchElementException::new);
						remainPoin = new Coordinate(minCoordinateColX.getX() + 1, minCoordinateColX.getY() - 1);
						if (isValidForShot(remainPoin))
							shotsTurn.add(remainPoin);
					}
				} else {
					shotsTurn = findRemainCoordinateShipCV(this.hitCoordinateList);
				}
			}

			// result
			if (CollectionUtils.isEmpty(shotsTurn)) {
				typeCheck = 5;
				shotsTurn = makeShotNeighbors(this.hitCoordinateList);
			}
			return shotsTurn;
		default:
			typeCheck = 5;
			// Shot neightBour previousHit
			return makeShotNeighbors(this.hitCoordinateList);
		}
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

	private ThresholdConfig thresholdConfig = new ThresholdConfig();

    private List<Coordinate> cornerCoordinates = new ArrayList<>();
	private List<Coordinate> borderCoordinates = new ArrayList<>();
	private List<Coordinate> boardCoordinates = new ArrayList<>();

    public List<Coordinate> priorityShotsList = new ArrayList<>();
    public boolean checkPriorityFlag = true;
    public int minScoreShotCornerThreshold = 0;
    
	public Coordinate makeSmartRandomShot() {
		// make priority shot first
    	if (CollectionUtils.isNotEmpty(priorityShotsList)) {
    		Coordinate prioritycoordinate = priorityShotsList.get(0);
    		priorityShotsList.remove(prioritycoordinate);
    		if(isValidForShot(prioritycoordinate)) {
        		return  prioritycoordinate;
    		}
    	}

		List<Coordinate> cordinates;
		Random rand = new Random();
		int thresholdCheck = rand.nextInt(thresholdConfig.getMaxThresholdShot()); // random number

		if (shipEnemyMap.containsKey(Ship.SHIP_DD) || shipEnemyMap.containsKey(Ship.SHIP_CA)) {
			if (thresholdCheck < thresholdConfig.getThresholdShotCorner()
					|| this.myShotNo + 5 > thresholdConfig.getThresholdShotCorner()) {
				// filter
				cordinates = this.cornerCoordinates.stream().filter(cordinate -> isValidForShot(cordinate))
						.collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(cordinates)) {
					for (Coordinate coordinate : cordinates) {
						coordinate.setScore(
								this.getScoreCoordinate(coordinate.getX(), coordinate.getY(), this.shipEnemyMap, true));
					}
					// find maxScore
					int maxScrore = cordinates.stream().max(Comparator.comparing(Coordinate::getScore))
							.orElseThrow(NoSuchElementException::new).getScore();
					if (maxScrore > 0) {
						cordinates = cordinates.stream().filter(cordinate -> cordinate.getScore() == maxScrore)
								.collect(Collectors.toList());
						return cordinates.get(rand.nextInt(cordinates.size()));
					}
				}
			}

			// Check shot border
			if (thresholdCheck < thresholdConfig.getThresholdShotBorder()
					|| this.myShotNo >= thresholdConfig.getMaxShotNoCheckDD()) {
				// filter
				cordinates = this.borderCoordinates.stream().filter(cordinate -> isValidForShot(cordinate))
						.collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(cordinates)) {
					for (Coordinate coordinate : cordinates) {
						coordinate.setScore(
								this.getScoreCoordinate(coordinate.getX(), coordinate.getY(), this.shipEnemyMap, true));
					}
					// find maxScore
					int maxScrore = cordinates.stream().max(Comparator.comparing(Coordinate::getScore))
							.orElseThrow(NoSuchElementException::new).getScore();
					if (maxScrore > 0) {
						cordinates = cordinates.stream().filter(cordinate -> cordinate.getScore() == maxScrore)
								.collect(Collectors.toList());
						for (Coordinate coordinate : cordinates) {
							coordinate.setScore(this.getScoreCoordinate(coordinate.getX(), coordinate.getY(),
									this.shipEnemyMap, false));
						}
						// continue find maxScore
						int maxScrore2 = cordinates.stream().max(Comparator.comparing(Coordinate::getScore))
								.orElseThrow(NoSuchElementException::new).getScore();
						// continue filter
						cordinates = cordinates.stream().filter(cordinate -> cordinate.getScore() == maxScrore2)
								.collect(Collectors.toList());
						return cordinates.get(rand.nextInt(cordinates.size()));
					}
				}
			}
		}

		// ELSE MAIN
		cordinates = this.boardCoordinates.stream().filter(cordinate -> isValidForShot(cordinate))
				.collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(cordinates)) {
			for (Coordinate coordinate : cordinates) {
				coordinate.setScore(this.getScoreCoordinate(coordinate.getX(), coordinate.getY(), this.shipEnemyMap, false));
			}
			// find maxScore
			int maxScrore = cordinates.stream().max(Comparator.comparing(Coordinate::getScore))
					.orElseThrow(NoSuchElementException::new).getScore();
			if (maxScrore > 0) {
				cordinates = cordinates.stream().filter(cordinate -> cordinate.getScore() == maxScrore).collect(Collectors.toList());
				return cordinates.get(rand.nextInt(cordinates.size()));
			}
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
				// return neightBourTypeA; // return
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
			objMax = new Coordinate(maxColX + 1, rowY);
			if (isValidForShot(objMax)) {
				neightBourTypeA.add(objMax);
				// return neightBourTypeA; // return
			}
			objMin = new Coordinate(minColX - 1, rowY);
			if (isValidForShot(objMin)) {
				neightBourTypeA.add(objMin);
			}
		}

		// check ship OR
		if (hitCoordinateList.size() == 2) {
			Coordinate obj;
			if (isVetical) {
				Coordinate objMax2 = hitList.stream().max(Comparator.comparing(Coordinate::getY))
						.orElseThrow(NoSuchElementException::new);
				obj = new Coordinate(objMax2.getX() - 1, objMax2.getY());
				if (isValidForShot(obj)) {
					neightBourTypeA.add(obj);
					return neightBourTypeA; // return for type2
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
					return neightBourTypeA; // return for type2
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
        		board[x][y] = botPlayer.getScoreCoordinate(x, y, botPlayer.shipEnemyMap, false);
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
    
    private int getScoreCoordinate(int x, int y, Map<String, Integer> shipEnemyMap, boolean flagOnlyOnBorder) {
    	int score = 0;    	
		if (shipEnemyMap.containsKey(Ship.SHIP_DD)) {
			score += getScoreShipDD(x, y, shipEnemyMap.get(Ship.SHIP_DD), flagOnlyOnBorder);
		}
		if (shipEnemyMap.containsKey(Ship.SHIP_CA)) {
			score += getScoreShipCA(x, y, shipEnemyMap.get(Ship.SHIP_CA), flagOnlyOnBorder);
		}
		if (shipEnemyMap.containsKey(Ship.SHIP_BB)) {
			score += getScoreShipBB(x, y, shipEnemyMap.get(Ship.SHIP_BB), flagOnlyOnBorder);
		}
		if (shipEnemyMap.containsKey(Ship.SHIP_OR)) {
			score += getScoreShipOR(x, y, shipEnemyMap.get(Ship.SHIP_OR));
		}
		if (shipEnemyMap.containsKey(Ship.SHIP_CV)) {
			score += getScoreShipCV(x, y, shipEnemyMap.get(Ship.SHIP_CV));
		}
		return score;
    }
    
	private void customizeListShip(List<List<Coordinate>> listShip) {
		if (typeCheck == 5) {
			ListIterator<List<Coordinate>> iter = listShip.listIterator();
			while (iter.hasNext()) {
				List<Coordinate> ship = iter.next();
				if (!ship.stream().anyMatch(coordinate -> this.hitCoordinateList.contains(coordinate))) {
					iter.remove(); // not have any match
				}
			}
		} else {
			ListIterator<List<Coordinate>> iter = listShip.listIterator();
			while (iter.hasNext()) {
				List<Coordinate> ship = iter.next();
				if (!ship.containsAll(this.hitCoordinateList)) {
					iter.remove(); // not contain all hitItem
				}
			}
		}
	}
	
    
	private void customizeListShipOnlyOnBorder(List<List<Coordinate>> listShip) {
		ListIterator<List<Coordinate>> iter = listShip.listIterator();
		while (iter.hasNext()) {
			List<Coordinate> ship = iter.next();
			if (!borderCoordinates.containsAll(ship)) {
				iter.remove(); // not have any match
			}
		}

	}
    
    private int getScoreShipDD(int x, int y, int quantity, boolean flagOnlyOnBorder) {
		int score = 0;
		List<List<Coordinate>> listShip = new ArrayList<>();
		listShip.add(List.of(new Coordinate(x, y), new Coordinate(x + 1, y)));
		listShip.add(List.of(new Coordinate(x - 1, y), new Coordinate(x, y)));
		listShip.add(List.of(new Coordinate(x, y), new Coordinate(x, y + 1)));
		listShip.add(List.of(new Coordinate(x, y - 1), new Coordinate(x, y)));
		
		if (CollectionUtils.isNotEmpty(this.hitCoordinateList)) {
			this.customizeListShip(listShip);
		} else {
			// check border
			if(flagOnlyOnBorder) {
				this.customizeListShipOnlyOnBorder(listShip);
			}
		}

		for (List<Coordinate> list : listShip) {
			if(!list.stream().anyMatch(coordinate -> !isCellNotInShotted(coordinate))) {
				score+=quantity;
			}
		}
    	return score;
    }

	private int getScoreShipCA(int x, int y, int quantity, boolean flagOnlyOnBorder) {
		int score = 0;
		List<List<Coordinate>> listShip = new ArrayList<>();
		listShip.add(List.of(new Coordinate(x, y), new Coordinate(x + 1, y), new Coordinate(x + 2, y)));
		listShip.add(List.of(new Coordinate(x - 1, y), new Coordinate(x, y), new Coordinate(x + 1, y)));
		listShip.add(List.of(new Coordinate(x - 2, y), new Coordinate(x - 1, y), new Coordinate(x, y)));
		listShip.add(List.of(new Coordinate(x, y), new Coordinate(x, y + 1), new Coordinate(x, y + 2)));
		listShip.add(List.of(new Coordinate(x, y - 1), new Coordinate(x, y), new Coordinate(x, y + 1)));
		listShip.add(List.of(new Coordinate(x, y - 2), new Coordinate(x, y - 1), new Coordinate(x, y)));
		
		if (CollectionUtils.isNotEmpty(this.hitCoordinateList)) {
			this.customizeListShip(listShip);
		} else {
			// check border
			if(flagOnlyOnBorder) {
				this.customizeListShipOnlyOnBorder(listShip);
			}
		}

		for (List<Coordinate> list : listShip) {
			if(!list.stream().anyMatch(coordinate -> !isCellNotInShotted(coordinate))) {
				score+=quantity;
			}
		}
    	return score;
    }
    
    public int getScoreShipBB(int x, int y, int quantity, boolean flagOnlyOnBorder) {
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
		
		if (CollectionUtils.isNotEmpty(this.hitCoordinateList)) {
			this.customizeListShip(listShip);
		} else {
			// check border
			if(flagOnlyOnBorder) {
				this.customizeListShipOnlyOnBorder(listShip);
			}
		}

		for (List<Coordinate> list : listShip) {
			if(!list.stream().anyMatch(coordinate -> !isCellNotInShotted(coordinate))) {
				score+=quantity;
			}
		}
    	return score;
    }
    
    private int getScoreShipCV(int x, int y, int quantity) {
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
		
		if (CollectionUtils.isNotEmpty(this.hitCoordinateList)) {
			this.customizeListShip(listShip);
		}

		for (List<Coordinate> list : listShip) {
			if(!list.stream().anyMatch(coordinate -> !isCellNotInShotted(coordinate))) {
				score+=quantity;
			}
		}
		return score;	
    }
   
    private int getScoreShipOR(int x, int y, int quantity) {
		int score = 0;
		List<List<Coordinate>> listShip = new ArrayList<>();
		listShip.add(List.of(new Coordinate(x, y), new Coordinate(x, y+1), new Coordinate(x+1, y+1), new Coordinate(x+1, y+1)));
		listShip.add(List.of(new Coordinate(x, y), new Coordinate(x-1, y), new Coordinate(x, y+1), new Coordinate(x-1, y-1)));
		listShip.add(List.of(new Coordinate(x, y), new Coordinate(x-1, y), new Coordinate(x-1, y-1), new Coordinate(x, y-1)));
		listShip.add(List.of(new Coordinate(x, y), new Coordinate(x+1, y), new Coordinate(x, y-1), new Coordinate(x-1, y-1)));
		
		if (CollectionUtils.isNotEmpty(this.hitCoordinateList)) {
			this.customizeListShip(listShip);
		}

		for (List<Coordinate> list : listShip) {
			if(!list.stream().anyMatch(coordinate -> !isCellNotInShotted(coordinate))) {
				score+=quantity;
			}
		}
    	return score;
    }

	public ThresholdConfig getThresholdConfig() {
		return thresholdConfig;
	}

	public void setThresholdConfig(ThresholdConfig thresholdConfig) {
		this.thresholdConfig = thresholdConfig;
	}
}
