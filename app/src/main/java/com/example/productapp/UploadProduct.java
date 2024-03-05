package com.example.productapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadProduct extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase firebase;
    DatabaseReference database;
    TextInputEditText productName, productPrice, productDescription;
    Button uploadImage, confirm;
    ImageView selectedImage;
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_product);
        firebase = FirebaseDatabase.getInstance("https://productlkw-default-rtdb.asia-southeast1.firebasedatabase.app/");
        database = firebase.getReference();
        mAuth = FirebaseAuth.getInstance();
        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        selectedImage = findViewById(R.id.selectedImage);
        productDescription = findViewById(R.id.product_description);
        uploadImage = findViewById(R.id.uploadImage);
        confirm = findViewById(R.id.confirm_button);
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
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedImageUri == null){
                    Toast.makeText(getApplicationContext(),"Please upload an image", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    String name = productName.getText().toString();
                    String priceStr = productPrice.getText().toString();
                    String description = productDescription.getText().toString();
                    String userID = mAuth.getCurrentUser().getUid();
                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(description)) {
                        Toast.makeText(UploadProduct.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Create a unique ID for the product
                    String productID = database.child("products").push().getKey();
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("product_images").child(productID);
                    UploadTask uploadTask = storageRef.putFile(selectedImageUri);
                    double price = Double.parseDouble(priceStr);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // image upload success grab image download link
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Product product = new Product();
                                    product.setId(productID);
                                    product.setName(name);
                                    product.setPrice(price);
                                    product.setDescription(description);
                                    product.setImgURL(uri.toString());
                                    product.setUserID(userID);
                                    database.child("products").child(productID).setValue(product);
                                    // Show success message
                                    Toast.makeText(UploadProduct.this, "Product uploaded successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
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
            selectedImageUri = data.getData();
            Picasso.get().load(selectedImageUri)
                    .resize(250, 250)
                    .centerCrop()
                    .into(selectedImage);
        }
    }
}