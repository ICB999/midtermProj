package com.example.productapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class UploadProduct extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase firebase;
    TextInputEditText productName, productPrice, productDescription;
    Button uploadImage, confirm;
    ImageView selectedImage;
    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_product);
        firebase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getUid();
        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        selectedImage = findViewById(R.id.selectedImage);
        productDescription = findViewById(R.id.product_description);
        uploadImage = findViewById(R.id.uploadImage);
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
//                String name = String.valueOf(productName);
//                String price = String.valueOf(productPrice);
//                String description = String.valueOf(productDescription);
//                if(TextUtils.isEmpty(name)){
//                    Toast.makeText(UploadProduct.this,"Enter product name", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if(TextUtils.isEmpty(price)){
//                    Toast.makeText(UploadProduct.this,"Enter product price", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if(TextUtils.isEmpty(description)){
//                    Toast.makeText(UploadProduct.this,"Enter product description", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the URI of the selected image
            // Note: You may need to handle permission requests for accessing external storage
            // The URI can be used to load the image into an ImageView or upload it to a server
            // For simplicity, let's just print the URI here
            String selectedImageUri = data.getData().toString();
            Picasso.get().load(selectedImageUri)
                    .resize(250,250)
                    .centerCrop()
                    .into(selectedImage);
        }
    }
}