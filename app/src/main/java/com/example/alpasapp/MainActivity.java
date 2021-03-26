package com.example.alpasapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    TextView fullName,email,phone;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    Button changeProfileImage, viewOrderBtn, makeOrderBtn, viewMyOrderBtn, rightBtn, leftBtn, btnOne, btnTwo, btnThree;
    ImageView profileImage;
    StorageReference storageReference;
    static String section = "1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fullName = findViewById(R.id.userName);
        phone = findViewById(R.id.userPhone);
        email = findViewById(R.id.userEmail);


        viewOrderBtn = findViewById(R.id.viewOrderBtn);
        makeOrderBtn = findViewById(R.id.makeOrderBtn);
        viewMyOrderBtn = findViewById(R.id.viewMyOrdersBtn);
        rightBtn = findViewById(R.id.rightbtn);
        leftBtn = findViewById(R.id.leftbtn);
        btnOne = findViewById(R.id.btnone);
        btnTwo = findViewById(R.id.btntwo);
        btnThree = findViewById(R.id.btnthree);

        profileImage = findViewById(R.id.profileImage);
        changeProfileImage = findViewById(R.id.profileBtn);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");



        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri).into(profileImage);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);

                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                        Toast.makeText(MainActivity.this, "Successfully Added Profile Picture", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



        userId = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                phone.setText(documentSnapshot.getString("phone"));
                fullName.setText(documentSnapshot.getString("fName"));
                email.setText(documentSnapshot.getString("email"));
            }
        });

        changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);

            }
        });

        viewOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),OrderLists.class));
                finish();
            }
        });

        viewMyOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PersonalOrdersPage.class));
                finish();
            }
        });

        makeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserOrderingPage.class));
                finish();
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable white = getDrawable(R.drawable.uielement1);
                Drawable red = getDrawable(R.drawable.uielement2);
                if (section.equalsIgnoreCase("1")){
                    viewOrderBtn.setVisibility(View.INVISIBLE);
                    viewMyOrderBtn.setVisibility(View.VISIBLE);
                    makeOrderBtn.setVisibility(View.INVISIBLE);
                    btnOne.setBackground(white);
                    btnTwo.setBackground(red);
                    btnThree.setBackground(white);
                    section = "2";
                } else if (section.equalsIgnoreCase("2")){
                    viewOrderBtn.setVisibility(View.VISIBLE);
                    viewMyOrderBtn.setVisibility(View.INVISIBLE);
                    makeOrderBtn.setVisibility(View.INVISIBLE);
                    btnOne.setBackground(white);
                    btnTwo.setBackground(white);
                    btnThree.setBackground(red);
                    section = "3";
                } else if (section.equalsIgnoreCase("3")){
                    viewOrderBtn.setVisibility(View.INVISIBLE);
                    viewMyOrderBtn.setVisibility(View.INVISIBLE);
                    makeOrderBtn.setVisibility(View.VISIBLE);
                    btnOne.setBackground(red);
                    btnTwo.setBackground(white);
                    btnThree.setBackground(white);
                    section = "1";
                }
            }
        });
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable white = getDrawable(R.drawable.uielement1);
                Drawable red = getDrawable(R.drawable.uielement2);
                if (section.equalsIgnoreCase("1")){
                    viewOrderBtn.setVisibility(View.VISIBLE);
                    viewMyOrderBtn.setVisibility(View.INVISIBLE);
                    makeOrderBtn.setVisibility(View.INVISIBLE);
                    btnOne.setBackground(white);
                    btnTwo.setBackground(white);
                    btnThree.setBackground(red);
                    section = "3";
                } else if (section.equalsIgnoreCase("2")){
                    viewOrderBtn.setVisibility(View.INVISIBLE);
                    viewMyOrderBtn.setVisibility(View.INVISIBLE);
                    makeOrderBtn.setVisibility(View.VISIBLE);
                    btnOne.setBackground(red);
                    btnTwo.setBackground(white);
                    btnThree.setBackground(white);
                    section = "1";
                } else if (section.equalsIgnoreCase("3")){
                    viewOrderBtn.setVisibility(View.INVISIBLE);
                    viewMyOrderBtn.setVisibility(View.VISIBLE);
                    makeOrderBtn.setVisibility(View.INVISIBLE);
                    btnOne.setBackground(white);
                    btnTwo.setBackground(red);
                    btnThree.setBackground(white);
                    section = "2";
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000){
            if(resultCode == Activity.RESULT_OK);
                Uri imageUri = data.getData();
                profileImage.setImageURI(imageUri);

                uploadImageToFireBase(imageUri);

        }
    }
    private void uploadImageToFireBase(Uri imageUri) {
        // upload image to firebase storage
        StorageReference fileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), RegisterPage.class));
        finish();
    }

}