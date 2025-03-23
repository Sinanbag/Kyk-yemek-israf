package com.example.kykdonusum.models;

public class Reward {
    private String id;
    private String title;
    private String description;
    private int pointsCost;
    private boolean isAvailable;
    private String imageUrl;

    // Firebase için boş constructor
    public Reward() {
    }

    public Reward(String title, String description, int pointsCost) {
        this.title = title;
        this.description = description;
        this.pointsCost = pointsCost;
        this.isAvailable = true;
    }

    // Getter ve Setter metodları
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPointsCost() {
        return pointsCost;
    }

    public void setPointsCost(int pointsCost) {
        this.pointsCost = pointsCost;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
} 