/**
 * RegisterActivity - This activity handles user registration by creating a new account with Firebase Authentication.
 * It collects user details, creates an account, and saves the user information in Firebase Realtime Database.
 * After successful registration, it navigates to the UserAccount activity.
 */

package com.example.mystoryapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    // UI elements for user input and registration button
    private EditText emailEt, passwordEt, nameEt;
    private Button registerBtn;

    // ProgressDialog to indicate registration progress
    private ProgressDialog progressDialog;

    // Firebase authentication instance
    private FirebaseAuth firebaseAuth;

   /**
     * Called when the activity is first created.
     * Initializes the UI elements, Firebase Auth, and sets up the registration button listener.
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most recently
     * supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI components
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        nameEt = findViewById(R.id.nameEt);
        registerBtn = findViewById(R.id.registerBtn);

        // Set up progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("המתן בבקשה");
        progressDialog.setCanceledOnTouchOutside(false);

        // Initialize Firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();

        // Set click listener for the registration button
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    // Fields to store user input
    private String email = "", password = "", name = "";

    /**
     * validateData - Validates user input for email, password, and name.
     * If fields are empty, sets error messages. If valid, calls createUserAccount to proceed with registration.
     */
    private void validateData() {
        email = emailEt.getText().toString().trim();
        password = passwordEt.getText().toString().trim();
        name = nameEt.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEt.setError("הכנס אימייל");
        } else if (TextUtils.isEmpty(password)) {
            passwordEt.setError("הכנס סיסמא");
        } else if (TextUtils.isEmpty(name)) {
            nameEt.setError("הכנס שם משתמש");
        } else {
            createUserAccount();
        }
    }
    /**
     * createUserAccount - Creates a new user account with Firebase Authentication.
     * Shows a progress dialog during the account creation process.
     */
    private void createUserAccount() {
        progressDialog.setMessage("יוצר חשבון חדש....");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        updateUserInfo();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

      /**
     * updateUserInfo - Saves additional user information to Firebase Realtime Database.
     * Includes fields such as UID, email, name, profile image, and user type.
     * Navigates to UserAccount activity on success.
     */
    private void updateUserInfo() {
        progressDialog.setMessage("שומר פרטי משתמש....");

        long timestamp = System.currentTimeMillis();
        String uid = firebaseAuth.getUid();

         // Prepare user information for database
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("email", email);
        hashMap.put("name", name);
        hashMap.put("profileImage", "");
        hashMap.put("userType", "user");
        hashMap.put("timestamp", timestamp);

        // Reference to Firebase Database
        DatabaseReference ref = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "החשבון נוצר...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, UserAccount.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
