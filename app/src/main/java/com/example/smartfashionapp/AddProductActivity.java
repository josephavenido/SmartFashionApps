package com.example.smartfashionapp;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity {

    EditText name, price;
    Button addBtn;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        addBtn = findViewById(R.id.addBtn);

        db = FirebaseFirestore.getInstance();

        addBtn.setOnClickListener(v -> {
            String n = name.getText().toString();
            String p = price.getText().toString();

            HashMap<String, Object> product = new HashMap<>();
            product.put("name", n);
            product.put("price", p);

            db.collection("products")
                    .add(product)
                    .addOnSuccessListener(documentReference ->
                            Toast.makeText(this, "Product Added", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show());
        });
    }
}