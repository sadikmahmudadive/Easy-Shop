package com.example.easyshop.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.easyshop.R;
import com.example.easyshop.models.Review;
import java.util.ArrayList;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {
    private List<Review> allReviews;
    private List<Review> filteredReviews;
    private Context context;
    private boolean withPhotoFilter = false;

    public ReviewsAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.allReviews = reviews;
        this.filteredReviews = new ArrayList<>(reviews);
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = filteredReviews.get(position);
        holder.userName.setText(review.getUserName());
        holder.reviewContent.setText(review.getReviewText());
        holder.ratingBar.setRating(review.getRating());
        holder.reviewDate.setText(formatDate(review.getDate()));
        // Avatar loading (use placeholder if URL is empty)
        if (review.getUserAvatarUrl() != null && !review.getUserAvatarUrl().isEmpty()) {
            Glide.with(context)
                    .load(review.getUserAvatarUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .circleCrop()
                    .into(holder.userAvatar);
        } else {
            holder.userAvatar.setImageResource(R.drawable.placeholder_image);
        }

        // Helpful
        holder.helpfulText.setText(context.getString(R.string.helpful));
        holder.helpfulIcon.setColorFilter(Color.GRAY);

        // Photos
        holder.photosLayout.removeAllViews();
        if (review.getPhotoUrls() != null && !review.getPhotoUrls().isEmpty()) {
            holder.photosLayout.setVisibility(View.VISIBLE);
            for (String url : review.getPhotoUrls()) {
                ImageView imageView = new ImageView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        90, 90);
                params.setMargins(0, 0, 12, 0);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(context)
                        .load(url)
                        .placeholder(R.drawable.placeholder_image)
                        .into(imageView);
                holder.photosLayout.addView(imageView);
            }
        } else {
            holder.photosLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return filteredReviews.size();
    }

    public void setReviews(List<Review> reviews) {
        this.allReviews = reviews;
        filterWithPhotos(withPhotoFilter);
    }

    public void filterWithPhotos(boolean withPhoto) {
        this.withPhotoFilter = withPhoto;
        if (withPhoto) {
            filteredReviews = new ArrayList<>();
            for (Review r : allReviews) {
                if (r.getPhotoUrls() != null && !r.getPhotoUrls().isEmpty()) {
                    filteredReviews.add(r);
                }
            }
        } else {
            filteredReviews = new ArrayList<>(allReviews);
        }
        notifyDataSetChanged();
    }

    private String formatDate(long timestamp) {
        // e.g. June 21, 2025
        return DateFormat.format("MMMM d, yyyy", timestamp).toString();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar, helpfulIcon;
        TextView userName, reviewDate, reviewContent, helpfulText;
        RatingBar ratingBar;
        LinearLayout photosLayout;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.image_avatar);
            userName = itemView.findViewById(R.id.text_user_name);
            reviewDate = itemView.findViewById(R.id.text_review_date);
            reviewContent = itemView.findViewById(R.id.text_review_content);
            ratingBar = itemView.findViewById(R.id.rating_stars);
            photosLayout = itemView.findViewById(R.id.layout_review_photos);
            helpfulText = itemView.findViewById(R.id.text_helpful);
            helpfulIcon = itemView.findViewById(R.id.icon_helpful);
        }
    }
}