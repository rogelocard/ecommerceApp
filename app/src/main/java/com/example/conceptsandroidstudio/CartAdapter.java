package com.example.conceptsandroidstudio;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItemList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public CartAdapter(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImageView;
        private TextView tituloTextView;
        private TextView cantidadTextView;
        private TextView precioTotalTextView;
        private Button decreaseButton;
        private Button increaseButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImageView);
            tituloTextView = itemView.findViewById(R.id.tituloTextView);
            cantidadTextView = itemView.findViewById(R.id.cantidadTextView);
            precioTotalTextView = itemView.findViewById(R.id.precioTotalTextView);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
            increaseButton = itemView.findViewById(R.id.increaseButton);
        }

        public void bind(CartItem cartItem) {
            // Aquí asigna los valores de cartItem a las vistas correspondientes
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
            tituloTextView.setText(cartItem.getTituloProducto());
            cantidadTextView.setText(String.valueOf(cartItem.getCantidad()));
            precioTotalTextView.setText(numberFormat.format(cartItem.getPrecioTotal()));
            Glide.with(itemView)
                    .load(cartItem.getFoto())
                    .into(productImageView);

            decreaseButton.setOnClickListener(v -> {
                int cantidadActual = cartItem.getCantidad();
                if (cantidadActual > 1) {
                    updateCartItemQuantity(cartItem, cantidadActual - 1);
                } else if (cantidadActual == 1) {
                    deleteCartItem(cartItem);
                }
            });


            increaseButton.setOnClickListener(v -> {
                int cantidadActual = cartItem.getCantidad();
                updateCartItemQuantity(cartItem, cantidadActual + 1);
            });

        }

        private void updateCartItemQuantity(CartItem cartItem, int newQuantity) {
            String userId = mAuth.getCurrentUser().getUid();
            DocumentReference cartItemRef = db.collection("user").document(userId).collection("cart").document(cartItem.getId());

            cartItemRef.update(
                    "cantidad", newQuantity,
                    "precioTotal", cartItem.getPrecioUnitario() * newQuantity
            ).addOnSuccessListener(aVoid -> {
                cartItem.setCantidad(newQuantity);
                cartItem.setPrecioTotal(cartItem.getPrecioUnitario() * newQuantity);
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    notifyItemChanged(position);
                }
            }).addOnFailureListener(e -> {
                Log.d("CartAdapter", "Error updating document", e);
            });
        }
        private void deleteCartItem(CartItem cartItem) {
            String userId = mAuth.getCurrentUser().getUid();
            DocumentReference cartItemRef = db.collection("user").document(userId).collection("cart").document(cartItem.getId());

            cartItemRef.delete().addOnSuccessListener(aVoid -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    cartItemList.remove(position);
                    notifyItemRemoved(position);
                }
            }).addOnFailureListener(e -> {
                Log.d("CartAdapter", "Error deleting document", e);
            });
        }
    }
}

