package com.androidproject.checkersgame.utils;

public class Constants {
    // Kích thước bàn cờ Đam tiêu chuẩn 8x8
    public static final int BOARD_SIZE = 8;

    //giá trị các loại ô và quân cờ trên ma trận
    public static final int EMPTY_CELL = 0;
    public static final int WHITE_PIECE = 1;
    public static final int WHITE_KING = 2;
    public static final int BLACK_PIECE = 3;
    public static final int BLACK_KING = 4;

    // Định nghĩa lượt đi
    public static final int TURN_WHITE = 1; // Trắng đi trước
    public static final int TURN_BLACK = 3; // Đen đi sau

    // Mã màu Hex đồ họa để vẽ giao diện (Custom View)
    public static final String COLOR_LIGHT_CELL = "#F0D9B5"; // Màu ô sáng (Kem)
    public static final String COLOR_DARK_CELL = "#B58863";  // Màu ô tối (Nâu gỗ)
    public static final String COLOR_WHITE_PIECE = "#FFFFFF"; // Quân Trắng
    public static final String COLOR_BLACK_PIECE = "#2B2B2B"; // Quân Đen
    public static final String COLOR_SELECTED = "#7B9652";    // Màu viền khi nhấn chọn quân cờ
    public static final String COLOR_HIGHLIGHT_MOVE = "#9BBC6A"; // Màu gợi ý nước đi hợp lệ
}