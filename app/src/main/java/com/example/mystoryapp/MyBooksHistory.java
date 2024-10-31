/**
 * MyBooksHistory - This activity displays a list of books created by the logged-in user.
 * It retrieves the user's books from Firebase Realtime Database and shows the titles in a ListView.
 * When a book title is selected, it opens the BookCoverActivity to view the book's cover and details.
 */

package com.example.mystoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBooksHistory extends AppCompatActivity {

    // UI element for displaying the list of books
    private ListView booksListView;
    
    // Firebase authentication and database references
    private FirebaseAuth firebaseAuth;
    private DatabaseReference booksRef;

    // Data structures to store book details
    private Map<String, String> booksMap;// Maps book IDs to titles
    private List<String> bookTitles;// List of book titles for display
    private ArrayAdapter<String> adapter;

    /**
     * Called when the activity is first created.
     * Initializes UI elements, Firebase references, and sets up ListView with user's books.
     * Adds a click listener to open a book's details on selection.
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most recently
     * supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books_history);

        // Initialize UI components
        booksListView = findViewById(R.id.booksListView);
        firebaseAuth = FirebaseAuth.getInstance();
        booksRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Books");

         // Retrieve the current user's ID
        String uid = firebaseAuth.getUid();
        if (uid != null) {
            loadUserBooks(uid);// Load books for the logged-in user
        } else {
            Toast.makeText(this, "משתמש לא מחובר", Toast.LENGTH_SHORT).show();
        }

       // Initialize data structures and set up adapter
        booksMap = new HashMap<>();
        bookTitles = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bookTitles);
        booksListView.setAdapter(adapter);

        // Set click listener for ListView items
        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedTitle = bookTitles.get(position);
                String selectedBookId = getKeyByValue(booksMap, selectedTitle);
                if (selectedBookId != null) {
                    openViewBookActivity(selectedBookId);
                }
            }
        });
    }

    /**
     * loadUserBooks - Loads the books created by the user from Firebase.
     * Retrieves book titles associated with the user's UID and displays them in the ListView.
     * @param uid The unique identifier of the logged-in user.
     */
    private void loadUserBooks(String uid) {
        booksRef.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                booksMap.clear();
                bookTitles.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot bookSnapshot : snapshot.getChildren()) {
                        String bookTitle = bookSnapshot.child("bookName").getValue(String.class);
                        String bookId = bookSnapshot.getKey();

                        // הוסף רק אם bookTitle אינו null
                        if (bookTitle != null) {
                            bookTitles.add(bookTitle);
                            if (bookId != null) {
                                booksMap.put(bookId, bookTitle);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MyBooksHistory.this, "אין ספרים להצגה", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyBooksHistory.this, "נכשלה הטעינה של הספרים", Toast.LENGTH_SHORT).show();
            }
        });
    }

  /**
     * getKeyByValue - Finds the key associated with a given value in a map.
     * @param map The map to search.
     * @param value The value for which to find the associated key.
     * @return The key associated with the given value, or null if not found.
     */
    private String getKeyByValue(Map<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

   /**
     * openViewBookActivity - Opens the BookCoverActivity to display the selected book's cover.
     * @param bookId The unique identifier of the selected book.
     */
    private void openViewBookActivity(String bookId) {
        Intent intent = new Intent(MyBooksHistory.this, BookCoverActivity.class);
        intent.putExtra("bookId", bookId);
        startActivity(intent);
    }
}
