package ati.player.rest.api.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import ati.player.rest.api.entity.Coordinate;

public class Game {
    private int[][] board;
    private int boardWidth;
    private int boardHeight;
    private int shotsPerTurn;
    

    private int previousHitX;
    private int previousHitY;
    
    private List<Shot> previousShots = new ArrayList<>();

    public Game(int width, int height, int shots) {
        this.boardWidth = width;
        this.boardHeight = height;
        this.shotsPerTurn = shots;
        this.board = new int[height][width];
        
    }

//    public boolean fire(int x, int y) {
//        if (board[y][x] == 0) {
//            board[y][x] = 1;
//            return false;
//        } else {
//            board[y][x] = 2;
//            return true;
//        }
//    }

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
