package com.androidproject.checkersgame.logic;

public class Move {
    public int fromRow, fromCol;
    public int toRow, toCol;
    public boolean isJump; // True nếu đây là nước cờ ăn (nhảy qua) quân đối phương

    public Move(int fromRow, int fromCol, int toRow, int toCol, boolean isJump) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.isJump = isJump;
    }
}