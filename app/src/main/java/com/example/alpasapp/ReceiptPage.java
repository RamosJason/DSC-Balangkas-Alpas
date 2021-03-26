package com.example.alpasapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ReceiptPage extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    TextView name, id, phone;
    ImageView picture;
    String userID;
    static String orderId, orderType;
    StorageReference storageReference;
    TextView returnBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciept_page);

        returnBtn = findViewById(R.id.returnBtn);
        name = findViewById(R.id.receiptName);
        id = findViewById(R.id.receiptId);
        phone = findViewById(R.id.receiptPhone);

        picture = findViewById(R.id.receiptPicture);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        userID = fAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");

        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri).into(picture);

            }
        });

        DocumentReference documentReference = fStore.collection("userOrders").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                orderId = documentSnapshot.getString("selectedOrder");
                orderType = documentSnapshot.getString("serviceType");
                DocumentReference documentReference1 = fStore.collection("users").document(orderId);
                documentReference1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot1, @Nullable FirebaseFirestoreException error) {
                        name.setText(documentSnapshot1.getString("fName"));
                        id.setText("Unique User ID: \n"+ orderId);
                        phone.setText("Contact Info: \n" + documentSnapshot1.getString("phone"));
                    }
                });
            }
        });

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fStore.collection("" + orderType).document(orderId).update("status", "Accepted By Driver");
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });


    }
}