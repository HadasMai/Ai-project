/**
 * MainActivity - This is the main launcher activity of the application.
 * It initializes the application by launching the WelcomePage activity.
 * When the user presses the back button, a confirmation dialog appears to confirm if they want to exit the app.
 */

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

    // Tag for logging
    private static final String TAG = "MainActivity";

   /**
     * Called when the activity is first created.
     * Initializes the UI and immediately starts the WelcomePage activity.
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down, this Bundle contains the data it most recently
     * supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Launch the WelcomePage activity
        Intent intent = new Intent(MainActivity.this, WelcomePage.class);
        startActivity(intent);
    }

    /**
     * onBackPressed - Overrides the default back button behavior.
     * When the back button is pressed, shows a confirmation dialog asking the user if they want to exit the application.
     * If the user confirms, the app will close; otherwise, the dialog is dismissed.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new AlertDialog.Builder(this)
                .setMessage("האם אתה בטוח שברצונך לצאת מהאפליקציה?")
                .setCancelable(false)
                .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity();
                    }
                })
                .setNegativeButton("לא", null)
                .show();
    }
}
