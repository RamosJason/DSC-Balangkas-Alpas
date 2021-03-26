package com.example.alpasapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class PersonalOrdersPage extends AppCompatActivity {
    TextView commuteDestination, commuteLocation, commutePayment, commuteMethod, commuteStatus, pickupLocation, pickupDestination, pickupItem, pickupPayment, pickupMethod, pickupStatus, deliveryEstablishment, deliveryOrder, deliveryPayment, deliveryMethod, deliveryStatus, deliveryDestination;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    Button returnBtn, changeOrderBtnOne, changeOrderBtnTwo, changeOrderBtnThree, cancelOrderBtnOne, cancelOrderBtnTwo, cancelOrderBtnThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_orders);

        commuteDestination = findViewById(R.id.commuteDestination);
        commuteLocation = findViewById(R.id.commuteCurrentLocation);
        commutePayment = findViewById(R.id.commutePaymentAmount);
        commuteMethod = findViewById(R.id.commutePaymentMethod);
        commuteStatus = findViewById(R.id.commuteStatus);
        pickupItem = findViewById(R.id.pickupItem);
        pickupLocation = findViewById(R.id.pickupLocation);
        pickupDestination = findViewById(R.id.pickupDestination);
        pickupPayment = findViewById(R.id.pickupPaymentAmount);
        pickupMethod = findViewById(R.id.pickupPaymentMethod);
        pickupStatus = findViewById(R.id.pickupStatus);
        deliveryDestination = findViewById(R.id.deliveryDestination);
        deliveryEstablishment = findViewById(R.id.deliveryLocation);
        deliveryOrder = findViewById(R.id.deliveryOrder);
        deliveryPayment = findViewById(R.id.deliveryPaymentAmount);
        deliveryMethod = findViewById(R.id.deliveryPaymentMethod);
        deliveryStatus = findViewById(R.id.deliveryStatus);

        changeOrderBtnOne = findViewById(R.id.changeOrderBtn);
        changeOrderBtnTwo = findViewById(R.id.changeOrderBtn2);
        changeOrderBtnThree = findViewById(R.id.changeOrderBtn3);
        cancelOrderBtnOne = findViewById(R.id.cancelOrderBtn);
        cancelOrderBtnTwo = findViewById(R.id.cancelOrderBtn2);
        cancelOrderBtnThree = findViewById(R.id.cancelOrderBtn3);
        returnBtn = findViewById(R.id.returnBtn);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference1 = fStore.collection("commuteOrders").document(userID);
        documentReference1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.getString("destination") != null){
                    commuteDestination.setText(commuteDestination.getText() + documentSnapshot.getString("destination"));
                    commuteLocation.setText(commuteLocation.getText() + documentSnapshot.getString("location"));
                    commutePayment.setText(commutePayment.getText() + documentSnapshot.getString("payment"));
                    commuteMethod.setText(commuteMethod.getText() + documentSnapshot.getString("method"));
                    commuteStatus.setText(commuteStatus.getText() + documentSnapshot.getString("status"));
                }
            }
        });
        DocumentReference documentReference2 = fStore.collection("deliveryOrders").document(userID);
        documentReference2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.getString("destination") != null){
                    deliveryDestination.setText(deliveryDestination.getText() + documentSnapshot.getString("destination"));
                    deliveryEstablishment.setText(deliveryEstablishment.getText() + documentSnapshot.getString("establishment"));
                    deliveryOrder.setText(deliveryOrder.getText() + documentSnapshot.getString("order"));
                    deliveryPayment.setText(deliveryPayment.getText() + documentSnapshot.getString("payment"));
                    deliveryMethod.setText(deliveryMethod.getText() + documentSnapshot.getString("method"));
                    deliveryStatus.setText(deliveryStatus.getText() + documentSnapshot.getString("status"));
                }

            }
        });
        DocumentReference documentReference3 = fStore.collection("pickupOrders").document(userID);
        documentReference3.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.getString("destination") != null){
                    pickupLocation.setText(pickupLocation.getText() + documentSnapshot.getString("location"));
                    pickupItem.setText(pickupItem.getText() + documentSnapshot.getString("item"));
                    pickupDestination.setText(pickupDestination.getText() + documentSnapshot.getString("destination"));
                    pickupPayment.setText(pickupPayment.getText() + documentSnapshot.getString("payment"));
                    pickupMethod.setText(pickupMethod.getText() + documentSnapshot.getString("method"));
                    pickupStatus.setText(pickupStatus.getText() + documentSnapshot.getString("status"));
                }

            }
        });

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });

        cancelOrderBtnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = fStore.collection("commuteOrders").document(userID);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()){
                                if (documentSnapshot.getString("status").equalsIgnoreCase("Pending Driver")){
                                    fStore.collection("commuteOrders").document(userID).update("status", "Canceled");
                                    commuteStatus.setText("Canceled");
                                    Toast.makeText(PersonalOrdersPage.this, "Successfully Canceled Booking", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
            }
        });
        cancelOrderBtnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = fStore.collection("deliveryOrders").document(userID);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()){
                                if (documentSnapshot.getString("status").equalsIgnoreCase("Pending Driver")){
                                    fStore.collection("deliveryOrders").document(userID).update("status", "Canceled");
                                    deliveryStatus.setText("Canceled");
                                    Toast.makeText(PersonalOrdersPage.this, "Successfully Canceled Booking", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
            }
        });
        cancelOrderBtnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = fStore.collection("pickupOrders").document(userID);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()){
                                if (documentSnapshot.getString("status").equalsIgnoreCase("Pending Driver")){
                                    fStore.collection("pickupOrders").document(userID).update("status", "Canceled");
                                    pickupStatus.setText("Canceled");
                                    Toast.makeText(PersonalOrdersPage.this, "Successfully Canceled Booking", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
            }
        });
        changeOrderBtnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserOrderingPage.class));
                finish();
            }
        });
        changeOrderBtnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserOrderingPage.class));
                finish();
            }
        });
        changeOrderBtnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserOrderingPage.class));
                finish();
            }
        });


    }
}