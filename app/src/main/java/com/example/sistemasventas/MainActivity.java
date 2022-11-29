package com.example.sistemasventas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    //Se genera un objeto para conectarse a la base de datos de firebase - Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String idAtomaticoFB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Referenciar los IDs del archivo activity_main.s¿xml

        EditText idseller = findViewById(R.id.etidseller);
        EditText fullname = findViewById(R.id.etfullname);
        EditText email = findViewById(R.id.etemail);
        EditText password = findViewById(R.id.etpassword);
        TextView totalcomision = findViewById(R.id.tvtotalcomision);
        ImageButton btnsave = findViewById(R.id.btnsave);
        ImageButton btnsearch = findViewById(R.id.btnsearch);
        ImageButton btnedit = findViewById(R.id.btnedit);
        ImageButton btndelete = findViewById(R.id.btndelete);
        ImageButton btnsales = findViewById(R.id.btnsales);
        ImageButton btnlist = findViewById(R.id.btnlist);

        //Eventos

        btnedit.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           Map<String, Object> mSeller = new HashMap<>();
                                           mSeller.put("idseller", idseller.getText().toString());
                                           mSeller.put("fullname", fullname.getText().toString());
                                           mSeller.put("email", email.getText().toString());
                                           mSeller.put("password", password.getText().toString());
                                           mSeller.put("totalcomision", 0);

                                           //Actualizar el documento a la coleccion seller a traves de la tabla temporal mseller
                                           db.collection("seller").document(idAtomaticoFB)
                                                   .set(mSeller)
                                                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                       @Override
                                                       public void onSuccess(Void unused) {
                                                           Toast.makeText(MainActivity.this, "Vendedor actualizado correctamente", Toast.LENGTH_SHORT).show();
                                                       }
                                                   })
                                                   .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getApplicationContext(), "Error al guardar el vendedor", Toast.LENGTH_SHORT).show();
                                                }
                                                });
                                       }
                                   });
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Buscar por Idseller y recuperar todos los datos
                db.collection("seller")
                        .whereEqualTo("idseller", idseller.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {

                                        //La instantanea tiene informacion del documento
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            //Mostrar el Id de la BD

                                            idAtomaticoFB = document.getId();

                                            //mostrar la informacion en cada de uno de los objetos referenciados
                                            fullname.setText(document.getString("fullname"));
                                            email.setText(document.getString("email"));
                                            totalcomision.setText(String.valueOf(document.getDouble(("Total Comision"))));

                                        }
                                    } else {
                                        //Si no encuentra el Idseller del vendedor
                                        Toast.makeText(getApplicationContext(), "Id vendedor No Existe ", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }
                        });
            }
        });
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validar si los datos estan diligenciados
                String mIdseller = idseller.getText().toString();
                String mFullname = fullname.getText().toString();
                String mEmail = email.getText().toString();
                String mPassword = password.getText().toString();


                if (!mIdseller.isEmpty() && !mFullname.isEmpty() && !mEmail.isEmpty() && !mPassword.isEmpty()) {
                    //Buscar el IDseller para verificar si esta o no el id

                    db.collection("seller")
                            .whereEqualTo("idseller", idseller.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().isEmpty()) { //No encontro id seller

                                            //Crear una tabla temporal con los mismos campos de la coleccion
                                            Map<String, Object> mSeller = new HashMap<>();
                                            mSeller.put("idseller", mIdseller);
                                            mSeller.put("fullname", mFullname);
                                            mSeller.put("email", mEmail);
                                            mSeller.put("password", mPassword);
                                            mSeller.put("totalcomision", 0);


                                            //Agregar el documento a la coleccion seller a traves de la tabla temporal mseller
                                            db.collection("seller")
                                                    .add(mSeller)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Toast.makeText(getApplicationContext(), "Vendedor agregado Exitosamente", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getApplicationContext(), "Error al guardar el vendedor", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(getApplicationContext(), " Id ya registrado ", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                            });
                } else {
                    Toast.makeText(getApplicationContext(), " Debe ingresar todos los datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


