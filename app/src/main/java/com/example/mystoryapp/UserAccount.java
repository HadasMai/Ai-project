package com.example.mystoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.HashMap;

public class UserAccount extends AppCompatActivity {

    private TextView welcomeText;
    private Button myBooksHistoryBtn, createNewBookBtn;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference booksRef;
    private String bookId;
    private DatabaseReference userRef;
    private  String uid ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        welcomeText = findViewById(R.id.welcomeText);
        myBooksHistoryBtn = findViewById(R.id.myBooksHistoryBtn);
        createNewBookBtn = findViewById(R.id.createNewBookBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        booksRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Books");

        // בדיקת חיבור משתמש ל-Firebase
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(UserAccount.this, "אין חיבור למשתמש", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserAccount.this, LoginActivity.class));
            finish();
            return;
        }

        // אתחול המשתנה userRef
        uid = firebaseAuth.getUid();
        userRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Users").child(uid);

        loadUserInfo();

        createNewBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewBook();
            }
        });

        myBooksHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMyBooksHistory();
            }
        });
    }

    private void loadUserInfo() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    welcomeText.setText("שלום לך , " + name);
                } else {
                    Toast.makeText(UserAccount.this, "שם משתמש לא קיים", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserAccount.this, "נכשל בהבאת נתוני המשתמש ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewBook() {
       //uid = firebaseAuth.getUid();
        bookId = booksRef.push().getKey(); // Generate a unique book ID

        if (bookId == null) {
            Toast.makeText(UserAccount.this, "Failed to create book ID", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> bookData = new HashMap<>();
        bookData.put("uid", uid);
        bookData.put("bookId", bookId);

        // Save the new book in Firebase
        booksRef.child(bookId).setValue(bookData)
                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Pass the bookId to HeroStoryQuestions
                        Intent intent = new Intent(UserAccount.this, HeroStoryQuestions.class);
                        intent.putExtra("bookId", bookId);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserAccount.this, "Failed to create book", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openMyBooksHistory() {
        Intent intent = new Intent(UserAccount.this, MyBooksHistory.class);
        // Pass the bookId to HeroStoryQuestions
      //  intent.putExtra("uid", uid);
        startActivity(intent);

    }
}
