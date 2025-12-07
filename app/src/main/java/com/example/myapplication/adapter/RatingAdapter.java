// Vị trí file: D:/App/COFFEE-main/app/src/main/java/com/example/myapplication/adapter/RatingAdapter.java

package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemRatingBinding;
import com.example.myapplication.model.Rating;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {

    private final Context context;
    private final List<Rating> ratingList;

    public RatingAdapter(Context context, List<Rating> ratingList) {
        this.context = context;
        this.ratingList = ratingList;
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng LayoutInflater từ context của parent để đảm bảo theme được áp dụng đúng
        ItemRatingBinding binding = ItemRatingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RatingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        Rating rating = ratingList.get(position);
        if (rating != null) {
            // Gọi phương thức bind của ViewHolder để gán dữ liệu, giúp code sạch sẽ hơn
            holder.bind(rating);
        }
    }

    @Override
    public int getItemCount() {
        // Kiểm tra list có null không để tránh lỗi
        return ratingList != null ? ratingList.size() : 0;
    }

    public static class RatingViewHolder extends RecyclerView.ViewHolder {
        private final ItemRatingBinding binding;

        public RatingViewHolder(ItemRatingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * Phương thức này gán dữ liệu từ một đối tượng Rating vào các view tương ứng.
         * @param rating Đối tượng chứa dữ liệu đánh giá.
         */
        public void bind(Rating rating) {
            // Giả sử các ID trong file item_rating.xml là tvUserName, tvComment, ratingBarItem, tvRatingDate
            binding.tvUserName.setText(rating.getUserName());
            binding.tvComment.setText(rating.getComment());

            // ===== SỬA LỖI TẠI ĐÂY =====
            // Sử dụng đúng tên phương thức là getStars() như đã định nghĩa trong file Rating.java
            binding.ratingBarItem.setRating(rating.getStars());
            // ===========================

            // Định dạng và hiển thị ngày tháng đánh giá
            if (rating.getTimestamp() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                binding.tvRatingDate.setText(sdf.format(new Date(rating.getTimestamp())));
            } else {
                binding.tvRatingDate.setText(""); // Ẩn đi nếu không có dữ liệu ngày
            }
        }
    }
}