package com.androidproject.checkersgame.logic;

import com.androidproject.checkersgame.models.Board;
import java.util.ArrayList;
import java.util.List;
import static com.androidproject.checkersgame.utils.Constants.*;

public class GameEngine {
    public static Board createNewBoard() {
        return new Board(); // tự kích hoạt constructor khởi tạo quân Trắng/Đen mặc định bên lớp Board
    }

    // Kiểm tra xem một quân cờ cụ thể có nước cờ ăn quân (Jump) nào không
    public static List<Move> getLegalJumpsForPiece(Board board, int r, int c) {
        List<Move> jumps = new ArrayList<>();
        int piece = board.getPieceAt(r, c);
        if (piece == EMPTY_CELL) return jumps;

        int[] rowDirs;
        if (piece == WHITE_KING || piece == BLACK_KING) {
            rowDirs = new int[]{-1, 1}; // Vua đi hướng nào cũng được
        } else if (piece == WHITE_PIECE) {
            rowDirs = new int[]{-1};    // Trắng đi tiến lên
        } else {
            rowDirs = new int[]{1};     // Đen đi tiến xuống
        }
        int[] colDirs = new int[]{-1, 1};

        for (int rd : rowDirs) {
            for (int cd : colDirs) {
                int victimRow = r + rd;
                int victimCol = c + cd;
                int targetRow = r + 2 * rd;
                int targetCol = c + 2 * cd;

                if (targetRow >= 0 && targetRow < BOARD_SIZE && targetCol >= 0 && targetCol < BOARD_SIZE) {
                    int victim = board.getPieceAt(victimRow, victimCol);
                    int target = board.getPieceAt(targetRow, targetCol);

                    if (target == EMPTY_CELL && victim != EMPTY_CELL) {
                        if ((piece == WHITE_PIECE || piece == WHITE_KING) && (victim == BLACK_PIECE || victim == BLACK_KING)) {
                            jumps.add(new Move(r, c, targetRow, targetCol, true));
                        } else if ((piece == BLACK_PIECE || piece == BLACK_KING) && (victim == WHITE_PIECE || victim == WHITE_KING)) {
                            jumps.add(new Move(r, c, targetRow, targetCol, true));
                        }
                    }
                }
            }
        }
        return jumps;
    }

    // Lấy tất cả các nước đi hợp lệ của một phe tại lượt hiện tại
    public static List<Move> getLegalMoves(Board board, int turn) {
        List<Move> jumpMoves = new ArrayList<>();
        List<Move> normalMoves = new ArrayList<>();

        // 1. Tìm các nước nhảy ăn quân BẮT BUỘC trước ( ép ăn)
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                int piece = board.getPieceAt(r, c);
                if (piece != EMPTY_CELL && ((turn == TURN_WHITE && (piece == WHITE_PIECE || piece == WHITE_KING)) ||
                        (turn == TURN_BLACK && (piece == BLACK_PIECE || piece == BLACK_KING)))) {
                    jumpMoves.addAll(getLegalJumpsForPiece(board, r, c));
                }
            }
        }

        if (!jumpMoves.isEmpty()) {
            return jumpMoves;
        }

        // 2. Tìm các nước di chuyển đơn ô thông thường
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                int piece = board.getPieceAt(r, c);
                if (piece != EMPTY_CELL && ((turn == TURN_WHITE && (piece == WHITE_PIECE || piece == WHITE_KING)) ||
                        (turn == TURN_BLACK && (piece == BLACK_PIECE || piece == BLACK_KING)))) {

                    int[] rowDirs;
                    if (piece == WHITE_KING || piece == BLACK_KING) {
                        rowDirs = new int[]{-1, 1};
                    } else if (piece == WHITE_PIECE) {
                        rowDirs = new int[]{-1};
                    } else {
                        rowDirs = new int[]{1};
                    }
                    int[] colDirs = new int[]{-1, 1};

                    for (int rd : rowDirs) {
                        for (int cd : colDirs) {
                            int targetRow = r + rd;
                            int targetCol = c + cd;

                            if (targetRow >= 0 && targetRow < BOARD_SIZE && targetCol >= 0 && targetCol < BOARD_SIZE) {
                                if (board.getPieceAt(targetRow, targetCol) == EMPTY_CELL) {
                                    normalMoves.add(new Move(r, c, targetRow, targetCol, false));
                                }
                            }
                        }
                    }
                }
            }
        }
        return normalMoves;
    }
    public static Board makeMove(Board board, Move move) {
        int piece = board.getPieceAt(move.fromRow, move.fromCol);
        board.setPieceAt(move.fromRow, move.fromCol, EMPTY_CELL);

        if (move.isJump) {
            int victimRow = (move.fromRow + move.toRow) / 2;
            int victimCol = (move.fromCol + move.toCol) / 2;
            board.setPieceAt(victimRow, victimCol, EMPTY_CELL);
        }

        // Phong Vua nếu đi tới hàng cuối cùng của phe đối phương
        if (piece == WHITE_PIECE && move.toRow == 0) {
            piece = WHITE_KING;
        } else if (piece == BLACK_PIECE && move.toRow == BOARD_SIZE - 1) {
            piece = BLACK_KING;
        }

        board.setPieceAt(move.toRow, move.toCol, piece);

        // XỬ LÝ ĐỔI LƯỢT HOẶC TIẾP TỤC NHẢY
        // Nếu vừa đi nước ăn quân, kiểm tra xem quân cờ tại vị trí mới đó có thể nhảy ăn tiếp được không
        if (move.isJump && !getLegalJumpsForPiece(board, move.toRow, move.toCol).isEmpty()) {
            // Giữ nguyên lượt để ép người chơi/AI phải thực hiện chuỗi nước ăn quân tiếp theo
        } else {
            // Nếu là nước đi thường hoặc không còn quân nào để ăn tiếp -> Tiến hành đổi lượt chơi
            board.turn = (board.turn == TURN_WHITE) ? TURN_BLACK : TURN_WHITE;
        }

        return board;
    }

    // Hàm kiểm tra ván cờ đã kết thúc chưa (Hết quân hoặc không còn nước đi hợp lệ)
    public static boolean isGameOver(Board board) {
        return getLegalMoves(board, board.turn).isEmpty();
    }

    // Hàm xác định phe chiến thắng
    public static int getWinner(Board board) {
        if (isGameOver(board)) {
            // Nếu đến lượt phe hiện tại đi mà không thể đi tiếp -> Phe kia giành chiến thắng
            return (board.turn == TURN_WHITE) ? TURN_BLACK : TURN_WHITE;
        }
        return EMPTY_CELL; // Chưa kết thúc
    }
}