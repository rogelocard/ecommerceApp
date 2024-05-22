package com.example.conceptsandroidstudio;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingCartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingCartFragment extends Fragment {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private TextView valorTotalTextView;
    private Button vaciarCarritoButton;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShoppingCartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShoppingCartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShoppingCartFragment newInstance(String param1, String param2) {
        ShoppingCartFragment fragment = new ShoppingCartFragment();
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
    }
    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);

        // Inicializar RecyclerView y adaptador
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItemList);
        cartRecyclerView.setAdapter(cartAdapter);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        valorTotalTextView = view.findViewById(R.id.valorTotalTextView);

        // Inicializar botón para vaciar carrito
        vaciarCarritoButton = view.findViewById(R.id.vaciarCarrito);
        vaciarCarritoButton.setOnClickListener(v -> vaciarCarrito());

        // Aquí debes obtener los datos del carrito de Fire
        // base y actualizar cartItemList
        obtenerDatosDelCarrito();

        return view;
    }
    private void obtenerDatosDelCarrito() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference cartRef = FirebaseFirestore.getInstance().collection("user").document(userId).collection("cart");

        // Agregar SnapshotListener para escuchar cambios en tiempo real
        cartRef.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w("ShoppingCartFragment", "listen:error", e);
                return;
            }

            if (snapshots != null) {
                cartItemList.clear();
                for (QueryDocumentSnapshot document : snapshots) {
                    String id = document.getId();
                    String tituloProducto = document.getString("tituloProducto");
                    int cantidad = document.getLong("cantidad").intValue();
                    long precioUnitario = document.getLong("precioUnitario");
                    long precioTotal = document.getLong("precioTotal");
                    String fotoUrl = document.getString("foto");

                    CartItem cartItem = new CartItem(id, tituloProducto, cantidad, precioUnitario, precioTotal, fotoUrl);
                    cartItemList.add(cartItem);
                }
                // Notificar al adaptador que los datos han cambiado
                cartAdapter.notifyDataSetChanged();

                // Calcular y actualizar el valor total
                calcularValorTotal();
            }
        });
    }
    private void calcularValorTotal() {
        long valorTotal = 0;
        for (CartItem item : cartItemList) {
            valorTotal += item.getPrecioTotal();
        }
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        valorTotalTextView.setText(numberFormat.format(valorTotal));

    }
    private void vaciarCarrito() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference cartRef = FirebaseFirestore.getInstance().collection("user").document(userId).collection("cart");

        cartRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    batch.delete(document.getReference());
                }
                batch.commit().addOnCompleteListener(batchTask -> {
                    if (batchTask.isSuccessful()) {
                        cartItemList.clear();
                        cartAdapter.notifyDataSetChanged();
                        calcularValorTotal();
                        Toast.makeText(getContext(), "Carrito vaciado", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w("ShoppingCartFragment", "Error vaciando el carrito", batchTask.getException());
                        Toast.makeText(getContext(), "Error al vaciar el carrito", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.w("ShoppingCartFragment", "Error obteniendo items del carrito", task.getException());
                Toast.makeText(getContext(), "Error al obtener los items del carrito", Toast.LENGTH_SHORT).show();
            }
        });
    }
}