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

        // กำหนดค่าให้กับ View ต่างๆ
        etStoreName = findViewById(R.id.etStoreName);
        etStoreDescription = findViewById(R.id.etStoreDescription);  // เพิ่มฟิลด์รายละเอียดร้าน
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBack);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // ตั้งค่า Spinner สำหรับสถานะร้าน
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        // เมื่อผู้ใช้กดปุ่ม Submit
        btnSubmit.setOnClickListener(v -> {
            String storeName = etStoreName.getText().toString();
            String storeDescription = etStoreDescription.getText().toString();  // เก็บรายละเอียดร้าน
            String status = spinnerStatus.getSelectedItem().toString();
            String userId = auth.getCurrentUser().getUid(); // ดึง uid ของผู้ใช้

            // ตรวจสอบว่าชื่อร้านและรายละเอียดร้านไม่ว่างเปล่า
            if (storeName.isEmpty()) {
                Toast.makeText(MainActivity3.this, "กรุณากรอกชื่อร้าน", Toast.LENGTH_SHORT).show();
                return;
            }
            if (storeDescription.isEmpty()) {
                Toast.makeText(MainActivity3.this, "กรุณากรอกรายละเอียดร้าน", Toast.LENGTH_SHORT).show();
                return;
            }

            // สร้าง HashMap สำหรับเก็บข้อมูล
            HashMap<String, Object> storeData = new HashMap<>();
            storeData.put("storeName", storeName);
            storeData.put("storeDescription", storeDescription);  // เพิ่มรายละเอียดร้าน
            storeData.put("status", status);
            storeData.put("ownerUid", userId);  // บันทึก uid ของเจ้าของร้าน

            // เพิ่มข้อมูลไปยัง Firestore
            db.collection("stores").add(storeData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(MainActivity3.this, "เพิ่มข้อมูลสำเร็จ", Toast.LENGTH_SHORT).show();
                        finish();  // กลับไปยังหน้าก่อนหน้า
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity3.this, "เกิดข้อผิดพลาดในการเพิ่มข้อมูล", Toast.LENGTH_SHORT).show();
                    });
        });

        // เมื่อผู้ใช้กดปุ่ม Back
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity3.this, MainActivity2.class);
            startActivity(intent);
            finish();  // ปิด MainActivity3
        });

        // ตรวจสอบว่าผู้ใช้เข้าสู่ระบบหรือไม่
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // ถ้าผู้ใช้ไม่ได้เข้าสู่ระบบ ให้กลับไปที่หน้า Login
            Intent intent = new Intent(MainActivity3.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
