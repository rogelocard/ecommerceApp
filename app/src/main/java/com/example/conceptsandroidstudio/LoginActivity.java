package com.example.conceptsandroidstudio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity{

    ImageButton btn_login;
    EditText email, pasword;
    FirebaseAuth mAuth;

    TextView registro;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();


        btn_login= findViewById(R.id.login);
        email= findViewById(R.id.editTextText);
        pasword= findViewById(R.id.editTextTextpassword);
        registro= findViewById(R.id.new_user);



        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailuser = email.getText().toString().trim();
                String pasword_user = pasword.getText().toString().trim();

                if(emailuser.isEmpty() || pasword_user.isEmpty()){
                    Toast.makeText(LoginActivity.this,"Porfavor Ingresar los Datos", Toast.LENGTH_SHORT).show();
                }

                else {
                    loginUser(emailuser, pasword_user);

                }
            }
        });

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });


    }
    private void loginUser(String emailUser, String password){
        mAuth.signInWithEmailAndPassword(emailUser, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    finish();
                    startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                    Toast.makeText(LoginActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                }

                else {
                    Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Error al iniciar Sesión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
