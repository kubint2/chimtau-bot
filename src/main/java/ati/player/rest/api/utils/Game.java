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

    	List<Coordinate> shotsTurn = getshotsTurn();
    	for (Coordinate coordinate : shotsTurn) {
    		coordinates.add(new int[]{coordinate.getX(),coordinate.getY()} );
		}

    	response.setCoordinates(coordinates);
    	
    	System.out.println(JsonUtil.objectToJson(response));
    }
    
    
    public List<Coordinate> getshotsTurn() {
    	List<Coordinate> shotsTurn = new ArrayList<>();
    	
    	if(hitList.size() == 0) {
    		// Random shot
    	} else if (hitList.size() == 1) {
    		// Shot neightBour previousHit

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
            	// lấy 1 điểm trong 2 điểm đầu và cuối để shot
            	neightBour3point = getNeightBourTypeA(hitList, true);
            	
            	
            	
            } else if (first.getX() == second.getX() && first.getX() == third.getX()) {
            	vertical = false;
            	neightBour3point = getNeightBourTypeA(hitList, false);
            } else {
            	vertical = null;
            	// 3 điểm không thẳng hàng -> tìm điểm góc vuông
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
        	}
    	}
    	return shotsTurn;
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
        // Tìm tọa độ còn lại
        int x4, y4;
        if (first.getX() == second.getX()) {
            // Trường hợp đường chéo chính nằm dọc
            x4 = third.getX();
            y4 = first.getY() + third.getY() - second.getY();
        } else {
            // Trường hợp đường chéo chính nằm ngang
            x4 = first.getX() + third.getX() - second.getX();
            y4 = third.getY();
        }

        // Tạo đối tượng Coordinate mới để lưu tọa độ của đỉnh còn lại
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

        // Tạo ra một tập hợp chứa các ô hàng xóm của các ô đã bị bắn trúng
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

        // Tìm kiếm tất cả các ô hàng xóm chưa bị bắn
        List<Coordinate> unshotNeighbors = new ArrayList<>();
        for (Coordinate neighbor : neighborCells) {
            if (!this.isShotted(neighbor)) {
                unshotNeighbors.add(neighbor);
            }
        }

        // Nếu không có ô hàng xóm chưa bị bắn thì trả về cú bắn ngẫu nhiên
        if (unshotNeighbors.isEmpty()) {
            return makeRandomShot(board);
        }

        // Ngược lại, chọn một ô hàng xóm ngẫu nhiên để bắn
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

	// Hàm này lấy đối tượng Board của đối thủ và tìm kiếm tất cả các ô hàng xóm của các ô đã bị bắn trúng. Sau đó, tìm kiếm tất cả các ô hàng xóm chưa bị bắn và chọn một ô hàng x

    // Đây là phần tiếp theo của code Java để hoàn thành thuật toán dò bắn tàu đối phương:

	/*

    // Lấy tọa độ mới nhất để bắn
	Coordinate shotCoordinate = getNextShotCoordinate();

    // Kiểm tra tọa độ đã được bắn chưa
    while (isShotted(shotCoordinate)) {
      // Tọa độ đã được bắn, chuyển sang tọa độ khác
      shotCoordinate = getNextShotCoordinate();
    }

    // Gửi yêu cầu bắn tới server
    String result = sendShotRequest(shotCoordinate);

    // Xử lý kết quả trả về
    if (result.equals("HIT")) {
      // Nếu bắn trúng, cập nhật trạng thái và lưu tọa độ
      updateShotStatus("HIT", shotCoordinate);
      lastShotCoordinate = shotCoordinate;
    } else if (result.equals("MISS")) {
      // Nếu bắn hụt, cập nhật trạng thái và lưu tọa độ
      updateShotStatus("MISS", shotCoordinate);
      lastShotCoordinate = shotCoordinate;
    } else if (result.equals("SUNK")) {
      // Nếu tàu bị chìm, cập nhật trạng thái và xóa tọa độ
      updateShotStatus("HIT", shotCoordinate);
      removeSunkShip(shotCoordinate);
      lastShotCoordinate = null;
    }
    
    */
}
