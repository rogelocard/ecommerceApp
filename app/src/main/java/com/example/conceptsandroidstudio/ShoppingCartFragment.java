package com.example.conceptsandroidstudio;


import android.content.DialogInterface;
import android.location.Address;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import static android.content.ContentValues.TAG;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.Geocoder;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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


//    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private LinearLayout addressContainer;
    private List<String> addressList = new ArrayList<>();
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

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        // Initialize FusedLocationProviderClient
         fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
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
        
        TextView addAddressTextView = view.findViewById(R.id.add_address_text_view);
        addressContainer = view.findViewById(R.id.address_container);

        addAddressTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showAddressDialog();
                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    getCurrentLocation();
                }
            }
        });

        loadAddresses();

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
        if(cartItemList !=null){
            for (CartItem item : cartItemList) {
                valorTotal += item.getPrecioTotal();
            }
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

    /**
     * Function to check the user's answer to the permission request.
     * @param requestCode The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(requireActivity(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Location location = task.getResult();
                    getAddressFromLocation(location);
                } else {
                    requestNewLocationData();
                }
            }
        });
    }

    private void requestNewLocationData() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000)
                .setMaxUpdateDelayMillis(10000)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Toast.makeText(requireActivity(), "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
                    return;
                }
                Location location = locationResult.getLastLocation();
                getAddressFromLocation(location);
                fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            }
        };

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressText = address.getAddressLine(0);
                saveAddress(addressText);
            } else {
                Toast.makeText(requireActivity(), "No se pudo obtener la dirección", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireActivity(), "Error obteniendo la dirección", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveAddress(String address) {
        if (addressList.size() >= 3) {
            // Remove the first address and show a message
            addressList.remove(0);
            Toast.makeText(requireActivity(), "Se ha eliminado la primera dirección para añadir la nueva.", Toast.LENGTH_SHORT).show();
        }
        addressList.add(address);
        CollectionReference addressesRef = db.collection("user").document(mAuth.getCurrentUser().getUid()).collection("addresses");

//        Map<String, Object> addresses = new HashMap<>();
//        addresses.put("addresses", addressList);
        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("address", address);

        addressesRef.add(addressMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    displayAddresses();
                }
            }
        });

    }

    private void loadAddresses() {
        CollectionReference addressesRef = db.collection("user").document(mAuth.getCurrentUser().getUid()).collection("addresses");

        addressesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    addressList.clear();
                    for (DocumentSnapshot document : task.getResult()) {
                        addressList.add(document.getString("address"));
                    }
                    displayAddresses();
                }
            }
        });
    }

    private void displayAddresses() {
        addressContainer.removeAllViews();
        for (String address : addressList) {
            TextView addressTextView = new TextView(requireActivity());
            addressTextView.setText(address);
            addressTextView.setPadding(8, 8, 8, 8);
            addressTextView.setTextSize(16);
            addressContainer.addView(addressTextView);
        }
    }

}