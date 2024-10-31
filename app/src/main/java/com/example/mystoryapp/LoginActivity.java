/**
 * LoginActivity - This activity handles user login with email and password.
 * It authenticates the user with Firebase Authentication and navigates to the UserAccount screen on successful login.
 * If login fails, the user is redirected to the WelcomePage.
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

public class LoginActivity extends AppCompatActivity {

    // UI elements for user input and login button
    private EditText emailEt, passwordEt;
    private Button loginBtn;

    // ProgressDialog to indicate login progress
    private ProgressDialog progressDialog;

    // Firebase authentication instance
    private FirebaseAuth firebaseAuth;

   /**
     * Called when the activity is first created.
     * Initializes the UI elements, Firebase Auth, and sets up the login button listener.
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most recently
     * supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

         // Initialize UI components
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        loginBtn = findViewById(R.id.loginBtn);

        // Set up progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("המתן בבקשה");
        progressDialog.setCanceledOnTouchOutside(false);

        // Initialize Firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();

         // Set click listener for the login button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    // Fields to store user input
    private String email = "", password = "";

    /**
     * validateData - Validates user input for email and password.
     * If fields are empty, sets error messages. If valid, calls loginUser to proceed with authentication.
     */
    private void validateData() {
        email = emailEt.getText().toString().trim();
        password = passwordEt.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEt.setError("הכנס אימייל");
        } else if (TextUtils.isEmpty(password)) {
            passwordEt.setError("הכנס סיסמא");
        } else {
            loginUser();
        }
    }

     /**
     * loginUser - Authenticates the user using Firebase Authentication.
     * Shows a progress dialog during login, navigates to UserAccount on success, and to WelcomePage on failure.
     */
    private void loginUser() {
        progressDialog.setMessage("מתחבר...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "התחברת בהצלחה", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, UserAccount.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, WelcomePage.class));//לבדוק אם לעשות את זה או משהו אחר במידה והוא טועה
                        finish();
                    }
                });
    }
}
