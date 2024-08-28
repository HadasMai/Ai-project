package com.example.mystoryapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.app.AlertDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, WelcomePage.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // יצירת חלון קופץ
        super.onBackPressed();
        new AlertDialog.Builder(this)
                .setMessage("האם אתה בטוח שברצונך לצאת מהאפליקציה?")
                .setCancelable(false)
                .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // סגור את האפליקציה
                        finishAffinity();
                    }
                })
                .setNegativeButton("לא", null)
                .show();
    }
}
