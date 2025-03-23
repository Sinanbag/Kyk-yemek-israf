package com.example.kykdonusum.models;

import java.util.Date;

public class MealRating {
    private String id;
    private String userId;
    private String mealId;
    private float rating;  // 1-5 arası puan
    private String comment;
    private Date date;

    // Firebase için boş constructor
    public MealRating() {
    }

    public MealRating(String userId, String mealId, float rating, String comment) {
        this.userId = userId;
        this.mealId = mealId;
        this.rating = rating;
        this.comment = comment;
        this.date = new Date();
    }

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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
} 