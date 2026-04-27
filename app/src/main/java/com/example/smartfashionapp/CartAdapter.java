package com.example.smartfashionapp;

import android.content.Context;
import android.view.*;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;

public class CartAdapter extends BaseAdapter {

    Context context;
    ArrayList<Product> list;
    ArrayList<String> docIds;

    String userId;
    FirebaseFirestore db;

    public CartAdapter(Context context, ArrayList<Product> list, ArrayList<String> docIds) {
        this.context = context;
        this.list = list;
        this.docIds = docIds;

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public int getCount() { return list.size(); }

    @Override
    public Object getItem(int position) { return list.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);

        ImageView image = view.findViewById(R.id.image);
        TextView name = view.findViewById(R.id.name);
        TextView price = view.findViewById(R.id.price);

        TextView minusBtn = view.findViewById(R.id.minusBtn);
        TextView plusBtn = view.findViewById(R.id.plusBtn);
        TextView qty = view.findViewById(R.id.qty);

        Button deleteBtn = view.findViewById(R.id.deleteBtn);
        Button updateBtn = view.findViewById(R.id.updateBtn);

        Product p = list.get(position);

        int basePrice = 0;

        if (p != null) {
            name.setText(p.getName());

            try {
                basePrice = Integer.parseInt(p.getPrice());
            } catch (Exception e) {}

            Glide.with(context).load(p.getImage()).into(image);
        }

        final int finalBasePrice = basePrice;

        String docId = docIds.get(position);

        // 🔥 LOAD QUANTITY
        db.collection("cart")
                .document(userId)
                .collection("items")
                .document(docId)
                .get()
                .addOnSuccessListener(doc -> {

                    int q = 1;

                    try {
                        q = Integer.parseInt(doc.get("quantity").toString());
                    } catch (Exception e) {}

                    qty.setText(String.valueOf(q));
                    price.setText("₱" + (finalBasePrice * q));
                });

        // ➕
        plusBtn.setOnClickListener(v -> {

            db.collection("cart")
                    .document(userId)
                    .collection("items")
                    .document(docId)
                    .get()
                    .addOnSuccessListener(doc -> {

                        int q = 1;

                        try {
                            q = Integer.parseInt(doc.get("quantity").toString());
                        } catch (Exception e) {}

                        q++;

                        db.collection("cart")
                                .document(userId)
                                .collection("items")
                                .document(docId)
                                .update("quantity", q);
                    });
        });

        // ➖
        minusBtn.setOnClickListener(v -> {

            db.collection("cart")
                    .document(userId)
                    .collection("items")
                    .document(docId)
                    .get()
                    .addOnSuccessListener(doc -> {

                        int q = 1;

                        try {
                            q = Integer.parseInt(doc.get("quantity").toString());
                        } catch (Exception e) {}

                        if (q > 1) q--;

                        db.collection("cart")
                                .document(userId)
                                .collection("items")
                                .document(docId)
                                .update("quantity", q);
                    });
        });

        // DELETE
        deleteBtn.setOnClickListener(v -> {
            db.collection("cart")
                    .document(userId)
                    .collection("items")
                    .document(docId)
                    .delete();
        });

        // CHANGE ITEM
        updateBtn.setOnClickListener(v -> {

            db.collection("products").get()
                    .addOnSuccessListener(query -> {

                        ArrayList<String> names = new ArrayList<>();
                        ArrayList<Product> products = new ArrayList<>();

                        for (DocumentSnapshot doc : query.getDocuments()) {
                            Product prod = doc.toObject(Product.class);

                            if (prod != null) {
                                products.add(prod);
                                names.add(prod.getName() + " - ₱" + prod.getPrice());
                            }
                        }

                        String[] items = names.toArray(new String[0]);

                        new android.app.AlertDialog.Builder(context)
                                .setTitle("Change Item")
                                .setItems(items, (dialog, which) -> {

                                    Product selected = products.get(which);

                                    db.collection("cart")
                                            .document(userId)
                                            .collection("items")
                                            .document(docId)
                                            .update(
                                                    "name", selected.getName(),
                                                    "price", selected.getPrice(),
                                                    "image", selected.getImage()
                                            );
                                })
                                .show();
                    });
        });

        return view;
    }
}