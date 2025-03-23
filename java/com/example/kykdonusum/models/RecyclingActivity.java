package com.example.kykdonusum.models;

import java.util.Date;

public class RecyclingActivity {
    private String id;
    private String userId;
    private String type; // PLASTIC, PAPER, GLASS, METAL
    private double weight; // kilogram cinsinden
    private int points;
    private long timestamp;
    private String status; // PENDING, APPROVED, REJECTED

    // Firebase için boş constructor
    public RecyclingActivity() {
    }

    public RecyclingActivity(String userId, String type, double weight) {
        this.userId = userId;
        this.type = type;
        this.weight = weight;
        this.timestamp = new Date().getTime();
        this.status = "PENDING";
        calculatePoints();
    }

    private void calculatePoints() {
        // Materyal tipine göre puan hesaplama
        switch (type) {
            case "PLASTIC":
                this.points = (int) (weight * 10); // 1 kg plastik = 10 puan
                break;
            case "PAPER":
                this.points = (int) (weight * 5);  // 1 kg kağıt = 5 puan
                break;
            case "GLASS":
                this.points = (int) (weight * 8);  // 1 kg cam = 8 puan
                break;
            case "METAL":
                this.points = (int) (weight * 15); // 1 kg metal = 15 puan
                break;
            default:
                this.points = 0;
        }
    }

    // Getter ve Setter metodları
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
        calculatePoints();
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 