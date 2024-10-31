/**
 * BookCoverActivity - Displays the cover page of a selected book with its title and provides an option to start reading.
 * This activity fetches the book title from Firebase Realtime Database using the provided book ID and displays it.
 * When the user clicks the "Start Reading" button, the app navigates to the `ViewBook` activity to display the book content.
 */

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
    
    // UI elements for displaying the book title and button to start reading
    private TextView bookTitleTextView;
    private Button startReadingButton;
    
    // Firebase database reference and book ID
    private String bookId;
    private DatabaseReference booksRef;
    
    /**
     * Called when the activity is first created.
     * Initializes the UI elements and retrieves the book ID from the intent.
     * Sets up a Firebase reference and fetches the book title.
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most recently
     * supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_cover);

        // Initialize UI components
        bookTitleTextView = findViewById(R.id.bookTitleTextView);
        startReadingButton = findViewById(R.id.startReadingButton);

        // Retrieve book ID from the intent extras
        bookId = getIntent().getStringExtra("bookId");

        // If no book ID is provided, close the activity
        if (bookId == null) {
            finish();
            return;
        }

        // Initialize Firebase reference to the "Books" node
        booksRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Books");
        
        // Fetch and display the book title
        fetchBookTitle();

        // Set up a click listener for the "Start Reading" button to navigate to the `ViewBook` activity
        startReadingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookCoverActivity.this, ViewBook.class);
                intent.putExtra("bookId", bookId);
                startActivity(intent);
            }
        });
    }

    /**
     * fetchBookTitle - Retrieves the book title from Firebase using the book ID
     * and displays it on the cover page. The title is fetched as a single event.
     * Handles cases where the title might be null.
     */
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
