package com.example.mystoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BookCoverActivity extends AppCompatActivity {

    private TextView bookTitleTextView;
    private Button startReadingButton;
    private String bookId;
    private DatabaseReference booksRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_cover);

        bookTitleTextView = findViewById(R.id.bookTitleTextView);
        startReadingButton = findViewById(R.id.startReadingButton);

        bookId = getIntent().getStringExtra("bookId");

        if (bookId == null) {
            finish();
            return;
        }

        booksRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Books");

        fetchBookTitle();

        startReadingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookCoverActivity.this, ViewBook.class);
                intent.putExtra("bookId", bookId);
                startActivity(intent);
            }
        });
    }

    private void fetchBookTitle() {
        booksRef.child(bookId).child("bookName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String bookTitle = dataSnapshot.getValue(String.class);
                if (bookTitle != null) {
                    bookTitleTextView.setText(bookTitle);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}