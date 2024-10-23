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

       
        mAuth = FirebaseAuth.getInstance();

        
        page2 = findViewById(R.id.imageButton);
        loginButton = findViewById(R.id.loginButton);

       
        page2.setOnClickListener(v -> {
           
            Intent activity2 = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(activity2);
        });

        
        loginButton.setOnClickListener(v -> {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        });
    }
}
