package com.example.menuw.data;

public class Order {
    private int id;
    private String fooditem;
    private String restaurant;
    private int price;
    private String image;
    private int units;

    public Order() {

    }

    public Order(int id, String fooditem, String restaurant, int price, String image, int units) {
        this.id = id;
        this.fooditem = fooditem;
        this.restaurant = restaurant;
        this.price = price;
        this.image = image;
        this.units = units;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFooditem() {
        return fooditem;
    }

    public void setFooditem(String fooditem) {
        this.fooditem = fooditem;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }
}

