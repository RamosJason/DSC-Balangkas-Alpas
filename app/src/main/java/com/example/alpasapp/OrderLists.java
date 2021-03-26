package com.example.alpasapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderLists extends AppCompatActivity {
    ListView listView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String orderid, a;
    String userID;
    Button returnBtn, pickupBtn, commuteBtn, deliveryBtn;
    String serviceType;

    private List<String> orderslist = new ArrayList<>();
    private List<String> orderlistid = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_lists);

        returnBtn = findViewById(R.id.returnBtn);

        listView = findViewById(R.id.listView);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        pickupBtn = findViewById(R.id.viewPickupBtn);
        deliveryBtn = findViewById(R.id.viewDeliveryBtn);
        commuteBtn = findViewById(R.id.viewCommuteBtn);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_view_settings,orderslist);

        userID = fAuth.getCurrentUser().getUid();

        pickupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fStore.collection("pickupOrders").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException error) {
                        orderslist.clear();
                        List<DocumentSnapshot> documents = documentSnapshots.getDocuments();
                        for (DocumentSnapshot document : documents){
                            a = document.getString("status");
                            if (a.equalsIgnoreCase("Pending Driver")){
                                serviceType = "pickupOrders";
                                orderlistid.add(document.getString("id"));
                                orderslist.add("Pickup Location: " + document.getString("location") + "\nPickup Destination: " + document.getString("destination") + "\nItem(s): " + document.getString("item") + "\nPayment Amount: " + document.getString("payment") + "\nPayment Method: " + document.getString("method"));
                            }
                        }
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);

                    }
                });
            }
        });
        commuteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fStore.collection("commuteOrders").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException error) {
                        orderslist.clear();
                        List<DocumentSnapshot> documents = documentSnapshots.getDocuments();
                        for (DocumentSnapshot document : documents){
                            a = document.getString("status");
                            if (a.equalsIgnoreCase("Pending Driver")){
                                serviceType = "commuteOrders";
                                orderlistid.add(document.getString("id"));
                                orderslist.add("Current Location: " + document.getString("location") + "\nCommute Destination: " + document.getString("destination") + "\nPayment Amount: " + document.getString("payment") + "\nPayment Method: " + document.getString("method"));
                            }
                        }
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);

                    }
                });
            }
        });
        deliveryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fStore.collection("deliveryOrders").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException error) {
                        orderslist.clear();
                        List<DocumentSnapshot> documents = documentSnapshots.getDocuments();
                        for (DocumentSnapshot document : documents){
                            a = document.getString("status");
                            if (a.equalsIgnoreCase("Pending Driver")){
                                serviceType = "deliveryOrders";
                                orderlistid.add(document.getString("id"));
                                orderslist.add("Establishment Location: " + document.getString("establishment") + "\nOrder Destination: " + document.getString("destination") + "\nOrder: " + document.getString("order") + "\nPayment Amount: " + document.getString("payment") + "\nPayment Method: " + document.getString("method"));

                            }
                        }
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);

                    }
                });
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Map<String, String> order = new HashMap<>();

                orderid = orderlistid.get(position);


                DocumentReference documentReference = fStore.collection("userOrders").document(userID);

                order.put("selectedOrder", orderid);
                order.put("serviceType", serviceType);

                documentReference.set(order).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(getApplicationContext(), ViewSelectedOrderPage.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String error = e.getMessage();
                        Toast.makeText(OrderLists.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });


    }
}