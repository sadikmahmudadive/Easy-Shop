package com.example.easyshop;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Button;

import com.example.easyshop.models.DeliveryMethod;
import com.example.easyshop.models.PaymentMethod;
import com.example.easyshop.models.ShippingAddress;

import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity {

    private static final int REQ_ADD_CARD = 100;
    private static final int REQ_ADD_ADDRESS = 101;

    private TextView tvAddressName, tvAddressDetail, tvAddressCity;
    private TextView tvOrderTotal, tvDeliveryFee, tvSummaryTotal;
    private Button btnSubmitOrder;

    private ShippingAddress shippingAddress;
    private ArrayList<PaymentMethod> paymentMethods = new ArrayList<>();
    private PaymentMethod selectedPaymentMethod;
    private ArrayList<DeliveryMethod> deliveryMethods = new ArrayList<>();
    private DeliveryMethod selectedDeliveryMethod;

    private int orderTotal = 0; // <-- Store the order total passed from BagFragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Bind views
        tvAddressName = findViewById(R.id.tv_address_name);
        tvAddressDetail = findViewById(R.id.tv_address_detail);
        tvAddressCity = findViewById(R.id.tv_address_city);
        tvOrderTotal = findViewById(R.id.tv_order_total);
        tvDeliveryFee = findViewById(R.id.tv_delivery_fee);
        tvSummaryTotal = findViewById(R.id.tv_summary_total);
        btnSubmitOrder = findViewById(R.id.btn_submit_order);

        // Get the order total from the intent (sent by BagFragment)
        orderTotal = getIntent().getIntExtra("total", 0);

        // Dummy data for address, payment and delivery
        shippingAddress = new ShippingAddress(0, "Jane Doe", "3 Newbridge Court", "Chino Hills, CA 91709, United States");
        paymentMethods.add(new PaymentMethod(1, PaymentMethod.PaymentType.CARD, "Mastercard", "3947", "**** **** **** 3947", R.drawable.ic_mastercard));
        paymentMethods.add(new PaymentMethod(2, PaymentMethod.PaymentType.ONLINE, null, null, "Online Payment", R.drawable.ic_online_payment));
        paymentMethods.add(new PaymentMethod(3, PaymentMethod.PaymentType.COD, null, null, "Cash on delivery", R.drawable.ic_cod));
        selectedPaymentMethod = paymentMethods.get(0);
        deliveryMethods.add(new DeliveryMethod(1, "FedEx", R.drawable.ic_fedex, "2-3 days"));
        deliveryMethods.add(new DeliveryMethod(2, "SteadFast", R.drawable.ic_steadfast, "2-3 days"));
        deliveryMethods.add(new DeliveryMethod(3, "DHL", R.drawable.ic_dhl, "2-3 days"));
        selectedDeliveryMethod = deliveryMethods.get(0);

        // Set initial UI
        updateShippingAddressUI();
        updateOrderSummaryUI();

        findViewById(R.id.btn_change_address).setOnClickListener(v -> {
            startActivityForResult(new Intent(this, ShippingAddressesActivity.class), REQ_ADD_ADDRESS);
        });

        findViewById(R.id.btn_change_payment).setOnClickListener(v -> {
            startActivityForResult(new Intent(this, AddCardActivity.class), REQ_ADD_CARD);
        });

        btnSubmitOrder.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, SuccessSplashActivity.class);
            startActivity(intent);
            finish(); // Optionally finish checkout so user can't go back
        });
    }

    private void updateShippingAddressUI() {
        tvAddressName.setText(shippingAddress.name);
        tvAddressDetail.setText(shippingAddress.addressLine);
        tvAddressCity.setText(shippingAddress.city);
    }

    private void updateOrderSummaryUI() {
        int deliveryFee = 15;
        int summary = orderTotal + deliveryFee;
        tvOrderTotal.setText(orderTotal + "$");
        tvDeliveryFee.setText(deliveryFee + "$");
        tvSummaryTotal.setText(summary + "$");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQ_ADD_CARD) {
                PaymentMethod card = (PaymentMethod) data.getSerializableExtra("card");
                if (card != null) {
                    paymentMethods.add(card);
                    selectedPaymentMethod = card;
                    // Update UI accordingly
                }
            } else if (requestCode == REQ_ADD_ADDRESS) {
                ShippingAddress address = (ShippingAddress) data.getSerializableExtra("address");
                if (address != null) {
                    shippingAddress = address;
                    updateShippingAddressUI();
                }
            }
        }
    }
}