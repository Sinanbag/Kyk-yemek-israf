package com.example.kykdonusum;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText currentPasswordInput, newPasswordInput, confirmPasswordInput;
    private Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();

        // UI elemanlarını tanımlama
        currentPasswordInput = findViewById(R.id.currentPasswordInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        changePasswordButton = findViewById(R.id.changePasswordButton);

        // Şifre değiştirme butonu tıklama olayı
        changePasswordButton.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String currentPassword = currentPasswordInput.getText().toString();
        String newPassword = newPasswordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        // Kontroller
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Yeni şifreler eşleşmiyor", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "Yeni şifre en az 6 karakter olmalıdır", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Önce mevcut şifre ile yeniden kimlik doğrulama
            mAuth.signInWithEmailAndPassword(user.getEmail(), currentPassword)
                    .addOnSuccessListener(authResult -> {
                        // Şifre değiştirme
                        user.updatePassword(newPassword)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(ChangePasswordActivity.this, 
                                            "Şifre başarıyla değiştirildi", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> 
                                    Toast.makeText(ChangePasswordActivity.this,
                                            "Şifre değiştirilemedi: " + e.getMessage(), 
                                            Toast.LENGTH_SHORT).show()
                                );
                    })
                    .addOnFailureListener(e -> 
                        Toast.makeText(ChangePasswordActivity.this,
                                "Mevcut şifre yanlış", Toast.LENGTH_SHORT).show()
                    );
        }
    }
} 