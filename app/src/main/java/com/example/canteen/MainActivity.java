package com.example.canteen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ImageButton page2;
    private Button loginButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Setup UI
        page2 = findViewById(R.id.imageButton);
        loginButton = findViewById(R.id.loginButton);

        // กดเพื่อไปยัง MainActivity2
        page2.setOnClickListener(v -> {
            // ไม่ต้องเช็คการล็อกอิน
            Intent activity2 = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(activity2);
        });

        // กดเพื่อเข้าสู่ระบบ
        loginButton.setOnClickListener(v -> {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        });
    }
}
