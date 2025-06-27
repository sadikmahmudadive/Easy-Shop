package com.example.easyshop.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.easyshop.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddCardBottomSheetDialog extends BottomSheetDialogFragment {

    public interface OnCardAddListener {
        void onCardAdded(String name, String number, String expiry, String cvv, boolean isDefault);
    }

    private OnCardAddListener cardAddListener;

    public AddCardBottomSheetDialog(OnCardAddListener listener) {
        this.cardAddListener = listener;
    }

    public AddCardBottomSheetDialog() {} // Needed for fragment manager

    public void setOnCardAddListener(OnCardAddListener listener) {
        this.cardAddListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_card, container, false);

        EditText etName = view.findViewById(R.id.et_card_name);
        EditText etNumber = view.findViewById(R.id.et_card_number);
        EditText etExpiry = view.findViewById(R.id.et_card_expiry);
        EditText etCvv = view.findViewById(R.id.et_card_cvv);
        CheckBox cbDefault = view.findViewById(R.id.cb_set_default);
        Button btnAdd = view.findViewById(R.id.btn_add_card);

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String number = etNumber.getText().toString().trim();
            String expiry = etExpiry.getText().toString().trim();
            String cvv = etCvv.getText().toString().trim();
            boolean isDefault = cbDefault.isChecked();

            if (name.isEmpty() || number.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cardAddListener != null) {
                cardAddListener.onCardAdded(name, number, expiry, cvv, isDefault);
            }
            dismiss();
        });

        return view;
    }
}