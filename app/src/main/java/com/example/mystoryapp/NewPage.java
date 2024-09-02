package com.example.mystoryapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

public class NewPage extends AppCompatActivity {

    private EditText editTextText2;
    private Button button;
    private Button reloadButton;
    private Button buttonPrevPage;
    private Button buttonNextPage;
    private Button buttonFinish;
    private ImageView imageView;
    private OkHttpClient client;
    private static final String TAG = "NewPage";

    private DatabaseReference booksRef;
    private DatabaseReference pagesRef;
    private String bookId;
    private String descriptionOfTheHeroOfStory;
    private String lastGeneratedUrl;
    private long currentPageNumber;
    private String pageId;
    private boolean isEditing;
    private ProgressDialog progressDialog;
    private Spinner styleSpinner;
    private String selectedStyle;
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
        buttonFinish = findViewById(R.id.EndButton);
        styleSpinner = findViewById(R.id.styleSpinner);

        client = new OkHttpClient.Builder()
                .connectTimeout(0, TimeUnit.SECONDS)
                .writeTimeout(0, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)
                .build();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("מכינים את התמונה עבורך, פעולה זו עשויה להימשך מספר שניות...");
        progressDialog.setCancelable(false);

        bookId = getIntent().getStringExtra("bookId");
        isEditing = getIntent().getBooleanExtra("isEditing", false);
        currentPageNumber = getIntent().getLongExtra("pageNumber", 1);

