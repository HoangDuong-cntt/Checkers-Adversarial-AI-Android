package com.androidproject.checkersgame.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.androidproject.checkersgame.R;
import com.androidproject.checkersgame.ai.MinimaxSolver;
import com.androidproject.checkersgame.logic.GameEngine;
import com.androidproject.checkersgame.logic.Move;
import com.androidproject.checkersgame.models.Board;
import com.androidproject.checkersgame.firebase.FirebaseRealtimeManager;
import static com.androidproject.checkersgame.utils.Constants.*;

public class GameActivity extends AppCompatActivity implements BoardView.OnMoveListener {

    private Board board;
    private BoardView boardView;
    private TextView tvStatus;
    private Button btnReset; //nút Chơi lại

    private String gameMode = "AI"; // "AI" hoặc "PVP"
    private boolean isAiThinking = false;
    private final Handler aiHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Kích hoạt lắng nghe cấu hình từ Firebase realtime
        FirebaseRealtimeManager firebaseManager = new FirebaseRealtimeManager();
        firebaseManager.startListeningToAIConfig();

        // Ánh xạ View từ layout XML
        boardView = findViewById(R.id.boardView);
        tvStatus = findViewById(R.id.tvStatus);
        btnReset = findViewById(R.id.btnReset); // Ánh xạ nút từ XML

        // Nhận chế độ chơi từ Menu truyền sang
        if (getIntent() != null && getIntent().hasExtra("mode")) {
            gameMode = getIntent().getStringExtra("mode");
        }

        // Khởi tạo bàn cờ mới từ GameEngine
        board = GameEngine.createNewBoard();
        boardView.setBoard(board);

        // Đăng ký sự kiện lắng nghe di chuyển
        boardView.setOnMoveListener(this);

        // Lắng nghe sự kiện click nút "Chơi Lại Trận Mới"
        if (btnReset != null) {
            btnReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetGame();
                }
            });
        }

        updateStatusText();
    }

    //  Hàm xử lý Reset game về trạng thái ban đầu
    private void resetGame() {
        // 1. Hủy bỏ mọi hành động AI đang xếp hàng chờ đi (nếu có) để tránh bug
        aiHandler.removeCallbacksAndMessages(null);
        isAiThinking = false;

        // 2. Khởi tạo lại một đối tượng bàn cờ mới tinh từ Engine
        board = GameEngine.createNewBoard();

        // 3. Đẩy dữ liệu mới vào BoardView và kích hoạt tương tác trở lại
        boardView.setBoard(board);
        boardView.setEnabled(true);

        // 4. Cập nhật lại thanh trạng thái chữ hiển thị
        updateStatusText();

        Toast.makeText(this, "Đã bắt đầu ván cờ mới!", Toast.LENGTH_SHORT).show();
    }

    // Hàm xử lý di chuyển khi người chơi thực hiện nước đi trên BoardView
    @Override
    public void onPlayerMove(Move move) {
        // Nếu AI đang tính toán thì chặn không cho người chơi đi tiếp
        if (isAiThinking) return;

        // Thực hiện nước đi của người chơi
        board = GameEngine.makeMove(board, move);
        boardView.setBoard(board);
        updateStatusText();

        // Kiểm tra xem ván cờ kết thúc chưa
        if (GameEngine.isGameOver(board)) {
            handleGameOver();
            return;
        }

        //XỬ LÝ ĐỐI KHÁNG AI REALTIME TỰ ĐỘNG
        if ("AI".equals(gameMode) && board.turn == TURN_BLACK) {
            isAiThinking = true;
            boardView.setEnabled(false); // Vô hiệu hóa touch tạm thời

            // Tạo độ trễ 500ms tạo cảm giác AI đang suy nghĩ thực tế
            aiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Gọi thuật toán Minimax lấy nước đi tốt nhất cho máy (Quân đen)
                    Move aiMove = MinimaxSolver.getBestMove(board, 3);

                    if (aiMove != null) {
                        board = GameEngine.makeMove(board, aiMove);
                        boardView.setBoard(board);
                        updateStatusText();

                        // Kiểm tra ván cờ sau khi AI đi xong
                        if (GameEngine.isGameOver(board)) {
                            handleGameOver();
                        }
                    }

                    // Trả lại trạng thái cho người chơi đi tiếp
                    isAiThinking = false;
                    boardView.setEnabled(true);
                }
            }, 500);
        }
    }

    // Hàm cập nhật chữ trạng thái hiển thị lượt đi
    private void updateStatusText() {
        if (tvStatus == null) return;

        if (board.turn == TURN_WHITE) {
            tvStatus.setText("Lượt của bạn (Quân Trắng)");
        } else {
            if ("AI".equals(gameMode)) {
                tvStatus.setText("Lượt của máy (AI đang tính...)");
            } else {
                tvStatus.setText("Lượt của phe Đen (Người chơi 2)");
            }
        }
    }

    // Hàm xử lý kết thúc trận đấu
    private void handleGameOver() {
        int winner = GameEngine.getWinner(board);
        String msg;
        if (winner == TURN_WHITE) {
            msg = "Trận đấu kết thúc! Bạn (Phe Trắng) đã chiến thắng!";
        } else if (winner == TURN_BLACK) {
            msg = "AI".equals(gameMode) ? "Trận đấu kết thúc! Máy (AI) thắng!" : "Trận đấu kết thúc! Người chơi 2 (Phe Đen) thắng!";
        } else {
            msg = "Trận đấu kết thúc! Hòa cờ!";
        }

        tvStatus.setText(msg);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        boardView.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Xóa sạch hàng đợi tránh rò rỉ bộ nhớ khi thoát Activity đột ngột
        aiHandler.removeCallbacksAndMessages(null);
    }
}