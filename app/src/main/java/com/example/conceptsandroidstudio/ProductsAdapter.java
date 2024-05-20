package com.example.conceptsandroidstudio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {

    private List<Product> productList;
    private OnAddToCartClickListener onAddToCartClickListener;

    public interface OnAddToCartClickListener {
        void onAddToCartClick(Product product);
    }

    public void setOnAddToCartClickListener(OnAddToCartClickListener listener) {
        this.onAddToCartClickListener = listener;
    }

    public ProductsAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
        holder.fabAddToCart.setOnClickListener(v -> {
            if (onAddToCartClickListener != null) {
                onAddToCartClickListener.onAddToCartClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        private TextView nombreTextView;
        private TextView precioTextView;
        private ImageView fotoImageView;
        private FloatingActionButton fabAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.nombreProducto);
            precioTextView = itemView.findViewById(R.id.precioProducto);
            fotoImageView = itemView.findViewById(R.id.imageProducto);
            fabAddToCart = itemView.findViewById(R.id.fabAddToCart);
        }

        public void bind(Product product) {
            nombreTextView.setText(product.getModelo() + " " + product.getMarca());
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
            String precioFormateado = numberFormat.format(product.getPrecio());
            precioTextView.setText(precioFormateado);

            if (product.getFotosUrls() != null && !product.getFotosUrls().isEmpty()) {
                String primeraUrlFoto = product.getFotosUrls().get(0);
                Glide.with(itemView.getContext())
                        .load(primeraUrlFoto)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.placeholder_image)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .fitCenter())
                        .into(fotoImageView);
            }
        }
    }
}
