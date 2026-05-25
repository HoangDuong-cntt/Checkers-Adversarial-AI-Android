package com.androidproject.checkersgame.ui;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.androidproject.checkersgame.R;
import com.androidproject.checkersgame.firebase.FirebaseRealtimeManager;
import com.androidproject.checkersgame.models.GameResult;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private HistoryAdapter adapter;
    private List<GameResult> historyList = new ArrayList<>();
    private FirebaseRealtimeManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        rvHistory = findViewById(R.id.rvHistory);
        Button btnBack = findViewById(R.id.btnBack);

        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(historyList);
        rvHistory.setAdapter(adapter);

        firebaseManager = new FirebaseRealtimeManager();
        firebaseManager.readHistory((results, keys) -> {
            historyList.clear();
            historyList.addAll(results);
            adapter.notifyDataSetChanged();
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
