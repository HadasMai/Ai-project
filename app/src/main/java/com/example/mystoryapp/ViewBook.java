package com.example.mystoryapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ViewBook extends AppCompatActivity {

    private TextView textView;
    private Button buttonPrevPage;
    private Button buttonNextPage;
    private ImageView imageView;

    private DatabaseReference pagesRef;
    private String bookId;
    private List<Page> pages;
    private int currentPageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_book);

        textView = findViewById(R.id.textStory);
        buttonPrevPage = findViewById(R.id.buttonPrevPage);
        buttonNextPage = findViewById(R.id.buttonNextPage);
        imageView = findViewById(R.id.imageStory);

        bookId = getIntent().getStringExtra("bookId");

        if (bookId == null) {
            Toast.makeText(ViewBook.this, "bookId לא התקבל", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pagesRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("pages");

        pages = new ArrayList<>();
        currentPageIndex = 0;

        fetchPagesFromFirebase();

        buttonPrevPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPageIndex > 0) {
                    currentPageIndex--;
                    displayPage(pages.get(currentPageIndex));
                }
            }
        });

        buttonNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPageIndex < pages.size() - 1) {
                    currentPageIndex++;
                    displayPage(pages.get(currentPageIndex));
                }
            }
        });
    }
    private void fetchPagesFromFirebase() {
        Query query = pagesRef.orderByChild("bookId").equalTo(bookId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot pageSnapshot : dataSnapshot.getChildren()) {
                    Page page = pageSnapshot.getValue(Page.class);
                    if (page != null) {
                        pages.add(page);
                    }
                }

                // Sort pages by pageNumber
                Collections.sort(pages, new Comparator<Page>() {
                    @Override
                    public int compare(Page p1, Page p2) {
                        return Long.compare(p1.getPageNumber(), p2.getPageNumber());
                    }
                });

                if (!pages.isEmpty()) {
                    displayPage(pages.get(0));
                } else {
                    Toast.makeText(ViewBook.this, "No pages found for this book", Toast.LENGTH_SHORT).show();
                }

                updateNavigationButtons();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewBook.this, "Failed to load pages: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayPage(Page page) {
        textView.setText(page.getText());
        if (page.getUrl() != null && !page.getUrl().isEmpty()) {
            String cleanUrl = page.getUrl().replace("\"", "");
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_delete)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(ViewBook.this)
                    .load(cleanUrl)
                    .apply(requestOptions)
                    .into(imageView);
        } else {
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        updateNavigationButtons();
    }

    private void updateNavigationButtons() {
        buttonPrevPage.setVisibility(currentPageIndex > 0 ? View.VISIBLE : View.GONE);
        buttonNextPage.setVisibility(currentPageIndex < pages.size() - 1 ? View.VISIBLE : View.GONE);
    }

    // Inner class to represent a page
    private static class Page {
        private String bookId;
        private long pageNumber;
        private String text;
        private String url;

        public Page() {
            // Default constructor required for Firebase
        }

        public String getBookId() {
            return bookId;
        }

        public long getPageNumber() {
            return pageNumber;
        }

        public String getText() {
            return text;
        }

        public String getUrl() {
            return url;
        }
    }
}
