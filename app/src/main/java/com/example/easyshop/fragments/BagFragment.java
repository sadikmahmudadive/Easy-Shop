package com.example.easyshop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easyshop.R;
import com.example.easyshop.models.BagProduct;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class BagFragment extends Fragment {

    private RecyclerView recyclerView;
    private BagAdapter bagAdapter;
    private TextView tvTotalAmount;
    private EditText etPromoCode;
    private Button btnCheckout;
    private ImageButton btnApplyPromo;

    private List<BagProduct> bagProducts = new ArrayList<>();
    private int totalAmount = 0;

    // Firebase
    private DatabaseReference cartRef;
    private ValueEventListener cartListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bag, container, false);

        recyclerView = root.findViewById(R.id.recycler_bag);
        tvTotalAmount = root.findViewById(R.id.tv_total_amount);
        etPromoCode = root.findViewById(R.id.et_promo_code);
        btnApplyPromo = root.findViewById(R.id.btn_apply_promo);
        btnCheckout = root.findViewById(R.id.btn_checkout);

        bagAdapter = new BagAdapter(bagProducts, new BagAdapter.OnBagActionListener() {
            @Override
            public void onQuantityChanged(int position, int newQuantity) {
                BagProduct p = bagProducts.get(position);
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String cartItemId = p.getProductId() + "_" + p.getSize();
                DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("cart")
                        .child(userId).child(cartItemId);
                p.setQuantity(newQuantity);
                itemRef.setValue(p);
                recalculateTotal();
            }

            @Override
            public void onRemove(int position) {
                BagProduct p = bagProducts.get(position);
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String cartItemId = p.getProductId() + "_" + p.getSize();
                DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("cart")
                        .child(userId).child(cartItemId);
                itemRef.removeValue();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(bagAdapter);

        // Listen to cart changes in Firebase for this user
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference("cart").child(userId);

        cartListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bagProducts.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    BagProduct p = snap.getValue(BagProduct.class);
                    if (p != null) bagProducts.add(p);
                }
                bagAdapter.notifyDataSetChanged();
                recalculateTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        cartRef.addValueEventListener(cartListener);

        btnApplyPromo.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Promo code applied: " + etPromoCode.getText().toString(), Toast.LENGTH_SHORT).show();
        });

        btnCheckout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Proceed to checkout!", Toast.LENGTH_SHORT).show();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (cartRef != null && cartListener != null) {
            cartRef.removeEventListener(cartListener);
        }
    }

    private void recalculateTotal() {
        int sum = 0;
        for (BagProduct p : bagProducts)
            sum += p.getPrice() * p.getQuantity();
        totalAmount = sum;
        tvTotalAmount.setText(totalAmount + "$");
    }

    public static class BagAdapter extends RecyclerView.Adapter<BagAdapter.BagViewHolder> {
        public interface OnBagActionListener {
            void onQuantityChanged(int position, int newQuantity);
            void onRemove(int position);
        }

        private final List<BagProduct> products;
        private final OnBagActionListener listener;

        public BagAdapter(List<BagProduct> products, OnBagActionListener listener) {
            this.products = products;
            this.listener = listener;
        }

        @NonNull
        @Override
        public BagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bag_product, parent, false);
            return new BagViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull BagViewHolder holder, int position) {
            BagProduct p = products.get(position);
            holder.tvName.setText(p.getName());
            holder.tvColor.setText("Color: " + p.getColor());
            holder.tvSize.setText("Size: " + p.getSize());
            holder.tvQuantity.setText(String.valueOf(p.getQuantity()));
            holder.tvPrice.setText(p.getPrice() + "$");

            Glide.with(holder.imgProduct.getContext())
                    .load(p.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.imgProduct);

            holder.btnDecrease.setOnClickListener(v -> {
                if (p.getQuantity() > 1) {
                    int newQty = p.getQuantity() - 1;
                    holder.tvQuantity.setText(String.valueOf(newQty));
                    if (listener != null) listener.onQuantityChanged(position, newQty);
                }
            });

            holder.btnIncrease.setOnClickListener(v -> {
                int newQty = p.getQuantity() + 1;
                holder.tvQuantity.setText(String.valueOf(newQty));
                if (listener != null) listener.onQuantityChanged(position, newQty);
            });

            holder.btnMore.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(holder.btnMore.getContext(), holder.btnMore);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_bag_item, popup.getMenu());
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.menu_add_to_favorites) {
                        Toast.makeText(holder.btnMore.getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                        return true;
                    } else if (item.getItemId() == R.id.menu_delete_from_list) {
                        if (listener != null) listener.onRemove(holder.getAdapterPosition());
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        public static class BagViewHolder extends RecyclerView.ViewHolder {
            ImageView imgProduct;
            TextView tvName, tvColor, tvSize, tvQuantity, tvPrice;
            ImageButton btnDecrease, btnIncrease, btnMore;
            public BagViewHolder(@NonNull View itemView) {
                super(itemView);
                imgProduct = itemView.findViewById(R.id.img_product);
                tvName = itemView.findViewById(R.id.tv_product_name);
                tvColor = itemView.findViewById(R.id.tv_product_color);
                tvSize = itemView.findViewById(R.id.tv_product_size);
                tvQuantity = itemView.findViewById(R.id.tv_quantity);
                tvPrice = itemView.findViewById(R.id.tv_product_price);
                btnDecrease = itemView.findViewById(R.id.btn_decrease);
                btnIncrease = itemView.findViewById(R.id.btn_increase);
                btnMore = itemView.findViewById(R.id.btn_more);
            }
        }
    }
}