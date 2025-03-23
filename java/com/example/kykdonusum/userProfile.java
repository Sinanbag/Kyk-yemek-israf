package com.example.kykdonusum;

import java.util.ArrayList;
import java.util.List;

public class userProfile {
    private String name;
    private String phone;
    private String email;
    private String studentId; // Öğrenci numarası
    private String dormitory; // Yurt bilgisi
    private String roomNumber; // Oda numarası
    private double walletBalance; // İade edilen yemek parası bakiyesi
    private List<String> cancelledMeals; // İptal edilen yemeklerin ID'leri

    // Firebase için boş constructor
    public userProfile() {
        this.walletBalance = 0.0;
        this.cancelledMeals = new ArrayList<>();
    }

    public userProfile(String name, String phone, String email, String studentId) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.studentId = studentId;
        this.walletBalance = 0.0;
        this.cancelledMeals = new ArrayList<>();
    }

    // Getter ve Setter metodları
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getDormitory() {
        return dormitory;
    }

    public void setDormitory(String dormitory) {
        this.dormitory = dormitory;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(double walletBalance) {
        this.walletBalance = walletBalance;
    }

    public List<String> getCancelledMeals() {
        return cancelledMeals;
    }

    public void setCancelledMeals(List<String> cancelledMeals) {
        this.cancelledMeals = cancelledMeals;
    }

    // Yardımcı metodlar
    public void addToWallet(double amount) {
        this.walletBalance += amount;
    }

    public boolean withdrawFromWallet(double amount) {
        if (this.walletBalance >= amount) {
            this.walletBalance -= amount;
            return true;
        }
        return false;
    }

    public void addCancelledMeal(String mealId) {
        if (this.cancelledMeals == null) {
            this.cancelledMeals = new ArrayList<>();
        }
        this.cancelledMeals.add(mealId);
    }
}
