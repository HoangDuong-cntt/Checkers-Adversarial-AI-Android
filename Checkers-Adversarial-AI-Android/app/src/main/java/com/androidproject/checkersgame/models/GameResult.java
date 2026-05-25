package com.androidproject.checkersgame.models;

public class GameResult {
    public String playerName;
    public String mode;
    public String result; // "Win", "Loss", "Draw"
    public long timestamp;

    public GameResult() {
        // Required for Firebase
    }

    public GameResult(String playerName, String mode, String result, long timestamp) {
        this.playerName = playerName;
        this.mode = mode;
        this.result = result;
        this.timestamp = timestamp;
    }
}