        if (bookId == null) {
            Toast.makeText(NewPage.this, "bookId לא התקבל", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        booksRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Books");
        pagesRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("pages");

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.style_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        styleSpinner.setAdapter(adapter);

        styleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStyle = parent.getItemAtPosition(position).toString();
                updatePageInFirebase(editTextText2.getText().toString(), lastGeneratedUrl);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        fetchDataFromFirebase();

        if (isEditing) {
            loadExistingPage(currentPageNumber);
            isEditing = false;
        } else {
            createNewPageInFirebase();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editTextText2.getText().toString();
                sendTextToServer(descriptionOfTheHeroOfStory + text +"תמונה בסגנון: " + selectedStyle );
            }
        });

        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewPage.this, NewPage.class);
                intent.putExtra("bookId", bookId);
                intent.putExtra("isEditing", isEditing);
                intent.putExtra("pageNumber", currentPageNumber);

                startActivity(intent);
                finish();
            }
        });

        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePageInFirebase(editTextText2.getText().toString(), lastGeneratedUrl);
                Intent intent = new Intent(NewPage.this, BookCoverActivity.class);
                intent.putExtra("bookId", bookId);
                startActivity(intent);
            }
        });

        buttonPrevPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPageNumber > 1) {
                    updatePageInFirebase(editTextText2.getText().toString(), lastGeneratedUrl);
                    loadExistingPage(currentPageNumber - 1);
                }
            }
        });

        buttonNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePageInFirebase(editTextText2.getText().toString(), lastGeneratedUrl);
                loadExistingPage(currentPageNumber + 1);
            }
        });

    }
    private void showInstructionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewPage.this);
        builder.setTitle("הוראות");
        builder.setMessage("ראשית יש להזין טקסט לתוך תיבת הטקסט, ולאחר מכן יש לבחור סגנון תמונה שאתם רוצים,\n " +
                "ואז לחצו על הכפתור צור תמונה, וקבלו לאחר מספר שניות תמונה.\n\n" +
                "לא אהבתם את התמונה? אין בעיה! ניתן ללחוץ שוב על צור תמונה וקבלו תמונה חדשה!\n\n" +
                "הנך מועבר/ת לדף חדש.\nבכל שלב אפשר לנווט בין הדפים על ידי שימוש בחיצים.");
        builder.setPositiveButton("הבנתי", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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

    private void loadExistingPage(long pageNumber) {
        pagesRef.orderByChild("bookId").equalTo(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@Nullable DataSnapshot snapshot) {
                boolean pageFound = false;
                for (DataSnapshot pageSnapshot : snapshot.getChildren()) {
                    Long pgNumber = pageSnapshot.child("pageNumber").getValue(Long.class);
                    if (pgNumber != null && pgNumber == pageNumber) {
                        pageId = pageSnapshot.getKey();
                        String text = pageSnapshot.child("text").getValue(String.class);
                        String url = pageSnapshot.child("url").getValue(String.class);
                        String style = pageSnapshot.child("style").getValue(String.class);
                        if (style != null && !style.isEmpty()) {
                            int spinnerPosition = ((ArrayAdapter) styleSpinner.getAdapter()).getPosition(style);
                            styleSpinner.setSelection(spinnerPosition);
                        }
                        editTextText2.setText(text);
                        if (url != null && !url.isEmpty()) {
                            loadImage(url);
                            lastGeneratedUrl = url;
                        }
                        else {
                            lastGeneratedUrl = "";
                        }

                        currentPageNumber = pageNumber;
                        updateNavigationButtons();
                        pageFound = true;
                        break;
                    }
                }
                if (!pageFound) {
                    Toast.makeText(NewPage.this, "העמוד לא נמצא, יוצר עמוד חדש...", Toast.LENGTH_SHORT).show();
                    createNewPageInFirebase();
                }
            }

            @Override
            public void onCancelled(@Nullable DatabaseError error) {
                Toast.makeText(NewPage.this, "Failed to load page", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewPageInFirebase() {
        pagesRef.orderByChild("bookId").equalTo(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@Nullable DataSnapshot snapshot) {
                long pageCount = snapshot.getChildrenCount();
                pageId = pagesRef.push().getKey();

                if (pageId != null) {
                    HashMap<String, Object> pageData = new HashMap<>();
                    pageData.put("bookId", bookId);
                    pageData.put("pageId", pageId);
                    pageData.put("pageNumber", pageCount + 1);

                    pagesRef.child(pageId).setValue(pageData)
                            .addOnSuccessListener(aVoid -> {
                               // Toast.makeText(NewPage.this, "New page created", Toast.LENGTH_SHORT).show();
                                currentPageNumber = pageCount + 1;
                                // Show instructions dialog only on the first page
                                if (currentPageNumber == 1) {
                                    showInstructionsDialog();
                                }
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
                .url("http://192.168.71.110:5000/getText")
                .post(body)
                .build();

        progressDialog.show();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Request failed", e);
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(NewPage.this, "הבקשה נכשלה", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    lastGeneratedUrl = responseData;
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
//                          Toast.makeText(NewPage.this, responseData, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Server response: " + responseData);
                        loadImage(responseData);
                        updatePageInFirebase(editTextText2.getText().toString(), lastGeneratedUrl);
                    });
                } else {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(NewPage.this, "Response not successful", Toast.LENGTH_SHORT).show();
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
            pageData.put("style", selectedStyle);

            pagesRef.child(pageId).updateChildren(pageData)
                    // .addOnSuccessListener(aVoid -> Toast.makeText(NewPage.this, "Page updated successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(NewPage.this, "Failed to update page", Toast.LENGTH_SHORT).show());
        }
//        else {
//            Toast.makeText(NewPage.this, "Page ID is null", Toast.LENGTH_SHORT).show();
//        }
    }
    private void updateNavigationButtons() {
        buttonPrevPage.setVisibility(currentPageNumber > 1 ? View.VISIBLE : View.GONE);
        pagesRef.orderByChild("bookId").equalTo(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@Nullable DataSnapshot snapshot) {
                long totalPages = snapshot.getChildrenCount();
                buttonNextPage.setVisibility(currentPageNumber < totalPages ? View.VISIBLE : View.GONE);
                reloadButton.setVisibility(currentPageNumber == totalPages ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@Nullable DatabaseError error) {
                Toast.makeText(NewPage.this, "Failed to update navigation buttons", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

