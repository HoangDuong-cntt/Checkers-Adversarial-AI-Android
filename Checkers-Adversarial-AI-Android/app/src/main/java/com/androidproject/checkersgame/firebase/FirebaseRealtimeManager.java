package com.androidproject.checkersgame.firebase;

import android.util.Log;
import androidx.annotation.NonNull;
import com.androidproject.checkersgame.ai.MinimaxSolver;
import com.androidproject.checkersgame.models.GameResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirebaseRealtimeManager {
    private static final String TAG = "FirebaseRealtime";
    private DatabaseReference mDatabase;

    public FirebaseRealtimeManager() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void startListeningToAIConfig() {
        DatabaseReference aiConfigRef = mDatabase.child("ai_config");
        aiConfigRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("weight_piece")) {
                    Long weightPiece = dataSnapshot.child("weight_piece").getValue(Long.class);
                    if (weightPiece != null) {
                        MinimaxSolver.WEIGHT_PIECE = weightPiece.intValue();
                        Log.d(TAG, "⚡ Realtime Update -> WEIGHT_PIECE: " + weightPiece);
                    }
                }
                if (dataSnapshot.hasChild("weight_king")) {
                    Long weightKing = dataSnapshot.child("weight_king").getValue(Long.class);
                    if (weightKing != null) {
                        MinimaxSolver.WEIGHT_KING = weightKing.intValue();
                        Log.d(TAG, "⚡ Realtime Update -> WEIGHT_KING: " + weightKing);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Lỗi kết nối Firebase Realtime:", databaseError.toException());
            }
        });
    }

    public void saveGameResult(GameResult result) {
        String key = mDatabase.child("results").push().getKey();
        if (key != null) {
            mDatabase.child("results").child(key).setValue(result)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Thành tích đã được lưu!"))
                    .addOnFailureListener(e -> Log.e(TAG, "Lỗi khi lưu thành tích", e));
        }
    }

    public interface DataStatus {
        void DataIsLoaded(List<GameResult> results, List<String> keys);
    }

    public void readHistory(final DataStatus dataStatus) {
        mDatabase.child("results").orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<GameResult> results = new ArrayList<>();
                List<String> keys = new ArrayList<>();
                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                    GameResult result = keyNode.getValue(GameResult.class);
                    results.add(result);
                }
                // Đảo ngược để hiện cái mới nhất lên đầu
                Collections.reverse(results);
                dataStatus.DataIsLoaded(results, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Lỗi khi đọc lịch sử:", databaseError.toException());
            }
        });
    }
}
