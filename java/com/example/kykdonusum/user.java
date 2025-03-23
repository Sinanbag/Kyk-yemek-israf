package com.example.kykdonusum;

public class user {
    private String email;

    // Firebase için boş constructor gerekli
    public user() {
    }

    // Kullanıcı oluşturmak için constructor
    public user(String email) {
        this.email = email;
    }

    // Email'i almak için getter
    public String getEmail() {
        return email;
    }

    // Email'i değiştirmek için setter
    public void setEmail(String email) {
        this.email = email;
    }
}