package com.example.kykdonusum;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.kykdonusum.models.UserProfile;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView welcomeTextView;
    private TextView pointsTextView;
    private Button viewMealsButton;
    private Button viewCancelledMealsButton;
    private Button viewAnnouncementsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // ActionBar başlığını kaldır ama menüyü koru
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setElevation(0); // Gölgeyi kaldır
            getSupportActionBar().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        
        // Status bar ayarları
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        welcomeTextView = findViewById(R.id.welcomeTextView);
        pointsTextView = findViewById(R.id.pointsTextView);
        viewMealsButton = findViewById(R.id.viewMealsButton);
        viewCancelledMealsButton = findViewById(R.id.viewCancelledMealsButton);
        viewAnnouncementsButton = findViewById(R.id.viewAnnouncementsButton);

        loadUserProfile();

        viewMealsButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MealListActivity.class);
            startActivity(intent);
        });

        viewCancelledMealsButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CancelledMealsActivity.class);
            startActivity(intent);
        });

        viewAnnouncementsButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AnnouncementsActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserProfile() {
        if (mAuth.getCurrentUser() != null) {
            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).get()
                    .addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            UserProfile profile = dataSnapshot.getValue(UserProfile.class);
                            if (profile != null) {
                                String displayName = profile.getName() != null ? profile.getName() : profile.getEmail();
                                welcomeTextView.setText("Hoş geldiniz, " + displayName);
                                pointsTextView.setText("Cüzdan Bakiyeniz: " + profile.getWalletBalance() + " TL");
                            }
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_profile) {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            return true;
        } else if (itemId == R.id.action_change_password) {
            startActivity(new Intent(HomeActivity.this, ChangePasswordActivity.class));
            return true;
        } else if (itemId == R.id.action_logout) {
            mAuth.signOut();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
