package com.androidproject.checkersgame.ai;

import com.androidproject.checkersgame.logic.GameEngine;
import com.androidproject.checkersgame.logic.Move;
import com.androidproject.checkersgame.models.Board;
import java.util.List;
import static com.androidproject.checkersgame.utils.Constants.*;

public class MinimaxSolver {

    // Các hằng số điểm trọng số  (có thể thay đổi động từ Firebase)
    public static int WEIGHT_PIECE = 100; // Điểm của một quân cờ thường
    public static int WEIGHT_KING = 250;  // Điểm của một quân Vua

    // Hàm gọi từ bên ngoài để tìm nước đi tốt nhất cho AI (Quân Đen)
    public static Move getBestMove(Board board, int depth) {
        Move bestMove = null;
        int maxEval = Integer.MIN_VALUE;
        List<Move> legalMoves = GameEngine.getLegalMoves(board, TURN_BLACK);

        for (Move move : legalMoves) {
            // Giả lập thử nước đi trên một bản sao bàn cờ
            Board simulatedBoard = board.copy();
            GameEngine.makeMove(simulatedBoard, move);

            // Tính điểm nước đi bằng hàm Minimax (lúc này đến lượt Người - TURN_WHITE)
            int eval = minimax(simulatedBoard, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

            if (eval > maxEval) {
                maxEval = eval;
                bestMove = move;
            }
        }
        return bestMove;
    }

    // Hàm đệ quy Minimax tích hợp cắt tỉa Alpha - Beta
    private static int minimax(Board board, int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        // Điều kiện dừng: Đạt tới độ sâu giới hạn hoặc một bên đã thua (không còn nước đi)
        if (depth == 0) {
            return evaluateBoard(board);
        }

        if (isMaximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            List<Move> moves = GameEngine.getLegalMoves(board, TURN_BLACK);
            if (moves.isEmpty()) return Integer.MIN_VALUE + 1; // AI bí nước ➔ Thua

            for (Move move : moves) {
                Board tempBoard = board.copy();
                GameEngine.makeMove(tempBoard, move);
                int eval = minimax(tempBoard, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break; // Cắt tỉa nhánh Min (Beta cut-off)
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            List<Move> moves = GameEngine.getLegalMoves(board, TURN_WHITE);
            if (moves.isEmpty()) return Integer.MAX_VALUE - 1; // Người bí nước ➔ AI thắng

            for (Move move : moves) {
                Board tempBoard = board.copy();
                GameEngine.makeMove(tempBoard, move);
                int eval = minimax(tempBoard, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break; // Cắt tỉa nhánh Max (Alpha cut-off)
            }
            return minEval;
        }
    }

    // Hàm lượng giá bàn cờ: Tổng điểm AI (Đen) trừ đi tổng điểm Người (Trắng)
    private static int evaluateBoard(Board board) {
        int score = 0;
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                int piece = board.getPieceAt(r, c);
                if (piece == BLACK_PIECE) {
                    score += WEIGHT_PIECE;
                } else if (piece == BLACK_KING) {
                    score += WEIGHT_KING;
                } else if (piece == WHITE_PIECE) {
                    score -= WEIGHT_PIECE;
                } else if (piece == WHITE_KING) {
                    score -= WEIGHT_KING;
                }
            }
        }
        return score;
    }
}