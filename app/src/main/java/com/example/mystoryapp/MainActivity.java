//package com.example.mystoryapp;
//
//import android.graphics.drawable.Drawable;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import android.app.ProgressDialog;
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
//
//public class MainActivity extends AppCompatActivity {
//
//    private EditText editTextText2;
//    private Button button;
//    private ImageView imageView;
//    private OkHttpClient client;
//    private ProgressDialog progressDialog;
//    private static final String TAG = "MainActivity";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        editTextText2 = findViewById(R.id.editTextText2);
//        button = findViewById(R.id.button);
//        imageView = findViewById(R.id.imageView);
//
//        client = new OkHttpClient.Builder()
//                .connectTimeout(0, TimeUnit.SECONDS)
//                .writeTimeout(0, TimeUnit.SECONDS)
//                .readTimeout(0, TimeUnit.SECONDS)
//                .build();
//
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Waiting for server response...");
//        progressDialog.setCancelable(false);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String inputText = editTextText2.getText().toString();
//                if (inputText.isEmpty()) {
//                    Toast.makeText(MainActivity.this, "Please enter text", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                progressDialog.show();
//                sendRequestToServer(inputText);
//            }
//        });
//    }
//
//    private void sendRequestToServer(String inputText) {
//        String url = "YOUR_SERVER_URL";
//        MediaType JSON = MediaType.get("application/json; charset=utf-8");
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("text", inputText);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        progressDialog.dismiss();
//                        Toast.makeText(MainActivity.this, "Request Failed", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    String responseData = response.body().string();
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            progressDialog.dismiss();
//                            Log.d(TAG, "Server response: " + responseData);
//                            loadImage(responseData);
//                        }
//                    });
//                } else {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            progressDialog.dismiss();
//                            Toast.makeText(MainActivity.this, "Response not successful", Toast.LENGTH_SHORT).show();
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
//        Glide.with(MainActivity.this)
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

package com.example.mystoryapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
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

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, HeroStoryQuestions.class);
        startActivity(intent);
    }
}
