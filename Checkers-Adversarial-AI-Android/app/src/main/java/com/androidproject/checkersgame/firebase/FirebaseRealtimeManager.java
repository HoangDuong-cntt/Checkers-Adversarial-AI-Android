package com.androidproject.checkersgame.firebase;

import android.util.Log;
import com.androidproject.checkersgame.ai.MinimaxSolver;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseRealtimeManager {
    private static final String TAG = "FirebaseRealtime";
    private DatabaseReference mDatabase;

    public void startListeningToAIConfig() {
        // Kết nối Realtime Database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ai_config");

        // Lắng nghe biến động dữ liệu theo thời gian thực (Realtime)
        mDatabase.addValueEventListener(new ValueEventListener() {
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
}