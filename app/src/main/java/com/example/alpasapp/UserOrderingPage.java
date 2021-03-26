package com.example.alpasapp;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
        import android.os.Bundle;
        import android.text.TextUtils;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.MarkerOptions;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.firestore.DocumentReference;
        import com.google.firebase.firestore.DocumentSnapshot;
        import com.google.firebase.firestore.EventListener;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.firestore.FirebaseFirestoreException;

        import java.util.HashMap;
        import java.util.Map;


public class UserOrderingPage extends AppCompatActivity implements OnMapReadyCallback {
    Button CommuteBtn, DeliveryBtn, PickupBtn, commuteOrderBtn, deliveryOrderBtn, pickupOrderBtn, backBtn, deliveryPointOne, commutePointOne, pickupPointOne, deliveryPointTwo, commutePointTwo, pickupPointTwo;
    ExpandableRelativeLayout Commute, Delivery, Pickup;
    GoogleMap gMap;
    EditText commuteDestination, commuteLocation, commutePayment, commuteMethod, pickupLocation, pickupDestination, pickupItem, pickupPayment, pickupMethod, deliveryDestination, deliveryEstablishment, deliveryOrder, deliveryPayment, deliveryMethod;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID, userName;
    Double latitudeInfoPointOne, longitudeInfoPointOne , latitudeInfoPointTwo, longitudeInfoPointTwo;
    ImageView commuteBackBtn, deliveryBackBtn, pickupBackBtn;
    TextView commuteSelect, deliverySelect, pickupSelect;
    static Boolean point;
    static String type;
    static String pointOneType, pointTwoType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_ordering_page);

        commuteDestination = findViewById(R.id.commuteDestination);
        commuteLocation = findViewById(R.id.commuteCurrentLocation);
        commutePayment = findViewById(R.id.commutePaymentAmount);
        commuteMethod = findViewById(R.id.commutePaymentMethod);
        pickupItem = findViewById(R.id.pickupItem);
        pickupLocation = findViewById(R.id.pickupLocation);
        pickupDestination = findViewById(R.id.pickupDestination);
        pickupPayment = findViewById(R.id.pickupPaymentAmount);
        pickupMethod = findViewById(R.id.pickupPaymentMethod);
        deliveryDestination = findViewById(R.id.deliveryDestination);
        deliveryEstablishment = findViewById(R.id.deliveryLocation);
        deliveryOrder = findViewById(R.id.deliveryOrder);
        deliveryPayment = findViewById(R.id.deliveryPaymentAmount);
        deliveryMethod = findViewById(R.id.deliveryPaymentMethod);

        commuteSelect = findViewById(R.id.currentlySelectingCommute);
        deliverySelect = findViewById(R.id.currentlySelectingDelivery);
        pickupSelect = findViewById(R.id.currentlySelectingPickup);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        deliveryPointOne = findViewById(R.id.deliveryEstablishmentBtn);
        commutePointOne = findViewById(R.id.commuteCurrentLocationBtn);
        pickupPointOne = findViewById(R.id.pickupLocationBtn);
        deliveryPointTwo = findViewById(R.id.deliveryDestinationBtn);
        commutePointTwo = findViewById(R.id.commuteDestinationBtn);
        pickupPointTwo = findViewById(R.id.pickupDestinationBtn);

        commuteBackBtn = findViewById(R.id.commuteBackBtn);
        deliveryBackBtn = findViewById(R.id.deliveryBackBtn);
        pickupBackBtn = findViewById(R.id.pickupBackBtn);
        backBtn = findViewById(R.id.backBtn);
        CommuteBtn = (Button) findViewById(R.id.commuteBtn);
        DeliveryBtn = (Button) findViewById(R.id.deliveryBtn);
        PickupBtn = (Button) findViewById(R.id.pickupBtn);
        commuteOrderBtn = findViewById(R.id.commuteOrderBtn);
        deliveryOrderBtn = findViewById(R.id.deliveryOrderBtn);
        pickupOrderBtn = findViewById(R.id.pickupOrderBtn);

        Pickup = (ExpandableRelativeLayout) findViewById(R.id.Pickup);
        Commute = (ExpandableRelativeLayout) findViewById(R.id.Commute);
        Delivery = (ExpandableRelativeLayout) findViewById(R.id.Delivery);

        point = false;

        Delivery.collapse();
        Pickup.collapse();
        Commute.collapse();


        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this);

        userID = fAuth.getCurrentUser().getUid().toString();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                userName = (documentSnapshot.getString("fName"));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });

        commuteOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Destination = commuteDestination.getText().toString().trim();
                String Location = commuteLocation.getText().toString().trim();
                String Payment = commutePayment.getText().toString().trim();
                String Method = commuteMethod.getText().toString().trim();
                if(TextUtils.isEmpty(Destination)){
                    commuteDestination.setError("Destination is Required.");
                    return;
                }
                if(TextUtils.isEmpty(Location)){
                    commuteLocation.setError("Current Location is Required.");
                    return;
                }
                if(TextUtils.isEmpty(Payment)){
                    commutePayment.setError("Payment is Required.");
                    return;
                }
                if(TextUtils.isEmpty(Method)){
                    commuteMethod.setError("Payment Method is Required.");
                    return;
                }

                if (latitudeInfoPointOne == null || longitudeInfoPointOne == null || latitudeInfoPointTwo == null || longitudeInfoPointTwo == null){
                    Toast.makeText(UserOrderingPage.this, "Please Input Locations On The Map", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> order = new HashMap<>();

                order.put("latitudeOne", latitudeInfoPointOne.toString());
                order.put("longitudeOne", longitudeInfoPointOne.toString());
                order.put("latitudeTwo", latitudeInfoPointTwo.toString());
                order.put("longitudeTwo", longitudeInfoPointTwo.toString());
                order.put("destination", Destination);
                order.put("location", Location);
                order.put("payment", Payment);
                order.put("method", Method);
                order.put("username", userName);
                order.put("status", "Pending Driver");
                order.put("id", userID);
                order.put("serviceType", "commuteOrders");
                DocumentReference documentReference = fStore.collection("commuteOrders").document(userID);
                documentReference.set(order).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UserOrderingPage.this, "Successful Booking", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String error = e.getMessage();
                        Toast.makeText(UserOrderingPage.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        pickupOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Destination = pickupDestination.getText().toString().trim();
                String Item = pickupItem.getText().toString().trim();
                String Payment = pickupPayment.getText().toString().trim();
                String Method = pickupMethod.getText().toString().trim();
                String Location = pickupLocation.getText().toString().trim();

                if(TextUtils.isEmpty(Location)){
                    pickupLocation.setError("Pickup Location is Required.");
                    return;
                }
                if(TextUtils.isEmpty(Destination)){
                    pickupDestination.setError("Destination is Required.");
                    return;
                }
                if(TextUtils.isEmpty(Item)){
                    pickupItem.setError("Item is Required.");
                    return;
                }
                if(TextUtils.isEmpty(Payment)){
                    pickupPayment.setError("Payment is Required.");
                    return;
                }
                if(TextUtils.isEmpty(Method)){
                    pickupMethod.setError("Payment Method is Required.");
                    return;
                }

                Map<String, String> order = new HashMap<>();

                if (latitudeInfoPointOne == null || longitudeInfoPointOne == null || latitudeInfoPointTwo == null || longitudeInfoPointTwo == null){
                    Toast.makeText(UserOrderingPage.this, "Please Input Locations On The Map", Toast.LENGTH_SHORT).show();
                    return;
                }
                order.put("latitudeOne", latitudeInfoPointOne.toString());
                order.put("longitudeOne", longitudeInfoPointOne.toString());
                order.put("latitudeTwo", latitudeInfoPointTwo.toString());
                order.put("longitudeTwo", longitudeInfoPointTwo.toString());
                order.put("item", Item);
                order.put("location", Location);
                order.put("destination", Destination);
                order.put("payment", Payment);
                order.put("method", Method);
                order.put("username", userName);
                order.put("status", "Pending Driver");
                order.put("id", userID);
                order.put("serviceType", "pickupOrders");

                DocumentReference documentReference = fStore.collection("pickupOrders").document(userID);
                documentReference.set(order).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UserOrderingPage.this, "Successful Booking", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String error = e.getMessage();
                        Toast.makeText(UserOrderingPage.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        deliveryOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Establishment = deliveryEstablishment.getText().toString().trim();
                String Order = deliveryOrder.getText().toString().trim();
                String Payment = deliveryPayment.getText().toString().trim();
                String Method = deliveryMethod.getText().toString().trim();
                String Destination = deliveryDestination.getText().toString().trim();

                if(TextUtils.isEmpty(Establishment)){
                    deliveryEstablishment.setError("Establishment is Required.");
                    return;
                }
                if(TextUtils.isEmpty(Destination)){
                    deliveryDestination.setError("Order Destination is Required.");
                    return;
                }
                if(TextUtils.isEmpty(Order)){
                    deliveryOrder.setError("Order is Required.");
                    return;
                }
                if(TextUtils.isEmpty(Payment)){
                    deliveryPayment.setError("Payment is Required.");
                    return;
                }
                if(TextUtils.isEmpty(Method)){
                    deliveryMethod.setError("Payment Method is Required.");
                    return;
                }

                if (latitudeInfoPointOne == null || longitudeInfoPointOne == null || latitudeInfoPointTwo == null || longitudeInfoPointTwo == null){
                    Toast.makeText(UserOrderingPage.this, "Please Input Locations On The Map", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> order = new HashMap<>();

                order.put("latitudeOne", latitudeInfoPointOne.toString());
                order.put("longitudeOne", longitudeInfoPointOne.toString());
                order.put("latitudeTwo", latitudeInfoPointTwo.toString());
                order.put("longitudeTwo", longitudeInfoPointTwo.toString());
                order.put("destination", Destination);
                order.put("establishment", Establishment);
                order.put("order", Order);
                order.put("payment", Payment);
                order.put("method", Method);
                order.put("username", userName);
                order.put("status", "Pending Driver");
                order.put("id", userID);
                order.put("serviceType", "deliveryOrders");

                DocumentReference documentReference = fStore.collection("deliveryOrders").document(userID);
                documentReference.set(order).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UserOrderingPage.this, "Successful Booking", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String error = e.getMessage();
                        Toast.makeText(UserOrderingPage.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        deliveryPointOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliverySelect.setText("Click On Map: Establishment Location");
                point = false;
                type = "delivery";
            }
        });
        deliveryPointTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliverySelect.setText("Click On Map: Order Destination");
                point = true;
                type = "delivery";
            }
        });
        commutePointOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commuteSelect.setText("Click On Map: Current Location");
                point = false;
                type = "commute";
            }
        });
        commutePointTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commuteSelect.setText("Click On Map: Destination");
                point = true;
                type = "commute";
            }
        });
        pickupPointOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickupSelect.setText("Click On Map: Pickup Location");
                point = false;
                type = "pickup";
            }
        });
        pickupPointTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickupSelect.setText("Click On Map: Destination");
                point = true;
                type = "pickup";
            }
        });



    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (point == false){
                    MarkerOptions markerOptions = new MarkerOptions();

                    markerOptions.position(latLng);

                    if (type == "commute"){
                        markerOptions.title("Current Location");
                    } else if (type == "delivery"){
                        markerOptions.title("Establishment Location");
                    } else if (type == "pickup"){
                        markerOptions.title("Pickup Location");
                    }


                    latitudeInfoPointOne = (latLng.latitude);
                    longitudeInfoPointOne = (latLng.longitude);
                    pointOneType = type;

                    gMap.clear();

                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                    gMap.addMarker(markerOptions);

                    if (latitudeInfoPointTwo != null && pointTwoType.equalsIgnoreCase(type)){
                        LatLng temp = new LatLng(latitudeInfoPointTwo, longitudeInfoPointTwo);
                        MarkerOptions markerOptionsTemp = new MarkerOptions();
                        markerOptionsTemp.position(temp);
                        if (type == "commute"){
                            markerOptionsTemp.title("Commute Destination");
                        } else if (type == "delivery"){
                            markerOptionsTemp.title("Delivery Destination");
                        } else if (type == "pickup"){
                            markerOptionsTemp.title("Pickup Destination");
                        }

                        gMap.addMarker(markerOptionsTemp);
                    }
                }
                else {
                    MarkerOptions markerOptions = new MarkerOptions();

                    markerOptions.position(latLng);

                    if (type == "commute"){
                        markerOptions.title("Commute Destination");
                    } else if (type == "delivery"){
                        markerOptions.title("Delivery Destination");
                    } else if (type == "pickup"){
                        markerOptions.title("Pickup Destination");
                    }

                    latitudeInfoPointTwo= (latLng.latitude);
                    longitudeInfoPointTwo = (latLng.longitude);
                    pointTwoType = type;
                    gMap.clear();

                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                    gMap.addMarker(markerOptions);
                    if (latitudeInfoPointOne != null && pointOneType.equalsIgnoreCase(type)){
                        LatLng temp = new LatLng(latitudeInfoPointOne, longitudeInfoPointOne);
                        MarkerOptions markerOptionsTemp = new MarkerOptions();
                        markerOptionsTemp.position(temp);
                        if (type == "commute"){
                            markerOptionsTemp.title("Current Location");
                        } else if (type == "delivery"){
                            markerOptionsTemp.title("Establishment Location");
                        } else if (type == "pickup"){
                            markerOptionsTemp.title("Pickup Location");
                        }

                        gMap.addMarker(markerOptionsTemp);
                    }
                }

            }
        });


    }


    public void HidePickup(View view) {
        Pickup = (ExpandableRelativeLayout) findViewById(R.id.Pickup);
        Pickup.collapse();
    }
    public void HideDelivery(View view) {
        Delivery = (ExpandableRelativeLayout) findViewById(R.id.Delivery);
        Delivery.collapse();
    }
    public void HideCommute(View view) {
        Commute = (ExpandableRelativeLayout) findViewById(R.id.Commute);
        Commute.collapse();
    }
    public void ShowCommuteForm(View view) throws InterruptedException {
        Pickup = (ExpandableRelativeLayout) findViewById(R.id.Pickup);
        Commute = (ExpandableRelativeLayout) findViewById(R.id.Commute);
        Delivery = (ExpandableRelativeLayout) findViewById(R.id.Delivery);
        Delivery.collapse();
        Pickup.collapse();
        Commute.toggle();
        point = false;
        if (type != "commute"){
            gMap.clear();
        }
        type = "commute";
    }
    public void ShowDeliveryForm(View view) {
        Pickup = (ExpandableRelativeLayout) findViewById(R.id.Pickup);
        Commute = (ExpandableRelativeLayout) findViewById(R.id.Commute);
        Delivery = (ExpandableRelativeLayout) findViewById(R.id.Delivery);
        Commute.collapse();
        Pickup.collapse();
        Delivery.toggle();
        point = false;
        if (type != "delivery"){
            gMap.clear();
        }
        type = "delivery";
    }
    public void ShowPickupForm(View view) {
        Pickup = (ExpandableRelativeLayout) findViewById(R.id.Pickup);
        Commute = (ExpandableRelativeLayout) findViewById(R.id.Commute);
        Delivery = (ExpandableRelativeLayout) findViewById(R.id.Delivery);
        Commute.collapse();
        Delivery.collapse();
        Pickup.toggle();
        point = false;
        if (type != "pickup"){
            gMap.clear();
        }
        type = "pickup";
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginPage.class));
        finish();
    }


}
