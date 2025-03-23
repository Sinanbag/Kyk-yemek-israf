package com.example.kykdonusum;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Meal {
    private String id;
    private Date date;
    private String type;
    private String menu;
    private double price;
    private boolean cancelled;

    // Firebase için boş constructor
    public Meal() {
    }

    public Meal(String dateStr, String mealType, String menu, double price) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        try {
            this.date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            this.date = new Date(); // Fallback to current date if parsing fails
        }
        this.type = mealType;
        this.menu = menu;
        this.price = price;
        this.cancelled = false;
    }

    // Getter ve Setter metodları
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}