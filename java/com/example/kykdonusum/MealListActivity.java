package com.example.kykdonusum;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kykdonusum.adapters.MealAdapter;
import com.example.kykdonusum.models.Meal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MealListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MealAdapter adapter;
    private List<Meal> meals;
    private ProgressBar progressBar;
    private TextView emptyView;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_list);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId).child("meals");

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        meals = new ArrayList<>();
        adapter = new MealAdapter(this, meals, userId);
        recyclerView.setAdapter(adapter);

        // Setup ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Yemek Listesi");
        }

        // Load meals
        loadMeals();
    }

    private void loadMeals() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                meals.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Meal meal = snapshot.getValue(Meal.class);
                    if (meal != null) {
                        meal.setId(snapshot.getKey());
                        meals.add(meal);
                    }
                }

                progressBar.setVisibility(View.GONE);

                if (meals.isEmpty()) {
                    // If no meals exist, add test meals
                    addTestMeals();
                } else {
                    adapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                emptyView.setText("Veri yüklenirken hata oluştu");
                emptyView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void addTestMeals() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userMealsRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId).child("meals");

        // Create test meals
        List<Meal> testMeals = new ArrayList<>();

        // Past meals - 2 days ago
        Meal meal1 = new Meal("20.03.2024", "Öğle Yemeği", "Mercimek Çorbası, Pilav, Tavuk Şiş, Salata", 25.0f);
        meal1.setId("meal1");
        meal1.setCancelled(false);
        testMeals.add(meal1);

        // Past meals - 1 day ago
        Meal meal2 = new Meal("21.03.2024", "Akşam Yemeği", "Ezogelin Çorbası, Kuru Fasulye, Bulgur Pilavı, Cacık", 25.0f);
        meal2.setId("meal2");
        meal2.setCancelled(false);
        testMeals.add(meal2);

        // Today's meals
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String today = dateFormat.format(new Date());

        Meal meal3 = new Meal(today, "Kahvaltı", "Zeytin, Peynir, Yumurta, Bal, Reçel, Çay", 20.0f);
        meal3.setId("meal3");
        meal3.setCancelled(false);
        testMeals.add(meal3);

        Meal meal4 = new Meal(today, "Öğle Yemeği", "Tarhana Çorbası, Mantı, Ayran", 25.0f);
        meal4.setId("meal4");
        meal4.setCancelled(false);
        testMeals.add(meal4);

        // Tomorrow's meal
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        String tomorrow = dateFormat.format(cal.getTime());

        Meal meal5 = new Meal(tomorrow, "Öğle Yemeği", "Domates Çorbası, İzmir Köfte, Pilav, Salata", 25.0f);
        meal5.setId("meal5");
        meal5.setCancelled(false);
        testMeals.add(meal5);

        // Add all test meals to Firebase
        for (Meal meal : testMeals) {
            userMealsRef.child(meal.getId()).setValue(meal)
                .addOnSuccessListener(aVoid -> {
                    // Meal added successfully
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Failed to add meal
                });
        }
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