package com.example.wastereborn.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wastereborn.R;
import com.example.wastereborn.model.Reward;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.RewardViewHolder> {
    
    private List<Reward> rewards;
    private OnRewardClickListener listener;

    public interface OnRewardClickListener {
        void onRewardClick(Reward reward);
        void onRedeemClick(Reward reward);
    }

    public RewardAdapter(List<Reward> rewards, OnRewardClickListener listener) {
        this.rewards = rewards;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward, parent, false);
        return new RewardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        Reward reward = rewards.get(position);
        holder.bind(reward);
    }

    @Override
    public int getItemCount() {
        return rewards.size();
    }

    class RewardViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView iconText;
        private TextView typeText;
        private TextView descriptionText;
        private TextView pointsText;
        private TextView dateText;
        private Button redeemButton;

        public RewardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_reward);
            iconText = itemView.findViewById(R.id.text_icon);
            typeText = itemView.findViewById(R.id.text_type);
            descriptionText = itemView.findViewById(R.id.text_description);
            pointsText = itemView.findViewById(R.id.text_points);
            dateText = itemView.findViewById(R.id.text_date);
            redeemButton = itemView.findViewById(R.id.btn_redeem);

            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRewardClick(rewards.get(getAdapterPosition()));
                }
            });

            redeemButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRedeemClick(rewards.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Reward reward) {
            iconText.setText(reward.getTypeIcon());
            typeText.setText(reward.getTypeDisplayName());
            descriptionText.setText(reward.getDescription());
            pointsText.setText("+" + reward.getPoints() + " pts");
            dateText.setText(formatDate(reward.getEarnedDate()));

            // Show/hide redeem button based on reward status
            if (reward.isValid() && reward.getIsRedeemed() != null && !reward.getIsRedeemed()) {
                redeemButton.setVisibility(View.VISIBLE);
                redeemButton.setText("Available");
                redeemButton.setEnabled(true);
                cardView.setCardBackgroundColor(Color.WHITE);
            } else if (reward.getIsRedeemed() != null && reward.getIsRedeemed()) {
                redeemButton.setVisibility(View.VISIBLE);
                redeemButton.setText("Redeemed");
                redeemButton.setEnabled(false);
                cardView.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
            } else {
                redeemButton.setVisibility(View.GONE);
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            // Set points color based on amount
            if (reward.getPoints() != null) {
                if (reward.getPoints() >= 100) {
                    pointsText.setTextColor(Color.parseColor("#4CAF50")); // Green for high points
                } else if (reward.getPoints() >= 50) {
                    pointsText.setTextColor(Color.parseColor("#FF9800")); // Orange for medium points
                } else {
                    pointsText.setTextColor(Color.parseColor("#2196F3")); // Blue for low points
                }
            }
        }

        private String formatDate(String dateString) {
            if (dateString == null) return "";
            
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                Date date = inputFormat.parse(dateString);
                return outputFormat.format(date);
            } catch (ParseException e) {
                return dateString;
            }
        }
    }
}
