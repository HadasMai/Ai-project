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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UserAccount extends AppCompatActivity {

    private TextView welcomeText;
    private Button myBooksHistoryBtn, createNewBookBtn;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef, booksRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        welcomeText = findViewById(R.id.welcomeText);
        myBooksHistoryBtn = findViewById(R.id.myBooksHistoryBtn);
        createNewBookBtn = findViewById(R.id.createNewBookBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Users").child(firebaseAuth.getUid());

        booksRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Books");

        loadUserInfo();

        myBooksHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // מעבר לדף שמציג את היסטורית הספרים שלי
                startActivity(new Intent(UserAccount.this, MyBooksHistoryActivity.class));
            }
        });

        createNewBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // יצירת ספר חדש והוספתו לטבלה Books
                createNewBook();
            }
        });
    }

    private void loadUserInfo() {
        userRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    welcomeText.setText("שלום לך , " + name);
                } else {
                    Toast.makeText(UserAccount.this, "שם משתמש לא קיים", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                Toast.makeText(UserAccount.this, "נכשל בהבאת נתוני המשתמש", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewBook() {
        String uid = firebaseAuth.getUid();
        String bookId = booksRef.push().getKey(); // יצירת מזהה ייחודי לספר

        HashMap<String, Object> bookData = new HashMap<>();
        bookData.put("uid", uid);
        bookData.put("bookId", bookId);
        bookData.put("heroName", ""); // ניתן לערוך את השדות בהתאם לצורך
        bookData.put("gender", "");
        bookData.put("animalType", "");
        bookData.put("age", "");
        bookData.put("hairColor", "");
        bookData.put("eyeColor", "");
        bookData.put("skinColor", "");
        bookData.put("religion", "");
        bookData.put("clothingDescription", "");

        booksRef.child(bookId).setValue(bookData)
                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UserAccount.this, "ספר חדש נוצר בהצלחה", Toast.LENGTH_SHORT).show();
                        // מעבר לדף שבו ניתן לערוך את הספר החדש (HeroStoryQuestions או דף אחר)
                        startActivity(new Intent(UserAccount.this, HeroStoryQuestions.class));
                    }
                })
                .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserAccount.this, "נכשל ביצירת הספר החדש", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

//package com.example.mystoryapp;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
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
//public class UserAccount extends AppCompatActivity {
//
//    private TextView welcomeText;
//    private Button myBooksHistoryBtn, createNewBookBtn;
//    private FirebaseAuth firebaseAuth;
//    private DatabaseReference userRef;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_user_account);
//
//        welcomeText = findViewById(R.id.welcomeText);
//        myBooksHistoryBtn = findViewById(R.id.myBooksHistoryBtn);
//        createNewBookBtn = findViewById(R.id.createNewBookBtn);
//
//        firebaseAuth = FirebaseAuth.getInstance();
//        userRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
//                .getReference("Users").child(firebaseAuth.getUid());
//
//        loadUserInfo();
//
//        myBooksHistoryBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // מעבר לדף שמציג את היסטורית הספרים שלי
//                startActivity(new Intent(UserAccount.this, MyBooksHistoryActivity.class));
//            }
//        });
//
//        createNewBookBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // מעבר לדף שבו ניתן ליצור ספר חדש
//                startActivity(new Intent(UserAccount.this, HeroStoryQuestions.class));
//            }
//        });
//    }
//
//    private void loadUserInfo() {
//        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    String name = snapshot.child("name").getValue(String.class);
//                    welcomeText.setText("שלום לך , " + name);
//                } else {
//                    Toast.makeText(UserAccount.this, "שם משתמש לא קיים", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(UserAccount.this, "נכשל בהבאת נתוני המשתמש ", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}