package com.example.smartfashionapp;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    ListView cartList;
    TextView totalText;
    Button checkoutBtn;

    ArrayList<Product> list;
    ArrayList<String> docIds;

    CartAdapter adapter;

    FirebaseFirestore db;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartList = findViewById(R.id.cartList);
        totalText = findViewById(R.id.total);
        checkoutBtn = findViewById(R.id.checkoutBtn);

        list = new ArrayList<>();
        docIds = new ArrayList<>();

        adapter = new CartAdapter(this, list, docIds);
        cartList.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("cart")
                .document(userId)
                .collection("items")
                .addSnapshotListener((value, error) -> {

                    if (value == null) return;

                    list.clear();
                    docIds.clear();

                    int total = 0;

                    for (DocumentSnapshot doc : value.getDocuments()) {

                        Product p = doc.toObject(Product.class);

                        if (p != null) {

                            list.add(p);
                            docIds.add(doc.getId());

                            int price = 0;
                            int qty = 1;

                            try {
                                price = Integer.parseInt(p.getPrice());
                                qty = Integer.parseInt(doc.get("quantity").toString());
                            } catch (Exception e) {}

                            total += price * qty;
                        }
                    }

                    totalText.setText("Total: ₱" + total);
                    adapter.notifyDataSetChanged();
                });

        // CHECKOUT
        checkoutBtn.setOnClickListener(v -> {

            if (docIds.isEmpty()) {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Confirm Checkout")
                    .setMessage("Are you sure you want to checkout?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        for (String id : docIds) {
                            db.collection("cart")
                                    .document(userId)
                                    .collection("items")
                                    .document(id)
                                    .delete();
                        }

                        Toast.makeText(this, "Order placed!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}