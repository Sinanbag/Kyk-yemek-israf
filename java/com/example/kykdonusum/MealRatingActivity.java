package com.example.kykdonusum;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kykdonusum.models.MealRating;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MealRatingActivity extends AppCompatActivity {
    private RatingBar ratingBar;
    private EditText commentEditText;
    private Button submitButton;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String mealId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_rating);

        // Get mealId from intent
        mealId = getIntent().getStringExtra("meal_id");
        if (mealId == null) {
            Toast.makeText(this, "Yemek bilgisi bulunamadı", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        ratingBar = findViewById(R.id.ratingBar);
        commentEditText = findViewById(R.id.commentEditText);
        submitButton = findViewById(R.id.submitButton);

        // Setup ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Yemek Değerlendir");
        }

        // Setup submit button
        submitButton.setOnClickListener(v -> submitRating());
    }

    private void submitRating() {
        float rating = ratingBar.getRating();
        String comment = commentEditText.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Lütfen puan verin", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        MealRating mealRating = new MealRating(userId, mealId, rating, comment);

        // Save to Firebase
        String ratingId = mDatabase.child("meal_ratings").push().getKey();
        if (ratingId != null) {
            mealRating.setId(ratingId);
            mDatabase.child("meal_ratings").child(ratingId).setValue(mealRating)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MealRatingActivity.this, 
                                "Değerlendirmeniz kaydedildi", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> 
                        Toast.makeText(MealRatingActivity.this,
                                "Hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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