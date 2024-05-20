package com.example.conceptsandroidstudio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;

import java.io.Serializable;

public class ProductDetailFragment extends DialogFragment {

    private Product product;
    private ViewFlipper photoFlipper;

    public ProductDetailFragment() {
        // Required empty public constructor
    }

    public static ProductDetailFragment newInstance(Product product) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("product", product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = (Product) getArguments().getSerializable("product");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        TextView marcaTextView = view.findViewById(R.id.marcaTextView);
        TextView modeloTextView = view.findViewById(R.id.modeloTextView);
        TextView precioTextView = view.findViewById(R.id.precioTextView);
        TextView procesadorTextView = view.findViewById(R.id.procesadorTextView);
        TextView ramTextView = view.findViewById(R.id.ramTextView);
        TextView romTextView = view.findViewById(R.id.romTextView);
        TextView colorTextView = view.findViewById(R.id.colorTextView);
        TextView sistemaOperativoTextView = view.findViewById(R.id.sistemaOperativoTextView);
        TextView pantallaTextView = view.findViewById(R.id.pantallaTextView);

        // Aquí puedes rellenar los TextViews con los datos del producto
        marcaTextView.setText(product.getMarca());
        modeloTextView.setText(product.getModelo());
        precioTextView.setText(String.valueOf(product.getPrecio()));
        procesadorTextView.setText(product.getProcesador());
        ramTextView.setText(product.getRam());
        romTextView.setText(product.getRom());
        colorTextView.setText(product.getColor());
        sistemaOperativoTextView.setText(product.getSistemaOperativo());
        pantallaTextView.setText(String.valueOf(product.getPantalla()));

        // Inicializar ViewFlipper y botones de navegación
        photoFlipper = view.findViewById(R.id.photoFlipper);
        ImageButton prevButton = view.findViewById(R.id.prevButton);
        ImageButton nextButton = view.findViewById(R.id.nextButton);

        // Configurar ViewFlipper con las imágenes del producto
        setupPhotoFlipper();

        // Manejar navegación del carrusel
        prevButton.setOnClickListener(v -> photoFlipper.showPrevious());
        nextButton.setOnClickListener(v -> photoFlipper.showNext());


        //Configurar botón de cerrar pantalla
        ImageButton closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dismiss());

        //Configurar botones de ir al carrito de compra o seguir comprando
        Button irAlCarritoButton = view.findViewById(R.id.irAlCarritoButton);
        Button seguirComprandoButton = view.findViewById(R.id.seguirComprandoButton);

        irAlCarritoButton.setOnClickListener(v -> {
            // Redirigir a ShoppingCartFragment
            dismiss();
            if (getActivity() instanceof MenuActivity) {
                ((MenuActivity) getActivity()).navigateToShoppingCart();
            }
        });

        seguirComprandoButton.setOnClickListener(v -> dismiss());

        return view;
    }
    private void setupPhotoFlipper() {
        if (product.getFotosUrls() != null) {
            for (String imageUrl : product.getFotosUrls()) {
                ImageView imageView = new ImageView(getContext());
                Glide.with(this)
                        .load(imageUrl)
                        .into(imageView);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                photoFlipper.addView(imageView);
            }
        }
    }
}
