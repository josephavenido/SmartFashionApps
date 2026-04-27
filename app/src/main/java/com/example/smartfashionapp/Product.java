package com.example.smartfashionapp;

public class Product {

    private String name;
    private String price;
    private String image;

    public Product() {}

    public Product(String name, String price, String image) {
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getImage() { return image; }

    public void setName(String name) { this.name = name; }
    public void setPrice(String price) { this.price = price; }
    public void setImage(String image) { this.image = image; }
}