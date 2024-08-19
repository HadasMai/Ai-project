package com.example.mystoryapp;

import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewPage extends AppCompatActivity {

    private EditText editTextText2;
    private Button button;
    private Button reloadButton;
    private Button buttonPrevPage;
    private Button buttonNextPage;
    private Button buttonFinish;
    private ImageView imageView;
    private OkHttpClient client;
    private ProgressDialog progressDialog;
    private static final String TAG = "NewPage";

    private DatabaseReference booksRef;
    private DatabaseReference pagesRef;
    private String bookId;
    private String descriptionOfTheHeroOfStory;
    private String lastGeneratedUrl;
    private long currentPageNumber;
    private String pageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_page);

        editTextText2 = findViewById(R.id.editTextText2);
        button = findViewById(R.id.button);
        reloadButton = findViewById(R.id.reloadButton);
        buttonPrevPage = findViewById(R.id.buttonPrevPage);
        buttonNextPage = findViewById(R.id.buttonNextPage);
        imageView = findViewById(R.id.imageView);
        buttonFinish=findViewById(R.id.EndButton);

        client = new OkHttpClient.Builder()
                .connectTimeout(0, TimeUnit.SECONDS)
                .writeTimeout(0, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)
                .build();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("מכינים את התמונה עבורך, פעולה זו עשויה להימשך מספר שניות...");
        progressDialog.setCancelable(false);

        bookId = getIntent().getStringExtra("bookId");

        if (bookId == null) {
            Toast.makeText(NewPage.this, "bookId לא התקבל", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        booksRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Books");
        pagesRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("pages");

        fetchDataFromFirebase();
        createNewPageInFirebase();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editTextText2.getText().toString();
                sendTextToServer(descriptionOfTheHeroOfStory + text);
            }
        });

        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewPage.this, NewPage.class);
                intent.putExtra("bookId", bookId);
                startActivity(intent);
                finish();
            }
        });

        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewPage.this, BookCoverActivity.class);
                intent.putExtra("bookId", bookId);
                startActivity(intent);
            }
        });

        buttonPrevPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPageNumber > 1) {
                    loadPage(currentPageNumber - 1);
                }
            }
        });

        buttonNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPage(currentPageNumber + 1);
            }
        });
    }

    private void fetchDataFromFirebase() {
        booksRef.child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@Nullable DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String characterType = snapshot.child("characterType").getValue(String.class);
                    String heroName = snapshot.child("heroName").getValue(String.class);

                    if (!"חיה".equals(characterType)) {
                        Long ageLong = snapshot.child("age").getValue(Long.class);
                        String age = ageLong != null ? String.valueOf(ageLong) : "";
                        String skinColor = snapshot.child("skinColor").getValue(String.class);
                        String eyeColor = snapshot.child("eyeColor").getValue(String.class);
                        String hairType = snapshot.child("hairType").getValue(String.class);
                        String hairColor = snapshot.child("hairColor").getValue(String.class);
                        String clothingDescription = snapshot.child("clothingDescription").getValue(String.class);
                        String religion = snapshot.child("religion").getValue(String.class);

                        if (characterType.equals("man") || characterType.equals("boy")) {
                            descriptionOfTheHeroOfStory = "הדמות הראשית הוא " + heroName + " שהוא " + characterType +
                                    " בגיל " + age + ", וצבע העור שלו " + skinColor +
                                    ", עם עיניים " + eyeColor + " ושיער " + hairType +
                                    " בצבע " + hairColor + ", ולובש " + clothingDescription +
                                    " והוא " + religion;
                        } else if (characterType.equals("woman") || characterType.equals("girl")) {
                            descriptionOfTheHeroOfStory = "הדמות הראשית היא " + heroName + " שהיא " + characterType +
                                    " בגיל " + age + ", וצבע העור שלה " + skinColor +
                                    ", עם עיניים " + eyeColor + " ושיער " + hairType +
                                    " בצבע " + hairColor + ", ולובשת " + clothingDescription +
                                    " והיא " + religion;
                        }
                    } else {
                        String animalType = snapshot.child("animalType").getValue(String.class);
                        descriptionOfTheHeroOfStory = heroName + " הוא חיה מסוג: " + animalType;
                    }
                } else {
                    descriptionOfTheHeroOfStory = "";
                    Toast.makeText(NewPage.this, "Failed to retrieve data from Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@Nullable DatabaseError error) {
                descriptionOfTheHeroOfStory = "";
                Toast.makeText(NewPage.this, "Failed to retrieve data from Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewPageInFirebase() {
        pagesRef.orderByChild("bookId").equalTo(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@Nullable DataSnapshot snapshot) {
                long pageCount = snapshot.getChildrenCount();
                pageId = pagesRef.push().getKey(); // Generate a unique page ID

                if (pageId != null) {
                    HashMap<String, Object> pageData = new HashMap<>();
                    pageData.put("bookId", bookId);
                    pageData.put("pageId", pageId);
                    pageData.put("pageNumber", pageCount + 1);

                    pagesRef.child(pageId).setValue(pageData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(NewPage.this, "New page created", Toast.LENGTH_SHORT).show();
                                currentPageNumber = pageCount + 1;
                                updateNavigationButtons();
                            })
                            .addOnFailureListener(e -> Toast.makeText(NewPage.this, "Failed to create new page", Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(NewPage.this, "Failed to generate page ID", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@Nullable DatabaseError error) {
                Toast.makeText(NewPage.this, "Failed to retrieve page count", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendTextToServer(String text) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("data", text);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url("http://192.168.73.55:5000/getText")
                .post(body)
                .build();

        progressDialog.show();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Request failed", e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(NewPage.this, "Request Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    lastGeneratedUrl = responseData; // Save the last generated URL
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(NewPage.this, responseData, Toast.LENGTH_SHORT).show();

                            Log.d(TAG, "Server response: " + responseData);
                            loadImage(responseData);

                            // Update the existing page in Firebase
                            updatePageInFirebase(editTextText2.getText().toString(), lastGeneratedUrl);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(NewPage.this, "Response not successful", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void loadImage(String url) {
        Log.d(TAG, "Loading image from URL: " + url);

        String myurl = url.replace("\"", "");

        Log.d(TAG, "Loading image from myurl: " + myurl);

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_delete)
                .override(371, 361);

        Glide.with(NewPage.this)
                .load(myurl)
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "Image load failed", e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "Image loaded successfully");
                        return false;
                    }
                })
                .into(imageView);
    }

    private void updatePageInFirebase(final String text, final String url) {
        if (pageId != null) {
            HashMap<String, Object> pageData = new HashMap<>();
            pageData.put("text", text);
            pageData.put("url", url);
            pageData.put("style", "");

            pagesRef.child(pageId).updateChildren(pageData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(NewPage.this, "Page updated successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(NewPage.this, "Failed to update page", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(NewPage.this, "Page ID is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPage(long pageNumber) {
        // בדוק ועדכן את הטקסט של העמוד הנוכחי לפני שעוברים לעמוד חדש
        updateCurrentPageText();
        pagesRef.orderByChild("bookId").equalTo(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@Nullable DataSnapshot snapshot) {
                for (DataSnapshot pageSnapshot : snapshot.getChildren()) {
                    Long pgNumber = pageSnapshot.child("pageNumber").getValue(Long.class);
                    if (pgNumber != null && pgNumber == pageNumber) {
                        pageId = pageSnapshot.getKey();
                        String text = pageSnapshot.child("text").getValue(String.class);
                        String url = pageSnapshot.child("url").getValue(String.class);

                        editTextText2.setText(text);
                        if (url != null && !url.isEmpty()) {
                            loadImage(url);
                        }

                        currentPageNumber = pageNumber;
                        updateNavigationButtons();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@Nullable DatabaseError error) {
                Toast.makeText(NewPage.this, "Failed to load page", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCurrentPageText() {
        if (pageId != null) {
            String currentText = editTextText2.getText().toString();
            pagesRef.child(pageId).child("text").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@Nullable DataSnapshot snapshot) {
                    String dbText = snapshot.getValue(String.class);
                    if (dbText != null && !dbText.equals(currentText)) {
                        // עדכן את הטקסט ב-Firebase אם הוא שונה ממה שיש ב-EditText
                        pagesRef.child(pageId).child("text").setValue(currentText)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Text updated in Firebase"))
                                .addOnFailureListener(e -> Log.e(TAG, "Failed to update text in Firebase", e));
                    }
                }

                @Override
                public void onCancelled(@Nullable DatabaseError error) {
                    Log.e(TAG, "Failed to read text from Firebase", error.toException());
                }
            });
        }
    }

    private void updateNavigationButtons() {
        buttonPrevPage.setVisibility(currentPageNumber > 1 ? View.VISIBLE : View.GONE);
        pagesRef.orderByChild("bookId").equalTo(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@Nullable DataSnapshot snapshot) {
                long totalPages = snapshot.getChildrenCount();
                buttonNextPage.setVisibility(currentPageNumber < totalPages ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@Nullable DatabaseError error) {
                Toast.makeText(NewPage.this, "Failed to update navigation buttons", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
