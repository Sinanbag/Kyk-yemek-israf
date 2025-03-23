package com.example.kykdonusum;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kykdonusum.adapters.CancelledMealAdapter;
import com.example.kykdonusum.models.Meal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CancelledMealsActivity extends AppCompatActivity implements CancelledMealAdapter.OnMealRestoreListener {
    private RecyclerView recyclerView;
    private CancelledMealAdapter adapter;
    private List<Meal> cancelledMeals;
    private TextView emptyView;
    private TextView totalRefundText;
    private DatabaseReference mDatabase;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelled_meals);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize views
        recyclerView = findViewById(R.id.cancelledMealsRecyclerView);
        emptyView = findViewById(R.id.emptyView);
        totalRefundText = findViewById(R.id.totalRefundText);

        // Initialize RecyclerView
        cancelledMeals = new ArrayList<>();
        adapter = new CancelledMealAdapter(this, cancelledMeals, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Load cancelled meals
        loadCancelledMeals();
    }

    private void loadCancelledMeals() {
        mDatabase.child("users").child(userId).child("meals")
                .orderByChild("cancelled")
                .equalTo(true)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        cancelledMeals.clear();
                        float totalRefund = 0;

                        for (DataSnapshot mealSnapshot : dataSnapshot.getChildren()) {
                            Meal meal = mealSnapshot.getValue(Meal.class);
                            if (meal != null) {
                                meal.setId(mealSnapshot.getKey());
                                cancelledMeals.add(meal);
                                totalRefund += meal.getPrice();
                            }
                        }

                        // Update UI
                        adapter.notifyDataSetChanged();
                        totalRefundText.setText(String.format("Toplam İade: %.2f TL", totalRefund));
                        
                        // Show/hide empty view
                        if (cancelledMeals.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(CancelledMealsActivity.this, 
                            "Veriler yüklenirken hata oluştu: " + databaseError.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onMealRestore(final Meal meal) {
        // Update the meal's cancelled status in Firebase
        mDatabase.child("users").child(userId).child("meals").child(meal.getId())
                .child("cancelled").setValue(false)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CancelledMealsActivity.this, 
                        "Yemek başarıyla geri alındı", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CancelledMealsActivity.this,
                        "Yemek geri alınamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Revert the change in the local list
                    meal.setCancelled(true);
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 