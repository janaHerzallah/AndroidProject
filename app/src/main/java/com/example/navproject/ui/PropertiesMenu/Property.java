package com.example.navproject.ui.PropertiesMenu;

import java.io.Serializable;

public class Property implements Serializable {
    public int id;
    public String title, description, location, type, imageUrl, area;
    public int price, bedrooms, bathrooms;

    public Property(int id, String title, String description, String location, String type,
                    int price, String area, int bedrooms, int bathrooms, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.price = price;
        this.area = area;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.imageUrl = imageUrl;
    }

    public Object getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }

    public String getArea() {
        return area;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
