package com.example.conceptsandroidstudio;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import coil.Coil;
import coil.request.ImageRequest;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "PortatilesActivity";

    private View rootView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProductsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment productsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductsFragment newInstance(String param1, String param2) {
        ProductsFragment fragment = new ProductsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Inicializar Firebase Firestore
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_products, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        List<MyItem> itemList = new ArrayList<>();
        itemList.add(new MyItem(R.drawable.image_portatil, "Pc"));
        itemList.add(new MyItem(R.drawable.image_tv, "Tv"));
        itemList.add(new MyItem(R.drawable.image_celular, "Celulares"));
        itemList.add(new MyItem(R.drawable.image_reloj, "Relojes"));
        itemList.add(new MyItem(R.drawable.image_accesorios, "Accesorios"));

        MyAdapter adapter = new MyAdapter(itemList);
        recyclerView.setAdapter(adapter);

        obtenerDatosDeFirestore();

        return rootView;
    }

    private void obtenerDatosDeFirestore(){
        // Obtener una referencia a la colección "portatiles" en Firebase Firestore
        CollectionReference portatilesRef = db.collection("portatiles");

        // Obtener los documentos de la colección "portatiles"
        portatilesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Product> productList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String marca = document.getString("marca");
                        String modelo = document.getString("modelo");
                        Long precio = document.getLong("precio");
                        List<String> fotosUrls = (List<String>) document.get("fotos");
                        String procesador = document.getString("procesador");
                        String ram = document.getString("ram");
                        String rom = document.getString("rom");
                        String color = document.getString("color");
                        String sistemaOperativo = document.getString("sistemaOpe");
                        Long pantallaLong = document.getLong("pantalla");
                        int pantalla = pantallaLong != null ? pantallaLong.intValue() : 0;
                        String id = document.getString("id");

                        // Crear un nuevo objeto Product con todos los campos
                        Product product = new Product(marca, modelo, precio, fotosUrls,
                                procesador, ram, rom, color,
                                sistemaOperativo, pantalla, id);

                        productList.add(product);
                    }
                    setupProductsRecyclerView(productList);
                    Context context = getContext();
                    if (context != null) {
                        Toast.makeText(context, "Datos cargados", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    Context context = getContext();
                    if (context != null) {
                        Toast.makeText(context, "Error obteniendo documentos", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
    private void setupProductsRecyclerView(List<Product> productList) {
        RecyclerView productsRecyclerView = rootView.findViewById(R.id.productsRecyclerView);
        productsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        ProductsAdapter productsAdapter = new ProductsAdapter(productList);
        productsAdapter.setOnAddToCartClickListener(product -> {
            // Mostrar detalles del producto
            showProductDetails(product);
            // O agregar directamente al carrito, dependiendo de tu flujo
            addToCart(product);
        });
        productsRecyclerView.setAdapter(productsAdapter);
    }

    private void showProductDetails(Product product) {
        ProductDetailFragment detailFragment = ProductDetailFragment.newInstance(product);
        detailFragment.show(getChildFragmentManager(), "ProductDetailFragment");
    }

    private void addToCart(Product product) {
        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference userCartRef = db.collection("user").document(userId).collection("cart").document(product.getId());

        userCartRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // El producto ya existe en el carrito del usuario, actualizar la cantidad
                    int cantidadActual = document.getLong("cantidad").intValue();
                    int nuevaCantidad = cantidadActual + 1;
                    int precio = document.getLong("precioTotal").intValue();
                    int precioTotal = precio * nuevaCantidad;

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("cantidad", nuevaCantidad);
                    updates.put("precioTotal", precioTotal);

                    userCartRef.update(updates).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(getContext(), "Cantidad y precio actualizados en el carrito", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Error al actualizar la cantidad y precio en el carrito", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // El producto no existe en el carrito del usuario, agregarlo
                    Map<String, Object> cartItem = new HashMap<>();
                    String tituloProducto = product.getMarca() +  " " + product.getModelo() + " "  + product.getPantalla() + "\"" +
                            product.getProcesador() + " " + product.getRam() + " " + product.getRom();
                    cartItem.put("id",product.getId());
                    cartItem.put("tituloProducto", tituloProducto);
                    cartItem.put("precioUnitario", product.getPrecio());
                    cartItem.put("precioTotal", product.getPrecio());
                    cartItem.put("cantidad", 1);
                    if (!product.getFotosUrls().isEmpty()) {
                        cartItem.put("foto", product.getFotosUrls().get(0));
                    }

                    userCartRef.set(cartItem).addOnCompleteListener(addTask -> {
                        if (addTask.isSuccessful()) {
                            Toast.makeText(getContext(), "Producto añadido al carrito", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Error al añadir al carrito", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(getContext(), "Error al verificar el carrito", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
