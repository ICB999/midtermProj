package com.example.productapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

public class ProductViewHolder extends RecyclerView.ViewHolder{
    ImageView productImageView;
    TextView productNameTextView;
    TextView productPriceTextView;
    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        productImageView = itemView.findViewById(R.id.productImage);
        productNameTextView = itemView.findViewById(R.id.productName);
        productPriceTextView = itemView.findViewById(R.id.productPrice);
    }

    public void bind(Product product) {
        // Bind data to views
        Picasso.get().load(product.getImgURL()).into(productImageView);
        productNameTextView.setText(product.getName());
        productPriceTextView.setText(String.valueOf(product.getPrice()));
    }
}
