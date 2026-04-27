package com.example.smartfashionapp;

import android.content.Context;
import android.view.*;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductAdapter extends BaseAdapter {

    Context context;
    ArrayList<Product> list;

    public ProductAdapter(Context context, ArrayList<Product> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() { return list.size(); }

    @Override
    public Object getItem(int position) { return list.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);

        ImageView image = view.findViewById(R.id.image);
        TextView name = view.findViewById(R.id.name);
        TextView price = view.findViewById(R.id.price);
        Button buyBtn = view.findViewById(R.id.buyBtn);

        Product p = list.get(position);

        name.setText(p.getName());
        price.setText("₱" + p.getPrice());

        Glide.with(context).load(p.getImage()).into(image);

        buyBtn.setOnClickListener(v -> {

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            HashMap<String, Object> cartItem = new HashMap<>();
            cartItem.put("name", p.getName());
            cartItem.put("price", p.getPrice());
            cartItem.put("image", p.getImage());
            cartItem.put("quantity", 1);

            db.collection("cart")
                    .document(userId)
                    .collection("items")
                    .add(cartItem);

            Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}