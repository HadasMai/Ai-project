/**
 * ClothingDescription - This activity allows the user to enter additional details about a book,
 * specifically the character's religion and clothing description.
 * Once entered, the data is saved to Firebase Realtime Database under the book's ID.
 * Upon successful submission, the user is directed to the BookName activity to continue setting up the book.
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

public class ClothingDescription extends AppCompatActivity {

    // UI elements for user input
    private EditText religionEt, clothingDescriptionEt;
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
        setContentView(R.layout.activity_clothing_description);

        // Initialize UI components
        religionEt = findViewById(R.id.religionEt);
        clothingDescriptionEt = findViewById(R.id.clothingDescriptionEt);
        submitBtn = findViewById(R.id.submitBtn);

        // Initialize Firebase Auth and Database reference
        firebaseAuth = FirebaseAuth.getInstance();
        booksRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Books");

         // Retrieve book ID from intent
        bookId = getIntent().getStringExtra("bookId");

        // Check if bookId is null, if so show a message and exit
        if (bookId == null) {
            Toast.makeText(ClothingDescription.this, "bookId לא התקבל", Toast.LENGTH_SHORT).show();
            finish(); // Return to the previous activity
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
     * validateAndSubmitData - Validates the religion and clothing description input and submits it to Firebase.
     * Checks if the inputs are not empty. If valid, saves the data to the Firebase Realtime Database.
     * On success, navigates to the BookName activity to continue the book creation process.
     */
    private void validateAndSubmitData() {
        String religion = religionEt.getText().toString().trim();
        String clothingDescription = clothingDescriptionEt.getText().toString().trim();

        // Check if the religion field is empty
        if (TextUtils.isEmpty(religion)) {
            religionEt.setError("הכנס דת");
            return;
        }

         // Check if the clothing description field is empty
        if (TextUtils.isEmpty(clothingDescription)) {
            clothingDescriptionEt.setError("הכנס תיאור לבוש");
            return;
        }

         // Prepare data to save to Firebase
        HashMap<String, Object> bookData = new HashMap<>();
        bookData.put("religion", religion);
        bookData.put("clothingDescription", clothingDescription);

       // Update Firebase with the religion and clothing description
        booksRef.child(bookId).updateChildren(bookData)
                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Navigate to BookName activity with the book ID after successful submission
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
                        // Show an error message if the data submission fails
                        Toast.makeText(ClothingDescription.this, "נכשל בשמירת התיאור", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
