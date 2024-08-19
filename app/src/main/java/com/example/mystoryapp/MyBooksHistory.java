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

    private ListView booksListView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference booksRef;
    private Map<String, String> booksMap;
    private List<String> bookTitles;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books_history);

        booksListView = findViewById(R.id.booksListView);
        firebaseAuth = FirebaseAuth.getInstance();
        booksRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Books");

        String uid = firebaseAuth.getUid();

        if (uid != null) {
            loadUserBooks(uid);
        } else {
            Toast.makeText(this, "משתמש לא מחובר", Toast.LENGTH_SHORT).show();
        }

        booksMap = new HashMap<>();
        bookTitles = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bookTitles);
        booksListView.setAdapter(adapter);

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
                        bookTitles.add(bookTitle != null ? bookTitle : "שם לא זמין");
                        if (bookId != null) {
                            booksMap.put(bookId, bookTitle != null ? bookTitle : "שם לא זמין");
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

    private String getKeyByValue(Map<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void openViewBookActivity(String bookId) {
        Intent intent = new Intent(MyBooksHistory.this, BookCoverActivity.class);
        intent.putExtra("bookId", bookId);
        startActivity(intent);
    }
}