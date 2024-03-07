package com.example.productapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoryFragment extends Fragment {
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private String categoryName;
    FirebaseDatabase firebase = FirebaseDatabase.getInstance("https://productlkw-default-rtdb.asia-southeast1.firebasedatabase.app/");
    public CategoryFragment() {
    }
    public static CategoryFragment newInstance(String categoryName) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString("categoryName", categoryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            categoryName = getArguments().getString("categoryName");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category_fragment, container, false);
        productList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.productList);
        adapter = new ProductAdapter(productList);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setAdapter(adapter);
        mAuth = FirebaseAuth.getInstance();
        if(Objects.equals(categoryName, "All")){
            loadAllProducts();
        }else {
            loadProductsForCategory(categoryName);
        }
        return view;
    }
    private void loadAllProducts(){
        DatabaseReference productsRef = firebase.getReference("products");
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot eachProduct: snapshot.getChildren()){
                    Product product = eachProduct.getValue(Product.class);
                    if(!product.getUserID().equals(mAuth.getCurrentUser().getUid())){
                        productList.add(product);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void loadProductsForCategory(String categoryName) {
        DatabaseReference categoryRef = firebase.getReference("categories").child(categoryName);
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> productIDs = new ArrayList<>();
                for(DataSnapshot eachProductID : snapshot.getChildren()){
                    String productId = eachProductID.getKey();
                    productIDs.add(productId);
                }
                fetchProducts(productIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void fetchProducts(List<String> productIds){
        DatabaseReference productsRef = firebase.getReference("products");
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for(String productId : productIds){
                    DataSnapshot eachProduct = snapshot.child(productId);
                    if(eachProduct.exists()){
                        Product product = eachProduct.getValue(Product.class);
                        if(!product.getUserID().equals(mAuth.getCurrentUser().getUid())){
                            productList.add(product);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(MainActivity.this, "Failed to fetch product details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
