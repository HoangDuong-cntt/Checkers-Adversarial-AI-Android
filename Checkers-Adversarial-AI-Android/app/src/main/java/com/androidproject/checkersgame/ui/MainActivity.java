package com.androidproject.checkersgame.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    }
}
