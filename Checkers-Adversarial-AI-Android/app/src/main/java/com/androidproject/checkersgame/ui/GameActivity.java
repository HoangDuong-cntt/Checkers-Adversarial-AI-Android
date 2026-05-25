package com.androidproject.checkersgame.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.androidproject.checkersgame.R;
import com.androidproject.checkersgame.ai.MinimaxSolver;
import com.androidproject.checkersgame.logic.GameEngine;
import com.androidproject.checkersgame.logic.Move;
import com.androidproject.checkersgame.models.Board;
import com.androidproject.checkersgame.models.GameResult;
import com.androidproject.checkersgame.firebase.FirebaseRealtimeManager;
import static com.androidproject.checkersgame.utils.Constants.*;

public class GameActivity extends AppCompatActivity implements BoardView.OnMoveListener {

    private Board board;
    private BoardView boardView;
    private TextView tvStatus;
    private Button btnReset;

    private String gameMode = "AI"; // "AI" hoặc "PVP"
    private boolean isAiThinking = false;
    private final Handler aiHandler = new Handler(Looper.getMainLooper());
    private FirebaseRealtimeManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        firebaseManager = new FirebaseRealtimeManager();
        firebaseManager.startListeningToAIConfig();

        boardView = findViewById(R.id.boardView);
        tvStatus = findViewById(R.id.tvStatus);
        btnReset = findViewById(R.id.btnReset);

        if (getIntent() != null && getIntent().hasExtra("mode")) {
            gameMode = getIntent().getStringExtra("mode");
        }

        board = GameEngine.createNewBoard();
        boardView.setBoard(board);
        boardView.setOnMoveListener(this);

        if (btnReset != null) {
            btnReset.setOnClickListener(v -> resetGame());
        }

        updateStatusText();
    }

    private void resetGame() {
        aiHandler.removeCallbacksAndMessages(null);
        isAiThinking = false;
        board = GameEngine.createNewBoard();
        boardView.setBoard(board);
        boardView.setEnabled(true);
        updateStatusText();
        Toast.makeText(this, "Đã bắt đầu ván cờ mới!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlayerMove(Move move) {
        if (isAiThinking) return;

        board = GameEngine.makeMove(board, move);
        boardView.setBoard(board);
        updateStatusText();

        if (GameEngine.isGameOver(board)) {
            handleGameOver();
            return;
        }

        if ("AI".equals(gameMode) && board.turn == TURN_BLACK) {
            isAiThinking = true;
            boardView.setEnabled(false);

            aiHandler.postDelayed(() -> {
                Move aiMove = MinimaxSolver.getBestMove(board, 3);
                if (aiMove != null) {
                    board = GameEngine.makeMove(board, aiMove);
                    boardView.setBoard(board);
                    updateStatusText();

                    if (GameEngine.isGameOver(board)) {
                        handleGameOver();
                    }
                }
                isAiThinking = false;
                boardView.setEnabled(true);
            }, 500);
        }
    }

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

    private void handleGameOver() {
        int winner = GameEngine.getWinner(board);
        String msg;
        String resultStatus;

        if (winner == TURN_WHITE) {
            msg = "Trận đấu kết thúc! Bạn (Phe Trắng) đã chiến thắng!";
            resultStatus = "Thắng";
        } else if (winner == TURN_BLACK) {
            msg = "AI".equals(gameMode) ? "Trận đấu kết thúc! Máy (AI) thắng!" : "Trận đấu kết thúc! Người chơi 2 (Phe Đen) thắng!";
            resultStatus = "Thua";
        } else {
            msg = "Trận đấu kết thúc! Hòa cờ!";
            resultStatus = "Hòa";
        }

        tvStatus.setText(msg);
        boardView.setEnabled(false);
        showSaveResultDialog(msg, resultStatus);
    }

    private void showSaveResultDialog(String message, String resultStatus) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_save_result, null);
        builder.setView(dialogView);

        TextView tvMsg = dialogView.findViewById(R.id.tvDialogMessage);
        EditText etName = dialogView.findViewById(R.id.etPlayerName);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        tvMsg.setText(message);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                etName.setError("Vui lòng nhập tên");
                return;
            }

            GameResult result = new GameResult(name, gameMode, resultStatus, System.currentTimeMillis());
            firebaseManager.saveGameResult(result);
            dialog.dismiss();
            
            Toast.makeText(this, "Đã lưu thành tích!", Toast.LENGTH_SHORT).show();

            // Chuyển sang màn hình lịch sử sau khi lưu
            Intent intent = new Intent(GameActivity.this, HistoryActivity.class);
            startActivity(intent);
            finish(); // Kết thúc màn hình chơi game
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        aiHandler.removeCallbacksAndMessages(null);
    }
}
