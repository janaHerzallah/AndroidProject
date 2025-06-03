package com.example.navproject.ui.PropertiesMenu;

public class Property {
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
}
