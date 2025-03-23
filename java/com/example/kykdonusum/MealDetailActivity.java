package com.example.kykdonusum;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

public class MealDetailActivity extends AppCompatActivity {
    private TextView dateTextView;
    private TextView menuTextView;
    private TextView priceTextView;
    private TextView statusTextView;
    private Button cancelButton;
    
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String mealId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_detail);
        
        // ActionBar ayarları
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Yemek Detayı");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        
        // View'ları bağla
        dateTextView = findViewById(R.id.dateTextView);
        menuTextView = findViewById(R.id.menuTextView);
        priceTextView = findViewById(R.id.priceTextView);
        statusTextView = findViewById(R.id.statusTextView);
        cancelButton = findViewById(R.id.cancelButton);
        
        // Firebase başlat
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        
        // Intent'ten meal ID'yi al
        mealId = getIntent().getStringExtra("meal_id");
        if (mealId != null) {
            loadMealDetails();
        }
        
        // İptal butonu click listener
        cancelButton.setOnClickListener(v -> cancelMeal());
    }
    
    private void loadMealDetails() {
        mDatabase.child("meals").child(mealId).get()
            .addOnSuccessListener(dataSnapshot -> {
                if (dataSnapshot.exists()) {
                    String date = dataSnapshot.child("date").getValue(String.class);
                    String menu = dataSnapshot.child("menu").getValue(String.class);
                    Double price = dataSnapshot.child("price").getValue(Double.class);
                    Boolean isCancelled = dataSnapshot.child("isCancelled").getValue(Boolean.class);
                    
                    dateTextView.setText(date);
                    menuTextView.setText(menu);
                    priceTextView.setText(String.format(Locale.getDefault(), "%.2f TL", price));
                    statusTextView.setText(isCancelled != null && isCancelled ? "İptal Edildi" : "Aktif");
                    
                    // İptal edilmişse veya tarih geçmişse butonu devre dışı bırak
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    try {
                        Date mealDate = sdf.parse(date);
                        Date today = new Date();
                        boolean isPassedDate = mealDate != null && mealDate.before(today);
                        cancelButton.setEnabled(!isPassedDate && (isCancelled == null || !isCancelled));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            })
            .addOnFailureListener(e -> 
                Toast.makeText(this, "Yemek detayları yüklenirken hata oluştu", Toast.LENGTH_SHORT).show()
            );
    }
    
    private void cancelMeal() {
        if (mealId != null && mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            
            // Yemeği iptal et
            mDatabase.child("meals").child(mealId).child("isCancelled").setValue(true)
                .addOnSuccessListener(aVoid -> {
                    // Kullanıcının cüzdanına parayı ekle
                    mDatabase.child("meals").child(mealId).child("price").get()
                        .addOnSuccessListener(dataSnapshot -> {
                            if (dataSnapshot.exists()) {
                                Double price = dataSnapshot.getValue(Double.class);
                                if (price != null) {
                                    mDatabase.child("users").child(userId)
                                        .child("walletBalance").get()
                                        .addOnSuccessListener(walletSnapshot -> {
                                            Double currentBalance = walletSnapshot.getValue(Double.class);
                                            if (currentBalance != null) {
                                                mDatabase.child("users").child(userId)
                                                    .child("walletBalance")
                                                    .setValue(currentBalance + price);
                                            }
                                        });
                                }
                            }
                        });
                    
                    Toast.makeText(this, "Yemek başarıyla iptal edildi", Toast.LENGTH_SHORT).show();
                    loadMealDetails(); // Sayfayı yenile
                })
                .addOnFailureListener(e ->
                    Toast.makeText(this, "Yemek iptal edilirken hata oluştu", Toast.LENGTH_SHORT).show()
                );
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