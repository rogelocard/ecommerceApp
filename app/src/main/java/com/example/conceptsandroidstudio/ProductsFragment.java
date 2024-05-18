package com.example.conceptsandroidstudio;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import android.widget.ImageView;
import coil.Coil;
import coil.api.load;

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
            Log.d(TAG, "Antes de abrir la función");
            portatilesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Integer contador = 1;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Obtener los datos de cada documento

                            String marca = document.getString("marca");
                            String modelo = document.getString("modelo");
                            List<String> fotosUrls = (List<String>) document.get("fotos");

                            // Imprimir los datos en la consola utilizando Log.d()
                            Log.d(TAG, "Modelo: " + modelo);
                            Log.d(TAG, "Marca: " + marca);
                            Log.d(TAG, "Fotos URLs: " + fotosUrls);
                            // Mostrar los datos en la interfaz de usuario
                            mostrarDatos(modelo, marca, fotosUrls, contador);
                            contador = contador + 1;
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(getActivity(), "Finaliza el for", Toast.LENGTH_LONG).show();
                }
            });
        }
    private void mostrarDatos(String modelo, String marca, List<String> fotosUrls, Integer contador) {

        // Obtén el ID dinámico basado en el contador
        String textViewIdName = "nombreProducto" + contador;
        Integer textViewId = getResources().getIdentifier(textViewIdName, "id", getContext().getPackageName());
        String imageViewIdName= "imageProducto" + contador;
        Integer imageViewId = getResources().getIdentifier(imageViewIdName, "id", getContext().getPackageName());

        // Referencias a los elementos de la interfaz de usuario
        TextView nombreTextView = rootView.findViewById(textViewId);
        ImageView fotoImageView = rootView.findViewById(imageViewId);

        // Mostrar modelo y color en los TextViews
        nombreTextView.setText(marca + " " + modelo);


        if (fotosUrls != null && !fotosUrls.isEmpty()) {
            String primeraUrlFoto = fotosUrls.get(0);
            Coil.load(getContext(), primeraUrlFoto); {
                placeholder(R.drawable.placeholder_image); // Imagen de placeholder
                // Otras configuraciones, como el tamaño de la imagen, ajuste, etc.
                target { drawable ->
                        // Actualizar la imagen en el ImageView
                        fotoImageView.setImageDrawable(drawable)
                }
            }
        }

        // Mostrar la primera foto en el ImageView usando Glide
        /*if (fotosUrls != null && fotosUrls.size() > 0) {
            String primeraUrlFoto = fotosUrls.get(0);
            Glide.with(getContext())
                    .load(primeraUrlFoto)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.placeholder_image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .fitCenter())
                    .into(fotoImageView);
        }*/
    }
}
