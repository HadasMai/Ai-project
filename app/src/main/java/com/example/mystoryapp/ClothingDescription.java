package com.example.mystoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ClothingDescription extends AppCompatActivity {

    private EditText religionEt, clothingDescriptionEt;
    private Button submitBtn;
    private DatabaseReference booksRef;
    private FirebaseAuth firebaseAuth;
    private String bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothing_description);

        religionEt = findViewById(R.id.religionEt);
        clothingDescriptionEt = findViewById(R.id.clothingDescriptionEt);
        submitBtn = findViewById(R.id.submitBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        booksRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Books");

        bookId = getIntent().getStringExtra("bookId");

        // בדיקה אם bookId הוא null
        if (bookId == null) {
            Toast.makeText(ClothingDescription.this, "bookId לא התקבל", Toast.LENGTH_SHORT).show();
            finish(); // חזרה לפעילות הקודמת
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
        String religion = religionEt.getText().toString().trim();
        String clothingDescription = clothingDescriptionEt.getText().toString().trim();

        if (TextUtils.isEmpty(religion)) {
            religionEt.setError("הכנס דת");
            return;
        }

        if (TextUtils.isEmpty(clothingDescription)) {
            clothingDescriptionEt.setError("הכנס תיאור לבוש");
            return;
        }

        HashMap<String, Object> bookData = new HashMap<>();
        bookData.put("religion", religion);
        bookData.put("clothingDescription", clothingDescription);

        booksRef.child(bookId).updateChildren(bookData)
                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ClothingDescription.this, "תיאור הלבוש נשמר בהצלחה", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ClothingDescription.this, BookName.class);
                        // Pass the bookId to HeroStoryQuestions
                        intent.putExtra("bookId", bookId);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ClothingDescription.this, "נכשל בשמירת התיאור", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
