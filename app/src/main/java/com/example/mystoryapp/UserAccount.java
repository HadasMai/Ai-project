/**
 * UserAccount - This activity represents the main user account interface.
 * It allows the user to view a welcome message, navigate to book history, or create a new book.
 */

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

    // UI elements
    private TextView welcomeText;
    private Button myBooksHistoryBtn, createNewBookBtn;
    
    // Firebase authentication and database references
    private FirebaseAuth firebaseAuth;
    private DatabaseReference booksRef;
    private DatabaseReference userRef;

     // User and book identifiers
    private String bookId;
    private  String uid ;
    
    /**
     * Called when the activity is first created.
     * Initializes UI elements, Firebase references, checks user authentication, and loads user information.
     * Sets up listeners for creating a new book and opening book history.
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most recently
     * supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

       // Initialize UI components
        welcomeText = findViewById(R.id.welcomeText);
        myBooksHistoryBtn = findViewById(R.id.myBooksHistoryBtn);
        createNewBookBtn = findViewById(R.id.createNewBookBtn);

        // Initialize Firebase references
        firebaseAuth = FirebaseAuth.getInstance();
        booksRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Books");

        // Check if user is logged in
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(UserAccount.this, "אין חיבור למשתמש", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserAccount.this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize user reference and load user information
        uid = firebaseAuth.getUid();
        userRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Users").child(uid);

        loadUserInfo();

        // Set click listeners for buttons
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

     /**
     * loadUserInfo - Fetches and displays the user’s name as a welcome message.
     */
    private void loadUserInfo() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    welcomeText.setText("שלום לך, " + name);
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

     /**
     * createNewBook - Creates a new book entry in the Firebase database for the current user.
     * Generates a unique book ID, stores the book data, and navigates to HeroStoryQuestions activity.
     */
    private void createNewBook() {
       //uid = firebaseAuth.getUid();
        bookId = booksRef.push().getKey(); // Generate a unique book ID

        if (bookId == null) {
            Toast.makeText(UserAccount.this, "Failed to create book ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Store book data
        HashMap<String, Object> bookData = new HashMap<>();
        bookData.put("uid", uid);
        bookData.put("bookId", bookId);

        // Save the new book in Firebase
        booksRef.child(bookId).setValue(bookData)
                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                       // Navigate to HeroStoryQuestions with the new bookId
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

     /**
     * openMyBooksHistory - Opens the MyBooksHistory activity where the user can view their books.
     */
    private void openMyBooksHistory() {
        Intent intent = new Intent(UserAccount.this, MyBooksHistory.class);
        // Pass the bookId to HeroStoryQuestions
      //  intent.putExtra("uid", uid);
        startActivity(intent);

    }
}
