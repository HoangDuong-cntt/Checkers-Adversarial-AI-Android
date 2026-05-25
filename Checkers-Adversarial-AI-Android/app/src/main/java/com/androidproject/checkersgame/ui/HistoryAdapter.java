package com.androidproject.checkersgame.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.androidproject.checkersgame.R;
import com.androidproject.checkersgame.models.GameResult;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<GameResult> historyList;

    public HistoryAdapter(List<GameResult> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameResult result = historyList.get(position);
        holder.tvPlayerName.setText(result.playerName);
        holder.tvGameResult.setText(result.result);
        holder.tvMode.setText("Chế độ: " + result.mode);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.tvTimestamp.setText(sdf.format(new Date(result.timestamp)));

        // Đổi màu kết quả
        if ("Thắng".equals(result.result)) {
            holder.tvGameResult.setTextColor(0xFF9BBC6A); // Xanh lá
        } else if ("Thua".equals(result.result)) {
            holder.tvGameResult.setTextColor(0xFFFF5252); // Đỏ
        } else {
            holder.tvGameResult.setTextColor(0xFFFFFFFF); // Trắng
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlayerName, tvGameResult, tvMode, tvTimestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlayerName = itemView.findViewById(R.id.tvPlayerName);
            tvGameResult = itemView.findViewById(R.id.tvGameResult);
            tvMode = itemView.findViewById(R.id.tvMode);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}
