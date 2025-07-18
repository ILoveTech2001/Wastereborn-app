package com.example.wastereborn.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wastereborn.R;
import com.example.wastereborn.model.Notification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    
    private List<Notification> notifications;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
        void onNotificationLongClick(Notification notification);
    }

    public NotificationAdapter(List<Notification> notifications, OnNotificationClickListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView iconText;
        private TextView titleText;
        private TextView messageText;
        private TextView timeText;
        private View unreadIndicator;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_notification);
            iconText = itemView.findViewById(R.id.text_icon);
            titleText = itemView.findViewById(R.id.text_title);
            messageText = itemView.findViewById(R.id.text_message);
            timeText = itemView.findViewById(R.id.text_time);
            unreadIndicator = itemView.findViewById(R.id.unread_indicator);

            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNotificationClick(notifications.get(getAdapterPosition()));
                }
            });

            cardView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onNotificationLongClick(notifications.get(getAdapterPosition()));
                }
                return true;
            });
        }

        public void bind(Notification notification) {
            iconText.setText(notification.getTypeIcon());
            titleText.setText(notification.getTitle());
            messageText.setText(notification.getMessage());
            timeText.setText(formatTime(notification.getCreatedAt()));

            // Set type color
            iconText.setTextColor(notification.getTypeColor());

            // Show/hide unread indicator
            if (notification.isUnread()) {
                unreadIndicator.setVisibility(View.VISIBLE);
                cardView.setCardBackgroundColor(Color.parseColor("#F8F9FA"));
                titleText.setTextColor(Color.parseColor("#212121"));
            } else {
                unreadIndicator.setVisibility(View.GONE);
                cardView.setCardBackgroundColor(Color.WHITE);
                titleText.setTextColor(Color.parseColor("#757575"));
            }
        }

        private String formatTime(String dateString) {
            if (dateString == null) return "";
            
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
                Date date = inputFormat.parse(dateString);
                return outputFormat.format(date);
            } catch (ParseException e) {
                return dateString;
            }
        }
    }
}
