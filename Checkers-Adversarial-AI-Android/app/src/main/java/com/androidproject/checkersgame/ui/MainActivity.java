package com.androidproject.checkersgame.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.androidproject.checkersgame.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnPlayAI = findViewById(R.id.btnPlayAI);
        Button btnPlayPvP = findViewById(R.id.btnPlayPvP);
        Button btnHistory = findViewById(R.id.btnHistory);
        Button btnRules = findViewById(R.id.btnRules);

        // Xử lý sự kiện khi bấm nút chơi với AI
        btnPlayAI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("mode", "AI");
                startActivity(intent);
            }
        });

        // Xử lý sự kiện khi bấm nút chơi 2 người
        btnPlayPvP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("mode", "PVP");
                startActivity(intent);
            }
        });

        // Xử lý sự kiện khi bấm xem lịch sử
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        // Xử lý sự kiện khi bấm xem hướng dẫn
        btnRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRulesDialog();
            }
        });
    }

    private void showRulesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Luật Chơi Cờ Đam (Checkers)");
        builder.setMessage("1. Di chuyển: Quân thường chỉ có thể di chuyển chéo tiến lên 1 ô trống.\n\n" +
                "2. Ăn quân: Nếu có quân đối phương nằm chéo trước mặt và ô phía sau nó trống, bạn bắt đầu nhảy qua để ăn. Việc ăn quân là BẮT BUỘC.\n\n" +
                "3. Nhảy liên tiếp (Multi-jump): Sau khi ăn một quân, nếu quân đó có thể ăn tiếp quân khác, nó sẽ tự động nhảy để ăn cho đến khi hết nước ăn.\n\n" +
                "4. Quân Vua: Khi một quân đi đến hàng cuối cùng của đối phương, nó sẽ trở thành Vua. Vua có thể di chuyển và ăn quân theo hướng chéo cả tiến và lùi.\n\n" +
                "5. Kết thúc: Bạn thắng khi đối phương mất hết quân hoặc không còn nước đi hợp lệ nào.");
        builder.setPositiveButton("Đã hiểu", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
