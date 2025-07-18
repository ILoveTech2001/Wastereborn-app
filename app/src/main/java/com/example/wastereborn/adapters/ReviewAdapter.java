package com.example.wastereborn.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wastereborn.R;
import com.example.wastereborn.model.Review;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    
    private List<Review> reviews;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView userNameText;
        private TextView commentText;
        private TextView dateText;
        private TextView helpfulText;
        private RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameText = itemView.findViewById(R.id.text_user_name);
            commentText = itemView.findViewById(R.id.text_comment);
            dateText = itemView.findViewById(R.id.text_date);
            helpfulText = itemView.findViewById(R.id.text_helpful);
            ratingBar = itemView.findViewById(R.id.rating_bar);
        }

        public void bind(Review review) {
            // Set user name (use email if name not available)
            if (review.getUserName() != null && !review.getUserName().isEmpty()) {
                userNameText.setText(review.getUserName());
            } else if (review.getUserEmail() != null) {
                // Show first part of email for privacy
                String email = review.getUserEmail();
                String displayName = email.substring(0, Math.min(email.indexOf('@'), 3)) + "***";
                userNameText.setText(displayName);
            } else {
                userNameText.setText("Anonymous User");
            }
            
            // Set rating
            if (review.getRating() != null) {
                ratingBar.setRating(review.getRating().floatValue());
            }
            
            // Set comment
            if (review.getComment() != null && !review.getComment().isEmpty()) {
                commentText.setText(review.getComment());
                commentText.setVisibility(View.VISIBLE);
            } else {
                commentText.setVisibility(View.GONE);
            }
            
            // Set date
            dateText.setText(formatDate(review.getDatePosted()));
            
            // Set helpful count
            if (review.getHelpfulCount() != null && review.getHelpfulCount() > 0) {
                helpfulText.setText(review.getHelpfulCount() + " found this helpful");
                helpfulText.setVisibility(View.VISIBLE);
            } else {
                helpfulText.setVisibility(View.GONE);
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
