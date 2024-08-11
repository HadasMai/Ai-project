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

    private EditText emailEt, passwordEt;
    private Button loginBtn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        loginBtn = findViewById(R.id.loginBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("המתן בבקשה");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private String email = "", password = "";

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