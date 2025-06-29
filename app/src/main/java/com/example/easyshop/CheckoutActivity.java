package com.example.easyshop;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.easyshop.models.DeliveryMethod;
import com.example.easyshop.models.PaymentMethod;
import com.example.easyshop.models.ShippingAddress;
import com.example.easyshop.models.CartItem;
import com.example.easyshop.models.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    private int orderTotal = 0; // Store the order total passed from BagFragment
    private List<CartItem> cartItems; // Pass this from BagFragment or CartManager

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

        orderTotal = getIntent().getIntExtra("total", 0);
        cartItems = (List<CartItem>) getIntent().getSerializableExtra("cart_items");

        // Defensive: Ensure cartItems is never null to prevent upload failures
        if (cartItems == null) cartItems = new ArrayList<>();

        // Dummy data for address, payment and delivery (replace with real data in production)
        shippingAddress = new ShippingAddress(0, "Jane Doe", "3 Newbridge Court", "Chino Hills, CA 91709, United States");
        paymentMethods.add(new PaymentMethod(1, PaymentMethod.PaymentType.CARD, "Mastercard", "3947", "**** **** **** 3947", R.drawable.ic_mastercard));
        paymentMethods.add(new PaymentMethod(2, PaymentMethod.PaymentType.ONLINE, null, null, "Online Payment", R.drawable.ic_online_payment));
        paymentMethods.add(new PaymentMethod(3, PaymentMethod.PaymentType.COD, null, null, "Cash on delivery", R.drawable.ic_cod));
        selectedPaymentMethod = paymentMethods.get(0);
        deliveryMethods.add(new DeliveryMethod(1, "FedEx", R.drawable.ic_fedex, "2-3 days"));
        deliveryMethods.add(new DeliveryMethod(2, "SteadFast", R.drawable.ic_steadfast, "2-3 days"));
        deliveryMethods.add(new DeliveryMethod(3, "DHL", R.drawable.ic_dhl, "2-3 days"));
        selectedDeliveryMethod = deliveryMethods.get(0);

        updateShippingAddressUI();
        updateOrderSummaryUI();

        findViewById(R.id.btn_change_address).setOnClickListener(v -> {
            startActivityForResult(new Intent(this, ShippingAddressesActivity.class), REQ_ADD_ADDRESS);
        });

        findViewById(R.id.btn_change_payment).setOnClickListener(v -> {
            startActivityForResult(new Intent(this, AddCardActivity.class), REQ_ADD_CARD);
        });

        btnSubmitOrder.setOnClickListener(v -> {
            if (cartItems == null || cartItems.isEmpty()) {
                Toast.makeText(this, "Cart is empty. Cannot submit order.", Toast.LENGTH_SHORT).show();
                return;
            }
            // Defensive: Check every CartItem has product and quantity > 0
            for (CartItem item : cartItems) {
                if (item.getProduct() == null) {
                    Toast.makeText(this, "Cart item missing product details.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (item.getQuantity() <= 0) {
                    Toast.makeText(this, "Cart item with invalid quantity.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            saveOrderToFirebase();
        });
    }

    private void saveOrderToFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId == null) {
            Toast.makeText(this, "You must be logged in to place an order.", Toast.LENGTH_SHORT).show();
            return;
        }
        String orderId = "order" + System.currentTimeMillis();
        long orderDate = System.currentTimeMillis();
        int deliveryFee = 15;
        double totalPrice = orderTotal + deliveryFee;
        String trackingNumber = orderId + "5453455";
        String status = "Delivered"; // or "Processing"/"Cancelled"
        String shippingAddr = shippingAddress != null
                ? shippingAddress.addressLine + ", " + shippingAddress.city
                : "";
        String paymentMethodStr = selectedPaymentMethod != null ? selectedPaymentMethod.displayName : "";
        String deliveryMethodStr = selectedDeliveryMethod != null
                ? selectedDeliveryMethod.name + ", " + selectedDeliveryMethod.eta + ", " + deliveryFee + "$"
                : "";
        String discountStr = "10%, Personal promo code"; // Or retrieve if any

        // Defensive: Make sure cartItems is not null
        if (cartItems == null) cartItems = new ArrayList<>();

        // Log order before upload (for debugging)
        // Uncomment if needed: android.util.Log.d("OrderUpload", new com.google.gson.Gson().toJson(
        //      new Order(orderId, userId, cartItems, totalPrice, orderDate, trackingNumber, status, shippingAddr, paymentMethodStr, deliveryMethodStr, discountStr)
        // ));

        Order order = new Order(orderId, userId, cartItems, totalPrice, orderDate,
                trackingNumber, status, shippingAddr, paymentMethodStr, deliveryMethodStr, discountStr);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("orders").child(userId).child(orderId);
        ref.setValue(order)
                .addOnSuccessListener(aVoid -> {
                    Intent intent = new Intent(CheckoutActivity.this, SuccessSplashActivity.class);
                    intent.putExtra("cart_items", (Serializable) cartItems); // cartItems must be Serializable!
                    intent.putExtra("total", orderTotal);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CheckoutActivity.this, "Failed to place order: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void updateShippingAddressUI() {
        if (shippingAddress != null) {
            tvAddressName.setText(shippingAddress.name);
            tvAddressDetail.setText(shippingAddress.addressLine);
            tvAddressCity.setText(shippingAddress.city);
        }
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