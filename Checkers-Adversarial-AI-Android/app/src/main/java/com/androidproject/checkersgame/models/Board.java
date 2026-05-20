package com.androidproject.checkersgame.models;

import static com.androidproject.checkersgame.utils.Constants.*;

public class Board {
    private int[][] matrix;

    // Quản lý lượt đi trực tiếp trên đối tượng bàn cờ
    public int turn;

    public Board() {
        matrix = new int[BOARD_SIZE][BOARD_SIZE];
        resetBoard();
    }

    // Hàm khởi tạo vị trí ban đầu cho 24 quân cờ
    public void resetBoard() {
        // Mặc định trò chơi bắt đầu bằng lượt của quân TRẮNG
        this.turn = TURN_WHITE;

        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                matrix[r][c] = EMPTY_CELL;

                // Quy tắc: Chỉ xếp quân trên các ô màu tối (r + c là số lẻ)
                if ((r + c) % 2 != 0) {
                    if (r < 3) {
                        matrix[r][c] = BLACK_PIECE; // 12 quân Đen xếp ở 3 hàng đầu
                    } else if (r > 4) {
                        matrix[r][c] = WHITE_PIECE; // 12 quân Trắng xếp ở 3 hàng cuối
                    }
                }
            }
        }
    }

    public int getPieceAt(int row, int col) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) return EMPTY_CELL;
        return matrix[row][col];
    }

    public void setPieceAt(int row, int col, int pieceType) {
        if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
            matrix[row][col] = pieceType;
        }
    }

    // Sao chép trạng thái bàn cờ hiện tại phục vụ AI
    public Board copy() {
        Board newBoard = new Board();
        // Sao chép cả lượt đi hiện tại sang bàn cờ mô phỏng của AI
        newBoard.turn = this.turn;

        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                newBoard.setPieceAt(r, c, this.matrix[r][c]);
            }
        }
        return newBoard;
    }
}