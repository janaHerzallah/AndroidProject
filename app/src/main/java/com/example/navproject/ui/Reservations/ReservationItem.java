package com.example.navproject.ui.Reservations;

import com.example.navproject.ui.PropertiesMenu.Property;

public class ReservationItem {
    public Property property;
    public String timestamp;

    public ReservationItem(Property property, String timestamp) {
        this.property = property;
        this.timestamp = timestamp;
    }
}
