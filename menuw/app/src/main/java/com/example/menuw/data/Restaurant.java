package com.example.menuw.data;

public class Restaurant {
    private int id;
    private String name;
    private String style;
    private String location;
    private float minOrder;
    private String image;

    public Restaurant() {
    }

    public Restaurant(int id, String name, String style, String location, float minOrder, String image) {
        this.id = id;
        this.name = name;
        this.style = style;
        this.location = location;
        this.minOrder = minOrder;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStyle() {
        return style;
    }

    public String getLocation() {
        return location;
    }

    public float getMinOrder() {
        return minOrder;
    }

    public String getImage() {
        return image;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setMinOrder(float minOrder) {
        this.minOrder = minOrder;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
