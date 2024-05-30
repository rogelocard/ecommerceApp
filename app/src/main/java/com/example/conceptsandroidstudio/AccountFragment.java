package com.example.conceptsandroidstudio;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db,db1;
    private FirebaseAuth mAuth1;
    Button exit1;
    private View rootView;

    TextView delete;



    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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



        this.db = FirebaseFirestore.getInstance();
        this.mAuth1 = FirebaseAuth.getInstance();




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_account, container, false);

        exit1=rootView.findViewById(R.id.buttonexit);
        delete=rootView.findViewById(R.id.deleteUser);


        if(db == null){
            db= FirebaseFirestore.getInstance();
        }

//Cerramos Sesión
        exit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth1.signOut();

                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requireActivity().finish();

            }
        });
//Eliminamos el Usuario
        delete.setOnClickListener(new View.OnClickListener() {


            @Override

            public void onClick(View v) {

                FirebaseUser loginUser = mAuth1.getCurrentUser();


                if (loginUser != null) {
                    String userId = loginUser.getUid();


                    db.collection("user").document(userId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {


                                @Override
                                public void onSuccess(Void aVoid) {

                                    loginUser.delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    // Redirigir al usuario a LoginActivity
                                                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                                                    Toast.makeText(requireContext(), "Cuenta Eliminada con exito", Toast.LENGTH_SHORT).show();
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    requireActivity().finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(requireContext(), "Error al borrar usuario de FirebaseAuth", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(requireContext(), "Error al borrar usuario de Firestore", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(requireContext(), "Ningún usuario autenticado", Toast.LENGTH_SHORT).show();
                }
            }

        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        super.onViewCreated(view, savedInstanceState);
        FirebaseUser currentUser = mAuth1.getCurrentUser();

        if (currentUser != null) {


            String userId = mAuth1.getCurrentUser().getUid();

if(db != null){
            db.collection("user").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Si el documento existe, obtener el nombre del usuario
                            String userName = documentSnapshot.getString("Nombre");
                            String emailtex = documentSnapshot.getString("Email");


                            // Actualizar el TextView con el nombre del usuario
                            TextView nameTextView = view.findViewById(R.id.nameuser1);
                            nameTextView.setText(userName);
                            TextView nameTextView1 = view.findViewById(R.id.emailUser1);
                            nameTextView1.setText(emailtex);

                        } else {
                            Toast.makeText(requireContext(), "No se encontró el Usuario", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Error al consultar Firestore", Toast.LENGTH_SHORT).show();
                    });
        }}

        else {

            startActivity(new Intent(requireContext(), MenuActivity.class));

            Toast.makeText(requireContext(), "Ningún usuario autenticado", Toast.LENGTH_SHORT).show();


        }
    }
}