package com.example.canteen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity3 extends AppCompatActivity {

    EditText etStoreName, etStoreDescription;
    Spinner spinnerStatus;
    Button btnSubmit, btnBack;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        
        etStoreName = findViewById(R.id.etStoreName);
        etStoreDescription = findViewById(R.id.etStoreDescription);  
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBack);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

       
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        
        btnSubmit.setOnClickListener(v -> {
            String storeName = etStoreName.getText().toString();
            String storeDescription = etStoreDescription.getText().toString();  
            String status = spinnerStatus.getSelectedItem().toString();
            String userId = auth.getCurrentUser().getUid(); 

           
            if (storeName.isEmpty()) {
                Toast.makeText(MainActivity3.this, "กรุณากรอกชื่อร้าน", Toast.LENGTH_SHORT).show();
                return;
            }
            if (storeDescription.isEmpty()) {
                Toast.makeText(MainActivity3.this, "กรุณากรอกรายละเอียดร้าน", Toast.LENGTH_SHORT).show();
                return;
            }

            
            HashMap<String, Object> storeData = new HashMap<>();
            storeData.put("storeName", storeName);
            storeData.put("storeDescription", storeDescription);  
            storeData.put("status", status);
            storeData.put("ownerUid", userId);  

           
            db.collection("stores").add(storeData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(MainActivity3.this, "เพิ่มข้อมูลสำเร็จ", Toast.LENGTH_SHORT).show();
                        finish(); 
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity3.this, "เกิดข้อผิดพลาดในการเพิ่มข้อมูล", Toast.LENGTH_SHORT).show();
                    });
        });

        
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity3.this, MainActivity2.class);
            startActivity(intent);
            finish();  
        });

       
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            
            Intent intent = new Intent(MainActivity3.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
