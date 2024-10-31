/**
 * BookName - This activity allows the user to set a name for a new book.
 * After entering the name, the book data is saved in Firebase Realtime Database.
 * Upon successful submission, the user is directed to the NewPage activity to continue creating the book.
 */

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

    // UI elements for user input
    private EditText bookNameEt;
    private Button submitBtn;

    // Firebase database reference and authentication
    private DatabaseReference booksRef;
    private FirebaseAuth firebaseAuth;
    private String bookId;

    /**
     * Called when the activity is first created.
     * Initializes the UI elements, retrieves the book ID, and sets up Firebase references.
     * Sets up an onClick listener for the submit button.
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most recently
     * supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_name);
        
        // Initialize UI components
        bookNameEt = findViewById(R.id.bookNameEt);
        submitBtn = findViewById(R.id.submitBtn);

        // Initialize Firebase Auth and Database reference
        firebaseAuth = FirebaseAuth.getInstance();
        booksRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Books");

        // Retrieve book ID from intent
        bookId = getIntent().getStringExtra("bookId");

        // Check if bookId is null, if so show a message and exit
        if (bookId == null) {
            Toast.makeText(BookName.this, "bookId לא התקבל", Toast.LENGTH_SHORT).show();
            finish(); // חזרה לפעילות הקודמת
            return;
        }

         // Set a click listener for the submit button
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSubmitData();
            }
        });
    }

     /**
     * validateAndSubmitData - Validates the book name input and submits it to Firebase.
     * Checks if the book name is not empty. If valid, saves the book name to the Firebase Realtime Database.
     * On success, navigates to the NewPage activity to continue the book creation process.
     */
    private void validateAndSubmitData() {
        String bookName = bookNameEt.getText().toString().trim();

         // Check if the book name is empty
        if (TextUtils.isEmpty(bookName)) {
            bookNameEt.setError("הכנס שם ספר");
            return;
        }

        // Prepare data to save to Firebase
        HashMap<String, Object> bookData = new HashMap<>();
        bookData.put("bookName", bookName);

        // Update Firebase with the book name
        booksRef.child(bookId).updateChildren(bookData)
                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Navigate to NewPage activity with the book ID after successful submission
                        Intent intent = new Intent(BookName.this, NewPage.class);
                        intent.putExtra("bookId", bookId);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Show an error message if the book name submission fails
                        Toast.makeText(BookName.this, "נכשל בשמירת שם הספר", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
