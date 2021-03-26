package com.example.alpasapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class ViewSelectedOrderPage extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap gMap;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ListView listView;
    String userID, OrderID, OrderType;
    Double latitudeInfoOne, longitudeInfoOne, latitudeInfoTwo, longitudeInfoTwo;
    TextView output1, output2, output3, output4, output5;
    Button cancelOrderBtn, confirmOrderBtn;
    static int set = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);

        listView = findViewById(R.id.listView);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        output1 = findViewById(R.id.output1);
        output2 = findViewById(R.id.output2);
        output3 = findViewById(R.id.output3);
        output4 = findViewById(R.id.output4);
        output5 = findViewById(R.id.output5);

        cancelOrderBtn = findViewById(R.id.cancelOrderBtn);
        confirmOrderBtn = findViewById(R.id.confirmOrderBtn);


        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this);



        userID = fAuth.getCurrentUser().getUid();

        DocumentReference tempdocumentReference = fStore.collection("userOrders").document(userID);
        tempdocumentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                OrderType = (documentSnapshot.getString("serviceType"));
                OrderID = (documentSnapshot.getString("selectedOrder"));
                DocumentReference documentReference = fStore.collection("" +OrderType).document(OrderID);
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot1, @Nullable FirebaseFirestoreException error) {
                        assert documentSnapshot1 != null;
                        if (OrderType.equalsIgnoreCase("commuteOrders")){
                            output1.setText("Destination: " + documentSnapshot1.getString("destination"));
                            output2.setText("Current Location: " + documentSnapshot1.getString("location"));
                            output3.setText("Payment Amount: " + documentSnapshot1.getString("payment"));
                            output4.setText("Payment Method: " + documentSnapshot1.getString("method"));
                            output5.setText("");


                        }
                        else if (OrderType.equalsIgnoreCase("pickupOrders")){
                            output1.setText("Pickup Location: " + documentSnapshot1.getString("location"));
                            output2.setText("Item Destination: " + documentSnapshot1.getString("destination"));
                            output3.setText("Item: " + documentSnapshot1.getString("item"));
                            output4.setText("Payment Amount: " + documentSnapshot1.getString("payment"));
                            output5.setText("Payment Method: " + documentSnapshot1.getString("method"));

                        }
                        else if (OrderType.equalsIgnoreCase("deliveryOrders")){
                            output1.setText("Establishment: " + documentSnapshot1.getString("establishment"));
                            output2.setText("Order Destination: " + documentSnapshot1.getString("destination"));
                            output3.setText("Order: " + documentSnapshot1.getString("order"));
                            output4.setText("Payment Amount: " + documentSnapshot1.getString("payment"));
                            output5.setText("Payment Method: " + documentSnapshot1.getString("method"));

                        }
                    }
                });
            }
        });

        cancelOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),OrderLists.class));
                finish();
            }
        });

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ReceiptPage.class));
                finish();
            }

        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();

                markerOptions.position(latLng);

                markerOptions.title("HELP");

                gMap.clear();

                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                gMap.addMarker(markerOptions);
            }
        });
        userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("userOrders").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                OrderType = (documentSnapshot.getString("serviceType"));
                OrderID = (documentSnapshot.getString("selectedOrder"));
                DocumentReference documentReference1 = fStore.collection("" +OrderType).document(OrderID);
                documentReference1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot1, @Nullable FirebaseFirestoreException error) {
                        latitudeInfoOne = Double.valueOf(documentSnapshot1.getString("latitudeOne"));
                        longitudeInfoOne = Double.valueOf(documentSnapshot1.getString("longitudeOne"));
                        latitudeInfoTwo = Double.valueOf(documentSnapshot1.getString("latitudeTwo"));
                        longitudeInfoTwo = Double.valueOf(documentSnapshot1.getString("longitudeTwo"));
                        MarkerOptions markerOptions1 = new MarkerOptions();
                        MarkerOptions markerOptions2 = new MarkerOptions();
                        LatLng latLng1 = new LatLng(latitudeInfoOne, longitudeInfoOne);
                        LatLng latLng2 = new LatLng(latitudeInfoTwo, longitudeInfoTwo);
                        markerOptions1.position(latLng1);
                        markerOptions2.position(latLng2);

                        if (OrderType.equalsIgnoreCase("commuteOrders")){
                            markerOptions1.title("Current Location");
                            markerOptions2.title("Commute Destination");
                        } else if (OrderType.equalsIgnoreCase("pickOrders")){
                            markerOptions1.title("Current Location");
                            markerOptions2.title("Pickup Location");
                        } else if (OrderType.equalsIgnoreCase("deliveryOrders")){
                            markerOptions1.title("Establishment Location");
                            markerOptions2.title("Delivery Destination");
                        }
                        gMap.clear();
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(markerOptions1.getPosition());
                        builder.include(markerOptions2.getPosition());
                        LatLngBounds bounds = builder.build();
                        gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));
                        gMap.addMarker(markerOptions1);
                        gMap.addMarker(markerOptions2);
                    }
                });
            }
        });


    }

}