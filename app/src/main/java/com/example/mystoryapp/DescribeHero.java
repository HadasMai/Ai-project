package com.example.mystoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DescribeHero extends AppCompatActivity {

    private EditText ageEt, hairColorEt, eyeColorEt, skinColorEt;
    private Spinner hairTypeSpinner;
    private Button submitBtn;
    private DatabaseReference booksRef;
    private FirebaseAuth firebaseAuth;
    private String bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_describe_hero);

        ageEt = findViewById(R.id.ageEt);
        hairColorEt = findViewById(R.id.hairColorEt);
        hairTypeSpinner = findViewById(R.id.hairTypeSpinner);
        eyeColorEt = findViewById(R.id.eyeColorEt);
        skinColorEt = findViewById(R.id.skinColorEt);
        submitBtn = findViewById(R.id.submitBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        booksRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Books");

        // Get the bookId passed from UserAccount
        bookId = getIntent().getStringExtra("bookId");
        if (bookId == null) {
            Toast.makeText(DescribeHero.this, "Failed to get book ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSubmitData();
            }
        });
    }

    private void validateAndSubmitData() {
        String ageStr = ageEt.getText().toString().trim();
        String hairColor = hairColorEt.getText().toString().trim();
        String hairType = hairTypeSpinner.getSelectedItem().toString();
        String eyeColor = eyeColorEt.getText().toString().trim();
        String skinColor = skinColorEt.getText().toString().trim();

        if (TextUtils.isEmpty(ageStr) || Integer.parseInt(ageStr) <= 0) {
            ageEt.setError("הכנס גיל תקין");
            return;
        }

        if (TextUtils.isEmpty(hairColor)) {
            hairColorEt.setError("הכנס צבע שיער");
            return;
        }

        if (TextUtils.isEmpty(eyeColor)) {
            eyeColorEt.setError("הכנס צבע עיניים");
            return;
        }

        if (TextUtils.isEmpty(skinColor)) {
            skinColorEt.setError("הכנס צבע עור");
            return;
        }

        int age = Integer.parseInt(ageStr);

        HashMap<String, Object> bookData = new HashMap<>();
        bookData.put("age", age);
        bookData.put("hairColor", hairColor);
        bookData.put("hairType", hairType);
        bookData.put("eyeColor", eyeColor);
        bookData.put("skinColor", skinColor);

        booksRef.child(bookId).updateChildren(bookData)
                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(DescribeHero.this, "תיאור הדמות נשמר בהצלחה", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DescribeHero.this, ClothingDescription.class);
                        // Pass the bookId to HeroStoryQuestions
                        intent.putExtra("bookId", bookId);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DescribeHero.this, "נכשל בשמירת התיאור", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
//
//package com.example.mystoryapp;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Spinner;
//import android.widget.Toast;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.HashMap;
//
//public class DescribeHero extends AppCompatActivity {
//
//    private EditText ageEt, hairColorEt, eyeColorEt, skinColorEt;
//    private Spinner hairTypeSpinner;
//    private Button submitBtn;
//    private DatabaseReference booksRef;
//    private FirebaseAuth firebaseAuth;
//    private String bookId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_describe_hero);
//
//        ageEt = findViewById(R.id.ageEt);
//        hairColorEt = findViewById(R.id.hairColorEt);
//        hairTypeSpinner = findViewById(R.id.hairTypeSpinner);
//        eyeColorEt = findViewById(R.id.eyeColorEt);
//        skinColorEt = findViewById(R.id.skinColorEt);
//        submitBtn = findViewById(R.id.submitBtn);
//
//        firebaseAuth = FirebaseAuth.getInstance();
//        booksRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
//                .getReference("Books");
//
//        // Get the bookId passed from UserAccount
//        bookId = getIntent().getStringExtra("bookId");
//        if (bookId == null) {
//            Toast.makeText(DescribeHero.this, "Failed to get book ID", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//        submitBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                validateAndSubmitData();
//            }
//        });
//    }
//
//    private void validateAndSubmitData() {
//        String ageStr = ageEt.getText().toString().trim();
//        String hairColor = hairColorEt.getText().toString().trim();
//        String hairType = hairTypeSpinner.getSelectedItem().toString();
//        String eyeColor = eyeColorEt.getText().toString().trim();
//        String skinColor = skinColorEt.getText().toString().trim();
//
//        if (TextUtils.isEmpty(ageStr) || Integer.parseInt(ageStr) < 0) {
//            ageEt.setError("הכנס גיל תקין מעל 1");
//            return;
//        }
//        if (TextUtils.isEmpty(hairColor)) {
//            hairColorEt.setError("הכנס צבע שיער");
//            return;
//        }
//
//        if (TextUtils.isEmpty(eyeColor)) {
//            eyeColorEt.setError("הכנס צבע עיניים");
//            return;
//        }
//
//        if (TextUtils.isEmpty(skinColor)) {
//            skinColorEt.setError("הכנס צבע עור");
//            return;
//        }
//
//        int age = Integer.parseInt(ageStr);
//
//        HashMap<String, Object> bookData = new HashMap<>();
//        bookData.put("age", age);
//        bookData.put("hairColor", hairColor);
//        bookData.put("hairType", hairType);
//        bookData.put("eyeColor", eyeColor);
//        bookData.put("skinColor", skinColor);
//
//        booksRef.child(bookId).updateChildren(bookData)
//                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        // Retrieve characterType from the database
//                        booksRef.child(bookId).child("characterType").addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                String characterType = snapshot.getValue(String.class);
//                                if ("חיה".equals(characterType)) {
//                                    // If characterType is "חיה", go to BookName
//                                    Intent intent = new Intent(DescribeHero.this, BookName.class);
//                                    intent.putExtra("bookId", bookId);
//                                    startActivity(intent);
//                                } else {
//                                    // Otherwise, go to ClothingDescription
//                                    Intent intent = new Intent(DescribeHero.this, ClothingDescription.class);
//                                    intent.putExtra("bookId", bookId);
//                                    startActivity(intent);
//                                }
//                                finish();
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                                Toast.makeText(DescribeHero.this, "Failed to retrieve character type", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                })
//                .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(DescribeHero.this, "נכשל בשמירת התיאור", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//}
