package com.example.productapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FrameLayout fragmentContainer;
    FirebaseDatabase firebase;
    ProductAdapter adapter;
    FirebaseAuth mAuth;
    FirebaseUser user;
    TextView menuEmail;
    MenuItem signOut;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    RecyclerView list;
    List<Product> productList;

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        fragmentContainer = findViewById(R.id.fragmentContainer);
        user = mAuth.getCurrentUser();
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        menuEmail = navigationView.getHeaderView(0).findViewById(R.id.menuEmail);
        toolbar = findViewById(R.id.topAppBar);
        signOut = navigationView.getMenu().findItem(R.id.sign_out);
        menuEmail.setText(String.valueOf(user.getEmail()));
        list = findViewById(R.id.list);
        productList = new ArrayList<>();
        adapter = new ProductAdapter(productList);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);
        firebase = FirebaseDatabase.getInstance("https://productlkw-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference productsRef = firebase.getReference("products");
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    productList.add(product);
                }
                // Notify the adapter that the data set has changed
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to retrieve products", Toast.LENGTH_SHORT).show();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle drawer open/close
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    drawerLayout.openDrawer(navigationView);
                }
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                boolean closeDrawer = false;
                if (id == R.id.sign_out) {
                    mAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    closeDrawer = false;
                }
                else if (id == R.id.uploadProduct) {
                    startActivity(new Intent(MainActivity.this, UploadProduct.class));
                    closeDrawer = true;
                }
                else if (id == R.id.manageProduct){
                    list.setVisibility(View.GONE);
                    fragmentContainer.setVisibility(View.VISIBLE);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, new ManageProductFragment()) // Replace with your fragment class
                            .commit();
                    closeDrawer = true;
                }
                else if(id == R.id.viewProduct){
                    fragmentContainer.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, new ManageProductFragment()) // Replace with your fragment class
                            .commit();
                    closeDrawer = true;
                }
                if (closeDrawer) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                return true;
            }
        });

    }
    private void loadProducts() {
        DatabaseReference productsRef = firebase.getReference("products");
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    productList.add(product);
                }
                // Notify the adapter that the data set has changed
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to retrieve products", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        MenuItem menuItem = menu.findItem(R.id.searchIcon);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type to Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}