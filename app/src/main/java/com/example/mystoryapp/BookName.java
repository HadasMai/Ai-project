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

public class BookName extends AppCompatActivity {

    private EditText bookNameEt;
    private Button submitBtn;
    private DatabaseReference booksRef;
    private FirebaseAuth firebaseAuth;
    private String bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_name);

        bookNameEt = findViewById(R.id.bookNameEt);
        submitBtn = findViewById(R.id.submitBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        booksRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Books");

        bookId = getIntent().getStringExtra("bookId");

        // בדיקה אם bookId הוא null
        if (bookId == null) {
            Toast.makeText(BookName.this, "bookId לא התקבל", Toast.LENGTH_SHORT).show();
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
        String bookName = bookNameEt.getText().toString().trim();

        if (TextUtils.isEmpty(bookName)) {
            bookNameEt.setError("הכנס שם ספר");
            return;
        }

        HashMap<String, Object> bookData = new HashMap<>();
        bookData.put("bookName", bookName);

        booksRef.child(bookId).updateChildren(bookData)
                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(BookName.this, "שם הספר נשמר בהצלחה", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(BookName.this, NewPage.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BookName.this, "נכשל בשמירת שם הספר", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}