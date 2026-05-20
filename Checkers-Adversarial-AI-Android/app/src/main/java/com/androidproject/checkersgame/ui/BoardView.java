package com.androidproject.checkersgame.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.androidproject.checkersgame.logic.GameEngine;
import com.androidproject.checkersgame.logic.Move;
import com.androidproject.checkersgame.models.Board;
import java.util.ArrayList;
import java.util.List;
import static com.androidproject.checkersgame.utils.Constants.*;

public class BoardView extends View {
    private Paint paint;
    private Board board;
    private int cellWidth, cellHeight;

    private int selectedRow = -1;
    private int selectedCol = -1;
    private List<Move> legalMovesForSelected = new ArrayList<>();
    private OnMoveListener moveListener;

    public interface OnMoveListener {
        void onPlayerMove(Move move);
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true); // Bật khử răng cưa giúp nét vẽ mượt, tròn trịa
    }

    public void setBoard(Board board) {
        this.board = board;
        invalidate(); // Yêu cầu vẽ lại giao diện khi dữ liệu bàn cờ thay đổi
    }

    public void setOnMoveListener(OnMoveListener listener) {
        this.moveListener = listener;
    }

    public void resetSelection() {
        selectedRow = -1;
        selectedCol = -1;
        legalMovesForSelected.clear();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Tính toán kích thước vuông cho từng ô cờ dựa trên kích thước màn hình điện thoại
        cellWidth = w / BOARD_SIZE;
        cellHeight = h / BOARD_SIZE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (board == null) return;

        // 1. Vẽ lưới ô cờ sáng / tối xen kẽ
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if ((r + c) % 2 == 0) {
                    paint.setColor(Color.parseColor(COLOR_LIGHT_CELL));
                } else {
                    paint.setColor(Color.parseColor(COLOR_DARK_CELL));
                }
                canvas.drawRect(c * cellWidth, r * cellHeight, (c + 1) * cellWidth, (r + 1) * cellHeight, paint);
            }
        }

        // 2. Vẽ Highlight ô cờ đang được người chơi nhấn chọn
        if (selectedRow != -1 && selectedCol != -1) {
            paint.setColor(Color.parseColor(COLOR_SELECTED));
            canvas.drawRect(selectedCol * cellWidth, selectedRow * cellHeight, (selectedCol + 1) * cellWidth, (selectedRow + 1) * cellHeight, paint);
        }

        // 3. Vẽ các chấm gợi ý nước đi hợp lệ (Màu xanh lá nhẹ)
        paint.setColor(Color.parseColor(COLOR_HIGHLIGHT_MOVE));
        for (Move move : legalMovesForSelected) {
            float cx = move.toCol * cellWidth + cellWidth / 2f;
            float cy = move.toRow * cellHeight + cellHeight / 2f;
            canvas.drawCircle(cx, cy, cellWidth / 6f, paint);
        }

        // 4. Vẽ các quân cờ hình tròn và biểu tượng Vua
        float radius = Math.min(cellWidth, cellHeight) * 0.4f;
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                int piece = board.getPieceAt(r, c);
                if (piece == EMPTY_CELL) continue;

                float cx = c * cellWidth + cellWidth / 2f;
                float cy = r * cellHeight + cellHeight / 2f;

                // Xác định màu sắc đổ bóng cho quân cờ
                if (piece == WHITE_PIECE || piece == WHITE_KING) {
                    paint.setColor(Color.parseColor(COLOR_WHITE_PIECE));
                } else {
                    paint.setColor(Color.parseColor(COLOR_BLACK_PIECE));
                }
                canvas.drawCircle(cx, cy, radius, paint);

                // Vẽ viền mỏng xung quanh quân cờ cho sắc nét
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(4f);
                paint.setColor(Color.GRAY);
                canvas.drawCircle(cx, cy, radius, paint);
                paint.setStyle(Paint.Style.FILL); // Trả lại trạng thái vẽ đặc

                // Nếu là quân Vua, vẽ thêm một vòng tròn vàng nhỏ bên trong làm vương miện
                if (piece == WHITE_KING || piece == BLACK_KING) {
                    paint.setColor(Color.YELLOW);
                    canvas.drawCircle(cx, cy, radius / 2.5f, paint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Đổi tọa độ pixel điểm chạm của ngón tay ra chỉ số dòng/cột trong ma trận
            int c = (int) (event.getX() / cellWidth);
            int r = (int) (event.getY() / cellHeight);

            if (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE) {
                handleTouch(r, c);
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void handleTouch(int r, int c) {
        // 1. Kiểm tra xem ô vừa chạm có trùng với một trong các chấm gợi ý nước đi hay không
        for (Move move : legalMovesForSelected) {
            if (move.toRow == r && move.toCol == c) {
                if (moveListener != null) {
                    moveListener.onPlayerMove(move); // Kích hoạt xử lý di chuyển quân cờ
                }
                resetSelection();
                return;
            }
        }

        // 2. Lấy thông tin quân cờ tại vị trí chạm ngón tay
        int piece = board.getPieceAt(r, c);
        if (piece == EMPTY_CELL) {
            resetSelection();
            return;
        }

        // 3. KIỂM TRA LƯỢT ĐI VÀ CHỈ CHO PHÉP CHỌN ĐÚNG QUÂN CỦA PHE ĐẾN LƯỢT
        if (board.turn == TURN_WHITE && (piece == WHITE_PIECE || piece == WHITE_KING)) {
            selectPieceAndShowMoves(r, c, TURN_WHITE);
        } else if (board.turn == TURN_BLACK && (piece == BLACK_PIECE || piece == BLACK_KING)) {
            selectPieceAndShowMoves(r, c, TURN_BLACK);
        } else {
            // Chạm sai quân của phe không phải lượt mình -> Hủy chọn
            resetSelection();
        }
    }

    // Hàm phụ trợ giúp hiển thị highlight và quét tìm nước đi hợp lệ theo lượt
    private void selectPieceAndShowMoves(int r, int c, int turn) {
        selectedRow = r;
        selectedCol = c;
        legalMovesForSelected.clear();

        // Lấy tất cả các nước đi hợp lệ của toàn bộ bàn cờ tại lượt của phe hiện tại
        List<Move> allLegalMoves = GameEngine.getLegalMoves(board, turn);

        // Lọc riêng ra những nước đi thuộc về quân cờ cụ thể vừa được chọn
        for (Move m : allLegalMoves) {
            if (m.fromRow == r && m.fromCol == c) {
                legalMovesForSelected.add(m);
            }
        }
        invalidate(); // Lệnh ép giao diện vẽ lại để cập nhật Highlight và Chấm gợi ý
    }
}