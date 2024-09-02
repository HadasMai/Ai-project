package com.example.mystoryapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.view.Gravity;
public class ViewBook extends AppCompatActivity {

    private TextView textView;
    private Button buttonPrevPage;
    private Button buttonNextPage;
    private ImageView imageView;
    private Button exportPdfButton;
    private Button menuButton;

    private DatabaseReference pagesRef;
    private DatabaseReference booksRef;
    private String bookId;
    private List<Page> pages;
    private int currentPageIndex;
    private DatabaseReference usersRef;

    private static final int STORAGE_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        setContentView(R.layout.activity_view_book);

        textView = findViewById(R.id.textStory);
        buttonPrevPage = findViewById(R.id.buttonPrevPage);
        buttonNextPage = findViewById(R.id.buttonNextPage);
        imageView = findViewById(R.id.imageStory);
        exportPdfButton = findViewById(R.id.exportPdfButton);
        menuButton = findViewById(R.id.menuButton);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        bookId = getIntent().getStringExtra("bookId");

        if (bookId == null) {
            Toast.makeText(ViewBook.this, "bookId לא התקבל", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pagesRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("pages");
        booksRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Books");
        usersRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Users");

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

        exportPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission();
            }
        });
    }

    private void showPopupMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.view_book_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_home) {
                    Intent homeIntent = new Intent(ViewBook.this, UserAccount.class);
                    startActivity(homeIntent);
                    finish();
                    return true;
                } else if (itemId == R.id.action_download) {
                    exportBookToPDF();
                    return true;
                } else if (itemId == R.id.action_edit) {
                    Intent editIntent = new Intent(ViewBook.this, NewPage.class);
                    editIntent.putExtra("bookId", bookId);
                    editIntent.putExtra("pageNumber", currentPageIndex + 1);
                    editIntent.putExtra("isEditing", true);
                    startActivity(editIntent);
                    return true;
                }
                return false;
            }
        });
        popup.show();
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

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_PERMISSION_CODE);
            } else {
                exportBookToPDF();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                exportBookToPDF();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, STORAGE_PERMISSION_CODE);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            } else {
                exportBookToPDF();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportBookToPDF();
            } else {
                Toast.makeText(this, "Storage permission is required to export PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    exportBookToPDF();
                } else {
                    Toast.makeText(this, "Storage permission is required to export PDF", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void exportBookToPDF() {
        booksRef.child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String bookName = dataSnapshot.child("bookName").getValue(String.class);
                String uid = dataSnapshot.child("uid").getValue(String.class);

                if (bookName != null && uid != null) {
                    usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            String userName = userSnapshot.child("name").getValue(String.class);

                            if (userName != null) {
                                try {
                                    PDFExporter.exportBookToPDF(ViewBook.this, pages, bookName, userName);
                                 //   Toast.makeText(ViewBook.this, "PDF exported to Downloads folder", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Log.e("ViewBook", "Error exporting PDF: " + e.getMessage(), e);
                                    Toast.makeText(ViewBook.this, "Failed to export PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(ViewBook.this, "Failed to fetch user name", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(ViewBook.this, "Failed to fetch user details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ViewBook.this, "Failed to fetch book details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewBook.this, "Failed to fetch book details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class Page {
        private String bookId;
        private long pageNumber;
        private String text;
        private String url;
        private String style;
        private String pageId;

        public Page() {
            // Default constructor required for calls to DataSnapshot.getValue(Page.class)
        }

        public String getBookId() { return bookId; }
        public void setBookId(String bookId) { this.bookId = bookId; }

        public long getPageNumber() { return pageNumber; }
        public void setPageNumber(long pageNumber) { this.pageNumber = pageNumber; }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }

        public String getStyle() { return style; }
        public void setStyle(String style) { this.style = style; }

        public String getPageId() { return pageId; }
        public void setPageId(String pageId) { this.pageId = pageId; }
    }
}
