package com.example.easyshop.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.easyshop.R;

import com.example.easyshop.models.PromoCode;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class PromoCodeBottomSheetDialog extends BottomSheetDialogFragment {

    private EditText etEnterPromo;
    private ImageButton btnApplyInput;
    private RecyclerView rvPromoList;
    private PromoCodeAdapter adapter;
    private List<PromoCode> promoCodeList;
    private OnPromoSelectedListener listener;

    // Pass promo codes and listener via factory method
    public static PromoCodeBottomSheetDialog newInstance(List<PromoCode> codes, OnPromoSelectedListener listener) {
        PromoCodeBottomSheetDialog dialog = new PromoCodeBottomSheetDialog();
        dialog.promoCodeList = codes;
        dialog.listener = listener;
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_promo_code, container, false);

        etEnterPromo = view.findViewById(R.id.et_enter_promo);
        btnApplyInput = view.findViewById(R.id.btn_apply_promo_input);
        rvPromoList = view.findViewById(R.id.rv_promo_list);

        // Setup promo code list
        rvPromoList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PromoCodeAdapter(promoCodeList, code -> {
            if (listener != null) listener.onPromoSelected(code);
            dismiss();
        });
        rvPromoList.setAdapter(adapter);

        btnApplyInput.setOnClickListener(v -> {
            String code = etEnterPromo.getText().toString().trim();
            if (!TextUtils.isEmpty(code)) {
                if (listener != null) listener.onPromoSelected(new PromoCode(code, "Manual", "", 0, 0, null));
                dismiss();
            } else {
                etEnterPromo.setError("Enter a promo code");
            }
        });

        return view;
    }

    public interface OnPromoSelectedListener {
        void onPromoSelected(PromoCode code);
    }

    // Adapter for promo code items
    public static class PromoCodeAdapter extends RecyclerView.Adapter<PromoCodeAdapter.PromoViewHolder> {
        private final List<PromoCode> promoCodes;
        private final OnPromoItemApplyListener listener;

        public PromoCodeAdapter(List<PromoCode> promoCodes, OnPromoItemApplyListener listener) {
            this.promoCodes = promoCodes;
            this.listener = listener;
        }

        @NonNull
        @Override
        public PromoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promo_code, parent, false);
            return new PromoViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull PromoViewHolder holder, int position) {
            PromoCode promo = promoCodes.get(position);
            holder.tvDiscountPercent.setText(String.valueOf(promo.getDiscountPercent()));
            holder.tvPromoTitle.setText(promo.getTitle());
            holder.tvPromoCode.setText(promo.getCode());
            holder.tvPromoTime.setText(promo.getDaysLeft() + " days remaining");
            // Optionally load promo.getImageUrl() if present.
            if (promo.getImageUrl() != null && !promo.getImageUrl().isEmpty()) {
                holder.imgPromo.setVisibility(View.VISIBLE);
                // Load image using Glide/Picasso if needed
                // Glide.with(holder.imgPromo.getContext()).load(promo.getImageUrl()).into(holder.imgPromo);
            } else {
                holder.imgPromo.setVisibility(View.GONE);
            }
            // Set background for discount area depending on color if needed.

            holder.btnApply.setOnClickListener(v -> {
                if (listener != null) listener.onPromoApply(promo);
            });
        }

        @Override
        public int getItemCount() {
            return promoCodes != null ? promoCodes.size() : 0;
        }

        public static class PromoViewHolder extends RecyclerView.ViewHolder {
            TextView tvDiscountPercent, tvPromoTitle, tvPromoCode, tvPromoTime;
            ImageView imgPromo;
            Button btnApply;
            public PromoViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDiscountPercent = itemView.findViewById(R.id.tv_discount_percent);
                tvPromoTitle = itemView.findViewById(R.id.tv_promo_title);
                tvPromoCode = itemView.findViewById(R.id.tv_promo_code);
                tvPromoTime = itemView.findViewById(R.id.tv_promo_time);
                imgPromo = itemView.findViewById(R.id.img_promo);
                btnApply = itemView.findViewById(R.id.btn_apply_promo);
            }
        }

        public interface OnPromoItemApplyListener {
            void onPromoApply(PromoCode code);
        }
    }
}