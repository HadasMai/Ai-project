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
    private ImageView imageView;
    private OkHttpClient client;
    private ProgressDialog progressDialog;
    private static final String TAG = "NewPage";

    private DatabaseReference booksRef;
    private DatabaseReference pagesRef;
    private String bookId;
    private String descriptionOfTheHeroOfStory;
    private String lastGeneratedUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_page);

        editTextText2 = findViewById(R.id.editTextText2);
        button = findViewById(R.id.button);
        reloadButton = findViewById(R.id.reloadButton);
        imageView = findViewById(R.id.imageView);

        client = new OkHttpClient.Builder()
                .connectTimeout(0, TimeUnit.SECONDS)
                .writeTimeout(0, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)
                .build();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("מכינים את התמונה עבורך, פעולה זו עשויה להימשך מספר שניות...");
        progressDialog.setCancelable(false);

        // Get the bookId from the intent
        bookId = getIntent().getStringExtra("bookId");

        // בדיקה אם bookId הוא null
        if (bookId == null) {
            Toast.makeText(NewPage.this, "bookId לא התקבל", Toast.LENGTH_SHORT).show();
            finish(); // חזרה לפעילות הקודמת
            return;
        }
        booksRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Books");
        pagesRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("pages");

        // Fetch data from Firebase
        fetchDataFromFirebase();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editTextText2.getText().toString();
                sendTextToServer( descriptionOfTheHeroOfStory + text);
            }
        });

        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add new page to Firebase
                addNewPageToFirebase();
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
                        // Convert age to String
                        Long ageLong = snapshot.child("age").getValue(Long.class);
                        String age = ageLong != null ? String.valueOf(ageLong) : "";
                        String skinColor = snapshot.child("skinColor").getValue(String.class);
                        String eyeColor = snapshot.child("eyeColor").getValue(String.class);
                        String hairType = snapshot.child("hairType").getValue(String.class);
                        String hairColor = snapshot.child("hairColor").getValue(String.class);
                        String clothingDescription = snapshot.child("clothingDescription").getValue(String.class);
                        String religion = snapshot.child("religion").getValue(String.class);

                        descriptionOfTheHeroOfStory = "הדמות הראשית היא " + heroName + " שהיא " + characterType +
                                " בגיל " + age + ", וצבע העור שלה " + skinColor +
                                ", עם עיניים " + eyeColor + " ושיער " + hairType +
                                " בצבע " + hairColor + ", ולובשת " + clothingDescription +
                                " והיא " + religion;
                    } else {
                        String animalType = snapshot.child("animalType").getValue(String.class);
                        descriptionOfTheHeroOfStory = heroName + " הוא " + animalType;
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
                .url("http://192.168.18.117:5000/getText")
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

        // Use Glide to load the image from the URL into the ImageView
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(android.R.drawable.ic_menu_gallery) // Placeholder while loading
                .error(android.R.drawable.ic_delete) // Placeholder if image fails to load
                .override(371, 361); // Set fixed dimensions for the image

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

    private void addNewPageToFirebase() {
        // Get the current page count for the book
        pagesRef.orderByChild("bookId").equalTo(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@Nullable DataSnapshot snapshot) {
                long pageCount = snapshot.getChildrenCount();
                String pageId = pagesRef.push().getKey(); // Generate a unique page ID
                if (pageId != null) {
                    HashMap<String, Object> pageData = new HashMap<>();
                    pageData.put("bookId", bookId);
                    pageData.put("pageId", pageId);
                    pageData.put("text", editTextText2.getText().toString());
                    pageData.put("url", lastGeneratedUrl);
                    pageData.put("style", ""); // You can customize the style if needed
                    pageData.put("pageNumber", pageCount + 1);

                    pagesRef.child(pageId).setValue(pageData)
                            .addOnSuccessListener(aVoid -> Toast.makeText(NewPage.this, "Page added successfully", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(NewPage.this, "Failed to add page", Toast.LENGTH_SHORT).show());
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
}


//
//package com.example.mystoryapp;
//
//
//import android.graphics.drawable.Drawable;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.bumptech.glide.load.DataSource;
//import com.bumptech.glide.load.engine.GlideException;
//import com.bumptech.glide.request.RequestListener;
//import com.bumptech.glide.request.target.Target;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.util.concurrent.TimeUnit;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.RequestOptions;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//
//
//
//public class NewPage extends AppCompatActivity {
//
//    private EditText editTextText2;
//    private Button button;
//    private Button reloadButton;
//    private ImageView imageView;
//    private OkHttpClient client;
//    private ProgressDialog progressDialog;
//    private static final String TAG = "NewPage";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.new_page);
//
//        editTextText2 = findViewById(R.id.editTextText2);
//        button = findViewById(R.id.button);
//        reloadButton = findViewById(R.id.reloadButton);
//        imageView = findViewById(R.id.imageView);
//
//        client = new OkHttpClient.Builder()
//                .connectTimeout(0, TimeUnit.SECONDS)
//                .writeTimeout(0, TimeUnit.SECONDS)
//                .readTimeout(0, TimeUnit.SECONDS)
//                .build();
//
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("מכינים את התמונה עבורך, פעולה זו עשויה להימשך מספר שניות...");
//        progressDialog.setCancelable(false);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String text = editTextText2.getText().toString();
//                sendTextToServer(text);
//            }
//        });
//
//        reloadButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Reload the activity
//                Intent intent = getIntent();
//                finish();
//                startActivity(intent);
//            }
//        });
//    }
//
//    private void sendTextToServer(String text) {
//        MediaType JSON = MediaType.get("application/json; charset=utf-8");
//
//        JSONObject json = new JSONObject();
//        try {
//            json.put("data", text);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        RequestBody body = RequestBody.create(json.toString(), JSON);
//        Request request = new Request.Builder()
//                .url("http://192.168.99.110:5000/getText")
//                .post(body)
//                .build();
//
//        progressDialog.show();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e(TAG, "Request failed", e);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        progressDialog.dismiss();
//                        Toast.makeText(NewPage.this, "Request Failed", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    final String responseData = response.body().string();
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            progressDialog.dismiss();
//                            Toast.makeText(NewPage.this, responseData, Toast.LENGTH_SHORT).show();
//
//                            Log.d(TAG, "Server response: " + responseData);
//                            loadImage(responseData);
//                        }
//                    });
//                } else {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            progressDialog.dismiss();
//                            Toast.makeText(NewPage.this, "Response not successful", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            }
//        });
//    }
//
//    private void loadImage(String url) {
//        Log.d(TAG, "Loading image from URL: " + url);
//
//        String myurl = url.replace("\"", "");
//
//        Log.d(TAG, "Loading image from myurl: " + myurl);
//
//        // Use Glide to load the image from the URL into the ImageView
//        RequestOptions requestOptions = new RequestOptions()
//                .placeholder(android.R.drawable.ic_menu_gallery) // Placeholder while loading
//                .error(android.R.drawable.ic_delete) // Placeholder if image fails to load
//                .override(371, 361); // Set fixed dimensions for the image
//
//        Glide.with(NewPage.this)
//                .load(myurl)
//                .apply(requestOptions)
//                .listener(new RequestListener<Drawable>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        Log.e(TAG, "Image load failed", e);
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        Log.d(TAG, "Image loaded successfully");
//                        return false;
//                    }
//                })
//                .into(imageView);
//    }
//}
//
