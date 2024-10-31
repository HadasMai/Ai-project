/**
 * DescribeHero - This activity allows the user to describe the hero's physical attributes for a storybook.
 * The user enters details such as age, hair color and type, eye color, and skin color.
 * After submitting, the data is saved to Firebase Realtime Database, and the user is directed to ClothingDescription to continue the setup.
 */

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

    // UI elements for hero description
    private EditText ageEt, hairColorEt, eyeColorEt, skinColorEt;
    private Spinner hairTypeSpinner;
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
        setContentView(R.layout.activity_describe_hero);

         // Initialize UI components
        ageEt = findViewById(R.id.ageEt);
        hairColorEt = findViewById(R.id.hairColorEt);
        hairTypeSpinner = findViewById(R.id.hairTypeSpinner);
        eyeColorEt = findViewById(R.id.eyeColorEt);
        skinColorEt = findViewById(R.id.skinColorEt);
        submitBtn = findViewById(R.id.submitBtn);

        // Initialize Firebase Auth and Database reference
        firebaseAuth = FirebaseAuth.getInstance();
        booksRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Books");

       // Retrieve book ID from intent
        bookId = getIntent().getStringExtra("bookId");
        if (bookId == null) {
            Toast.makeText(DescribeHero.this, "Failed to get book ID", Toast.LENGTH_SHORT).show();
            finish();
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
     * validateAndSubmitData - Validates the hero's physical attribute inputs and submits them to Firebase.
     * Checks that all fields are filled with valid data. If valid, saves the data to Firebase and navigates to ClothingDescription.
     */
    private void validateAndSubmitData() {
        String ageStr = ageEt.getText().toString().trim();
        String hairColor = hairColorEt.getText().toString().trim();
        String hairType = hairTypeSpinner.getSelectedItem().toString();
        String eyeColor = eyeColorEt.getText().toString().trim();
        String skinColor = skinColorEt.getText().toString().trim();

       // Check if age is valid
        if (TextUtils.isEmpty(ageStr) || Integer.parseInt(ageStr) <= 0) {
            ageEt.setError("הכנס גיל תקין");
            return;
        }

         // Check if hair color is entered
        if (TextUtils.isEmpty(hairColor)) {
            hairColorEt.setError("הכנס צבע שיער");
            return;
        }

       // Check if eye color is entered
        if (TextUtils.isEmpty(eyeColor)) {
            eyeColorEt.setError("הכנס צבע עיניים");
            return;
        }

        // Check if skin color is entered
        if (TextUtils.isEmpty(skinColor)) {
            skinColorEt.setError("הכנס צבע עור");
            return;
        }

        // Parse age to an integer
        int age = Integer.parseInt(ageStr);

         // Prepare data to save to Firebase
        HashMap<String, Object> bookData = new HashMap<>();
        bookData.put("age", age);
        bookData.put("hairColor", hairColor);
        bookData.put("hairType", hairType);
        bookData.put("eyeColor", eyeColor);
        bookData.put("skinColor", skinColor);

         // Update Firebase with hero attributes
        booksRef.child(bookId).updateChildren(bookData)
                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Navigate to ClothingDescription activity with the book ID after successful submission
                        Intent intent = new Intent(DescribeHero.this, ClothingDescription.class);
                        intent.putExtra("bookId", bookId);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Show an error message if data submission fails
                        Toast.makeText(DescribeHero.this, "נכשל בשמירת התיאור", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
