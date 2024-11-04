package com.example.menuw.data;

import java.util.List;

public class FoodItem {
    private int id;
    private String dishName;
    private String restaurant;
    private List<String> mainIngredients;
    private float price;
    private String image;

    public FoodItem(int id, String dishName, String restaurant, List<String> mainIngredients, float price, String image) {
        this.id = id;
        this.dishName = dishName;
        this.restaurant = restaurant;
        this.mainIngredients = mainIngredients;
        this.price = price;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getDishName() {
        return dishName;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public List<String> getMainIngredients() {
        return mainIngredients;
    }

    public float getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public void setMainIngredients(List<String> mainIngredients) {
        this.mainIngredients = mainIngredients;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
