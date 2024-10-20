package com.example.canteen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity2 extends AppCompatActivity {
    Button btnToActivity3, btnToMainActivity;
    ListView listView;
    ArrayList<String> storeList;
    ArrayList<String> documentIdList; // เก็บ document ID
    ArrayAdapter<String> arrayAdapter;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    boolean isUserLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        isUserLoggedIn = currentUser != null; // เช็คว่าผู้ใช้ล็อกอินอยู่หรือไม่

        btnToActivity3 = findViewById(R.id.btnToActivity3);
        listView = findViewById(R.id.listView);
        storeList = new ArrayList<>();
        documentIdList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        btnToMainActivity = findViewById(R.id.btnToMainActivity);

        btnToMainActivity.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, MainActivity.class);
            startActivity(intent);
        });

        // กดเพื่อไปยัง MainActivity3
        btnToActivity3.setOnClickListener(v -> {
            if (isUserLoggedIn) {
                Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity2.this, "กรุณาเข้าสู่ระบบก่อน", Toast.LENGTH_SHORT).show();
            }
        });

        // โหลดข้อมูลจาก Firestore
        loadStoresFromFirestore();

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, storeList);
        listView.setAdapter(arrayAdapter);

        // กดค้างเพื่อแสดงรายละเอียดร้านและแก้ไขหรือลบข้อมูล
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            String documentId = documentIdList.get(position);

            // ดึงข้อมูลร้านจาก Firestore
            db.collection("stores").document(documentId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String ownerUid = documentSnapshot.getString("ownerUid");
                    String currentUserUid = isUserLoggedIn ? mAuth.getCurrentUser().getUid() : null;

                    // ตัวเลือกสำหรับทุกคน: แสดงรายละเอียด
                    ArrayList<CharSequence> options = new ArrayList<>();
                    options.add("แสดงรายละเอียด");

                    // เพิ่มตัวเลือก "แก้ไข" และ "ลบ" เฉพาะเจ้าของร้าน
                    if (isUserLoggedIn && currentUserUid != null && currentUserUid.equals(ownerUid)) {
                        options.add("แก้ไขสถานะ");
                        options.add("แก้ไขรายละเอียด");  // เพิ่มตัวเลือกแก้ไขรายละเอียด
                        options.add("ลบ");
                    }

                    // แสดงเมนูตัวเลือก
                    new AlertDialog.Builder(MainActivity2.this)
                            .setTitle("เลือกการกระทำ")
                            .setItems(options.toArray(new CharSequence[0]), (dialog, which) -> {
                                if (options.get(which).equals("แสดงรายละเอียด")) {
                                    showStoreDetails(documentId);  // แสดงรายละเอียด
                                } else if (options.get(which).equals("แก้ไขสถานะ")) {
                                    showStatusDialog(position);  // แก้ไขสถานะ
                                } else if (options.get(which).equals("แก้ไขรายละเอียด")) {
                                    showEditDetailsDialog(documentId);  // แก้ไขรายละเอียด
                                } else if (options.get(which).equals("ลบ")) {
                                    confirmDelete(position);  // ลบข้อมูล
                                }
                            })
                            .show();
                }
            });
            return true;
        });
    }

    // ฟังก์ชันสำหรับโหลดข้อมูลจาก Firestore
    private void loadStoresFromFirestore() {
        db.collection("stores")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(MainActivity2.this, "เกิดข้อผิดพลาดในการดึงข้อมูล", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    storeList.clear();
                    documentIdList.clear();

                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            String storeName = document.getString("storeName");
                            String status = document.getString("status");
                            storeList.add("ร้าน: " + storeName + ", สถานะ: " + status);
                            documentIdList.add(document.getId());
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                });
    }

    // ฟังก์ชันแสดงรายละเอียดร้าน
    private void showStoreDetails(String documentId) {
        db.collection("stores").document(documentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String storeName = documentSnapshot.getString("storeName");
                        String storeDescription = documentSnapshot.getString("storeDescription");
                        String status = documentSnapshot.getString("status");

                        // แสดงรายละเอียดร้านใน AlertDialog
                        new AlertDialog.Builder(MainActivity2.this)
                                .setTitle("รายละเอียดร้าน")
                                .setMessage("ชื่อร้าน: " + storeName + "\n\nรายละเอียด: " + storeDescription + "\n\nสถานะ: " + status)
                                .setPositiveButton("ปิด", null)
                                .show();
                    }
                });
    }

    // ฟังก์ชันแสดง Dialog สำหรับแก้ไขสถานะ (เฉพาะเจ้าของร้าน)
    private void showStatusDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
        builder.setTitle("แก้ไขสถานะ");

        String[] options = {"เปิด", "ปิด"};
        builder.setSingleChoiceItems(options, -1, (dialog, which) -> {
            String newStatus = options[which];
            String documentId = documentIdList.get(position);

            HashMap<String, Object> updateData = new HashMap<>();
            updateData.put("status", newStatus);
            db.collection("stores").document(documentId).update(updateData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MainActivity2.this, "อัปเดตสถานะสำเร็จ", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity2.this, "เกิดข้อผิดพลาดในการอัปเดตสถานะ", Toast.LENGTH_SHORT).show();
                    });

            String[] currentData = storeList.get(position).split(", ");
            String storeName = currentData[0];
            storeList.set(position, storeName + ", สถานะ: " + newStatus);
            arrayAdapter.notifyDataSetChanged();

            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // ฟังก์ชันแสดง Dialog สำหรับแก้ไขรายละเอียด (เฉพาะเจ้าของร้าน)
    private void showEditDetailsDialog(String documentId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
        builder.setTitle("แก้ไขรายละเอียดร้าน");

        // กำหนด Layout ของ Dialog ให้มีฟิลด์ EditText
        final EditText input = new EditText(MainActivity2.this);
        input.setHint("กรอกรายละเอียดร้านใหม่");
        builder.setView(input);

        // ดึงรายละเอียดร้านปัจจุบัน
        db.collection("stores").document(documentId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String currentDescription = documentSnapshot.getString("storeDescription");
                input.setText(currentDescription);
            }
        });

        builder.setPositiveButton("บันทึก", (dialog, which) -> {
            String newDescription = input.getText().toString();

            // อัปเดตรายละเอียดใหม่ใน Firestore
            HashMap<String, Object> updateData = new HashMap<>();
            updateData.put("storeDescription", newDescription);
            db.collection("stores").document(documentId).update(updateData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MainActivity2.this, "อัปเดตรายละเอียดสำเร็จ", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity2.this, "เกิดข้อผิดพลาดในการอัปเดตรายละเอียด", Toast.LENGTH_SHORT).show();
                    });
        });

        builder.setNegativeButton("ยกเลิก", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // ฟังก์ชันลบข้อมูล (เฉพาะเจ้าของร้าน)
    private void confirmDelete(int position) {
        new AlertDialog.Builder(MainActivity2.this)
                .setTitle("ยืนยันการลบ")
                .setMessage("คุณแน่ใจหรือว่าต้องการลบข้อมูลนี้?")
                .setPositiveButton("ลบ", (dialog, which) -> {
                    String documentId = documentIdList.get(position);
                    db.collection("stores").document(documentId).delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(MainActivity2.this, "ลบข้อมูลสำเร็จ", Toast.LENGTH_SHORT).show();
                                storeList.remove(position);
                                arrayAdapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(MainActivity2.this, "เกิดข้อผิดพลาดในการลบข้อมูล", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("ยกเลิก", null)
                .show();
    }
}
