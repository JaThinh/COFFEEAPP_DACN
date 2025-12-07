package com.example.myapplication.UI.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.databinding.DialogAddRatingBinding;

public class RatingDialogFragment extends DialogFragment {

    private DialogAddRatingBinding binding;
    private static final String ARG_PRODUCT_ID = "productId";
    private static final String ARG_PRODUCT_NAME = "productName";

    private String productId;
    private String productName;

    public interface RatingDialogListener {
        void onSubmitRating(String productId, float rating, String comment);
    }

    public static RatingDialogFragment newInstance(String productId, String productName) {
        RatingDialogFragment fragment = new RatingDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productId);
        args.putString(ARG_PRODUCT_NAME, productName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getString(ARG_PRODUCT_ID);
            productName = getArguments().getString(ARG_PRODUCT_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddRatingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvProductName.setText(productName);

        binding.btnCancel.setOnClickListener(v -> dismiss());

        binding.btnSubmit.setOnClickListener(v -> {
            float rating = binding.ratingBar.getRating();
            String comment = binding.etComment.getText().toString().trim();

            if (rating == 0) {
                Toast.makeText(getContext(), "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show();
                return;
            }

            RatingDialogListener listener = (RatingDialogListener) getActivity();
            if (listener != null) {
                listener.onSubmitRating(productId, rating, comment);
            }
            dismiss();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
