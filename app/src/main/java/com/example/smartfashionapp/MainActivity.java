package com.example.smartfashionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.*;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    GridView gridView;
    Button cartBtn;

    ArrayList<Product> list;
    ProductAdapter adapter;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = findViewById(R.id.gridView);
        cartBtn = findViewById(R.id.cartBtn);

        list = new ArrayList<>();
        adapter = new ProductAdapter(this, list);
        gridView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // LOAD PRODUCTS
        db.collection("products")
                .addSnapshotListener((value, error) -> {

                    list.clear();

                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Product p = doc.toObject(Product.class);
                        list.add(p);
                    }

                    adapter.notifyDataSetChanged();
                });

        // 🛒 OPEN CART
        cartBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CartActivity.class));
        });
    }
}