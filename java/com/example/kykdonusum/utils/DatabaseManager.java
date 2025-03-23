package com.example.kykdonusum.utils;

import com.example.kykdonusum.models.Meal;
import com.example.kykdonusum.models.MealCancellation;
import com.example.kykdonusum.models.RecyclingActivity;
import com.example.kykdonusum.models.Reward;
import com.example.kykdonusum.models.UserProfile;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final DatabaseReference database;
    private final FirebaseAuth auth;

    private DatabaseManager() {
        database = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // Kullanıcı işlemleri
    public Task<Void> updateUserProfile(UserProfile profile) {
        return database.child("users")
                .child(auth.getCurrentUser().getUid())
                .setValue(profile);
    }

    public Task<Void> updateUserWallet(double amount) {
        return database.child("users")
                .child(auth.getCurrentUser().getUid())
                .child("walletBalance")
                .setValue(amount);
    }

    // Geri dönüşüm aktivite işlemleri
    public Task<Void> addRecyclingActivity(RecyclingActivity activity) {
        String key = database.child("recycling_activities").push().getKey();
        activity.setId(key);
        return database.child("recycling_activities")
                .child(key)
                .setValue(activity);
    }

    public Query getUserRecyclingActivities() {
        return database.child("recycling_activities")
                .orderByChild("userId")
                .equalTo(auth.getCurrentUser().getUid());
    }

    public Query getPendingRecyclingActivities() {
        return database.child("recycling_activities")
                .orderByChild("status")
                .equalTo("PENDING");
    }

    // Ödül işlemleri
    public Task<Void> addReward(Reward reward) {
        String key = database.child("rewards").push().getKey();
        reward.setId(key);
        return database.child("rewards")
                .child(key)
                .setValue(reward);
    }

    public Query getAvailableRewards() {
        return database.child("rewards")
                .orderByChild("available")
                .equalTo(true);
    }

    public Task<Void> claimReward(String rewardId) {
        // Ödülü kullanıcının listesine ekle
        return database.child("users")
                .child(auth.getCurrentUser().getUid())
                .child("claimedRewards")
                .push()
                .setValue(rewardId);
    }

    // Yemek işlemleri
    public Task<Void> addMeal(Meal meal) {
        String key = database.child("meals").push().getKey();
        meal.setId(key);
        return database.child("meals")
                .child(key)
                .setValue(meal);
    }

    public Query getMealsByDate(String date) {
        return database.child("meals")
                .orderByChild("date")
                .equalTo(date);
    }

    public Query getMealsByDateRange(String startDate, String endDate) {
        return database.child("meals")
                .orderByChild("date")
                .startAt(startDate)
                .endAt(endDate);
    }

    // Yemek iptal işlemleri
    public Task<Void> cancelMeal(MealCancellation cancellation) {
        String key = database.child("meal_cancellations").push().getKey();
        cancellation.setId(key);
        return database.child("meal_cancellations")
                .child(key)
                .setValue(cancellation)
                .continueWithTask(task -> {
                    // Kullanıcının iptal listesine ekle
                    return database.child("users")
                            .child(cancellation.getUserId())
                            .child("cancelledMeals")
                            .push()
                            .setValue(cancellation.getMealId());
                });
    }

    public Query getUserCancellations() {
        return database.child("meal_cancellations")
                .orderByChild("userId")
                .equalTo(auth.getCurrentUser().getUid());
    }

    public Query getPendingCancellations() {
        return database.child("meal_cancellations")
                .orderByChild("status")
                .equalTo("PENDING");
    }

    // Yurt ve oda bilgisi güncelleme
    public Task<Void> updateDormitoryInfo(String dormitory, String roomNumber) {
        return database.child("users")
                .child(auth.getCurrentUser().getUid())
                .child("dormitory")
                .setValue(dormitory)
                .continueWithTask(task -> 
                    database.child("users")
                            .child(auth.getCurrentUser().getUid())
                            .child("roomNumber")
                            .setValue(roomNumber)
                );
    }
} 