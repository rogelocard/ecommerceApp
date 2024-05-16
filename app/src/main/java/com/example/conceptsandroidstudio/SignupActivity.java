package com.example.conceptsandroidstudio;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {


    ImageButton btn_registro;
    EditText nombre, email , password , confirmpasword ;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        nombre = findViewById(R.id.namereg);
        email = findViewById(R.id.emailreg);
        password = findViewById(R.id.password_reg);
        confirmpasword = findViewById(R.id.password_cof);
        btn_registro = findViewById(R.id.signupButton);

        btn_registro.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String emailuser = email.getText().toString().trim();
                String pasword_user = password.getText().toString().trim();
                String nameuser = nombre.getText().toString().trim();
                String pasword_confir = confirmpasword.getText().toString().trim();

                if (emailuser.isEmpty() || pasword_user.isEmpty() || nameuser.isEmpty() || pasword_confir.isEmpty()){
                    Toast.makeText(SignupActivity.this, "Ingrese los datos", Toast.LENGTH_SHORT).show();

                } else if (pasword_user.length() < 6) {
                    Toast.makeText(SignupActivity.this, "La contraseñá debe tener 6 caracteres minimo", Toast.LENGTH_SHORT).show();
                    
                } else if (!pasword_confir.equals(pasword_user)) {
                    Toast.makeText(SignupActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();

                }

                else {
                    registrouser(nameuser,emailuser,pasword_user);
                }

            }
        });




    }

    private void registrouser(String nameuser, String emailuser, String paswordUser) {
        mAuth.createUserWithEmailAndPassword(emailuser, paswordUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String id = mAuth.getCurrentUser().getUid(); // Mediante metodo de autenticación obtenemos Id del usuario//
                Map<String, Object> map = new HashMap<>();
                map.put("id",id);
                map.put("Nombre",nameuser);
                map.put("Password",paswordUser);
                map.put("Email",emailuser);

                db.collection("user").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                        startActivity(new Intent(SignupActivity.this,MainActivity.class));
                        Toast.makeText(SignupActivity.this, "Registro Exitoso", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignupActivity.this, "Error al Guardar", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignupActivity.this, "Error al Registrarce", Toast.LENGTH_SHORT).show();
            }
        });



    }


}
