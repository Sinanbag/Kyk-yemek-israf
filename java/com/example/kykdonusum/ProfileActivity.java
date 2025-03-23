package com.example.kykdonusum;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.kykdonusum.models.UserProfile;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText nameInput, phoneInput, studentIdInput, dormitoryInput, roomNumberInput;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Firebase başlatma
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // UI elemanlarını tanımlama
        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        studentIdInput = findViewById(R.id.studentIdInput);
        dormitoryInput = findViewById(R.id.dormitoryInput);
        roomNumberInput = findViewById(R.id.roomNumberInput);
        saveButton = findViewById(R.id.saveButton);

        // Mevcut kullanıcı bilgilerini yükle
        loadUserProfile();

        // Kaydet butonu tıklama olayı
        saveButton.setOnClickListener(v -> saveUserProfile());
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mDatabase.child("users").child(user.getUid()).get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        UserProfile profile = dataSnapshot.getValue(UserProfile.class);
                        if (profile != null) {
                            nameInput.setText(profile.getName());
                            phoneInput.setText(profile.getPhone());
                            studentIdInput.setText(profile.getStudentId());
                            dormitoryInput.setText(profile.getDormitory());
                            roomNumberInput.setText(profile.getRoomNumber());
                        }
                    }
                })
                .addOnFailureListener(e -> 
                    Toast.makeText(ProfileActivity.this, "Profil yüklenemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
        }
    }

    private void saveUserProfile() {
        String name = nameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String studentId = studentIdInput.getText().toString().trim();
        String dormitory = dormitoryInput.getText().toString().trim();
        String roomNumber = roomNumberInput.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || studentId.isEmpty() || dormitory.isEmpty() || roomNumber.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            UserProfile profile = new UserProfile(name, phone, user.getEmail(), studentId);
            profile.setDormitory(dormitory);
            profile.setRoomNumber(roomNumber);
            
            mDatabase.child("users").child(user.getUid()).setValue(profile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProfileActivity.this, "Profil güncellendi", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> 
                    Toast.makeText(ProfileActivity.this, "Profil güncellenemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
        }
    }
} 