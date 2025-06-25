package com.example.easyshop.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.easyshop.R;
import com.example.easyshop.models.Product;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class SelectSizeBottomSheet extends BottomSheetDialogFragment {

    public interface OnSizeSelectedListener {
        void onSizeSelected(String size);
    }

    private static final List<String> SIZES = Arrays.asList("XS", "S", "M", "L", "XL");

    private OnSizeSelectedListener listener;
    private Product product;
    private String selectedSize = null;

    public static SelectSizeBottomSheet newInstance(Product product) {
        SelectSizeBottomSheet sheet = new SelectSizeBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable("product", product);
        sheet.setArguments(args);
        return sheet;
    }

    public void setOnSizeSelectedListener(OnSizeSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.select_size_bottom_sheet, container, false);

        if (getArguments() != null) {
            product = (Product) getArguments().getSerializable("product");
        }

        GridLayout gridLayout = v.findViewById(R.id.grid_size_options);
        Button btnAddToFavourites = v.findViewById(R.id.btn_add_to_favourites);
        TextView tvSelectSizeTitle = v.findViewById(R.id.tv_select_size_title);

        // Dynamically add size buttons
        for (int i = 0; i < SIZES.size(); i++) {
            String size = SIZES.get(i);
            Button sizeBtn = new Button(requireContext());
            sizeBtn.setText(size);
            sizeBtn.setBackgroundResource(R.drawable.bg_size_selector);
            sizeBtn.setAllCaps(false);
            sizeBtn.setTextColor(getResources().getColor(R.color.black));
            sizeBtn.setTextSize(16);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(i % 3, 1f);
            params.setMargins(8, 8, 8, 8);
            sizeBtn.setLayoutParams(params);

            sizeBtn.setOnClickListener(view -> {
                selectedSize = size;
                // Highlight selection
                for (int j = 0; j < gridLayout.getChildCount(); j++) {
                    View child = gridLayout.getChildAt(j);
                    child.setBackgroundResource(R.drawable.bg_size_selector);
                }
                sizeBtn.setBackgroundResource(R.drawable.bg_size_selector_selected);
            });

            gridLayout.addView(sizeBtn);
        }

        btnAddToFavourites.setOnClickListener(view -> {
            if (selectedSize != null) {
                if (listener != null) listener.onSizeSelected(selectedSize);
                dismiss();
            } else {
                tvSelectSizeTitle.setError("Please select a size");
            }
        });

        return v;
    }
}