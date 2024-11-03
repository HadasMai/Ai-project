/**
 * WelcomePage - This activity serves as the entry point for the application,
 * providing options for users to either register or log in.
 * It navigates to the appropriate activity based on the button selected.
 */

package com.example.mystoryapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomePage extends AppCompatActivity {

     // UI elements for registration and login
    private Button registerBtn, loginBtn;

    /**
     * Called when the activity is first created.
     * Initializes UI components and sets click listeners for navigation.
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most recently
     * supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

          // Initialize buttons
        registerBtn = findViewById(R.id.registerBtn);
        loginBtn = findViewById(R.id.loginBtn);

        // Set click listener for Register button to navigate to RegisterActivity
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomePage.this, RegisterActivity.class));
            }
        });

        // Set click listener for Login button to navigate to LoginActivity
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomePage.this, LoginActivity.class));
            }
        });
    }


}
