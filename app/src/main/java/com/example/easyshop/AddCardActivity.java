package com.example.easyshop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easyshop.models.PaymentMethod;

public class AddCardActivity extends AppCompatActivity {
    private EditText etCardNumber, etCardHolder, etCardExpiry;
    private Button btnSaveCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        etCardNumber = findViewById(R.id.et_card_number);
        etCardHolder = findViewById(R.id.et_card_holder);
        etCardExpiry = findViewById(R.id.et_card_expiry);
        btnSaveCard = findViewById(R.id.btn_save_card);

        btnSaveCard.setOnClickListener(v -> {
            String cardNumber = etCardNumber.getText().toString().trim();
            String cardHolder = etCardHolder.getText().toString().trim();
            String expiry = etCardExpiry.getText().toString().trim();

            if (cardNumber.length() < 12 || cardHolder.isEmpty() || expiry.isEmpty()) {
                etCardNumber.setError("Invalid");
                return;
            }

            String last4 = cardNumber.length() > 4 ? cardNumber.substring(cardNumber.length() - 4) : cardNumber;
            PaymentMethod card = new PaymentMethod(
                    (int) System.currentTimeMillis(),
                    PaymentMethod.PaymentType.CARD,
                    "Mastercard", // or detect
                    last4,
                    "**** **** **** " + last4,
                    R.drawable.ic_mastercard
            );

            //Intent result = new Intent();
            //result.putExtra("card", card);
            //setResult(Activity.RESULT_OK, result);
            //finish();
        });
    }
}