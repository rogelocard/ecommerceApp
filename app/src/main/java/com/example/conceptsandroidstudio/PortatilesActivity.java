package com.example.conceptsandroidstudio;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class PortatilesActivity extends AppCompatActivity {

    private static final String TAG = "PortatilesActivity";

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portatiles);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.portatiles), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Obtener una referencia a la colección "portatiles" en Firebase Firestore
        CollectionReference portatilesRef = db.collection("portatiles");

        // Obtener los documentos de la colección "portatiles"
        Log.d(TAG, "Antes de abrir la función");
        portatilesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Obtener los datos de cada documento
                        String modelo = document.getString("modelo");
                        String color = document.getString("color");
                        List<String> fotosUrls = (List<String>) document.get("fotos");

                        // Imprimir los datos en la consola utilizando Log.d()
                        Log.d(TAG, "Modelo: " + modelo);
                        Log.d(TAG, "Color: " + color);
                        Log.d(TAG, "Fotos URLs: " + fotosUrls);
                        // Mostrar los datos en la interfaz de usuario
                        mostrarDatos(modelo, color, fotosUrls);
                        break;
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    Toast.makeText(PortatilesActivity.this, "error", Toast.LENGTH_LONG).show();
                }
                Toast.makeText(PortatilesActivity.this, "Finaliza el for", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Función para mostrar los datos en la interfaz de usuario
    private void mostrarDatos(String modelo, String color, List<String> fotosUrls) {
        // Referencias a los elementos de la interfaz de usuario
        TextView modeloTextView = findViewById(R.id.modeloTextView);
        TextView colorTextView = findViewById(R.id.colorTextView);
        ImageView fotoImageView = findViewById(R.id.fotoImageView);

        // Mostrar modelo y color en los TextViews
        modeloTextView.setText(modelo);
        colorTextView.setText(color);

        // Mostrar la primera foto en el ImageView usando Glide
        if (fotosUrls != null && fotosUrls.size() > 0) {
            String primeraUrlFoto = fotosUrls.get(0);
            Glide.with(this)
                    .load(primeraUrlFoto)
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder_image))
                    .into(fotoImageView);
        }
    }
}
