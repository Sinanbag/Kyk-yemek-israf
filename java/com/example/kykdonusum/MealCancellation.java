package com.example.kykdonusum;

import java.util.Date;

public class MealCancellation {
    private String id;
    private String userId;
    private String mealId;
    private String date;
    private String mealType;
    private double refundAmount;
    private String status; // PENDING, APPROVED, REJECTED
    private long timestamp;

    // Firebase için boş constructor
    public MealCancellation() {
    }

    public MealCancellation(String userId, String mealId, String date, String mealType, double refundAmount) {
        this.userId = userId;
        this.mealId = mealId;
        this.date = date;
        this.mealType = mealType;
        this.refundAmount = refundAmount;
        this.status = "PENDING";
        this.timestamp = new Date().getTime();
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

    public String getMealId() {
        return mealId;
    }

    public void setMealId(String mealId) {
        this.mealId = mealId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}