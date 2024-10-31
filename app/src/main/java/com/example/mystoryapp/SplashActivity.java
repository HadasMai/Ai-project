/**
 * SplashActivity - This activity displays a splash screen with an animated GIF image.
 * It shows the splash screen for a specified duration and then transitions to the MainActivity.
 */

package com.example.mystoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mystoryapp.MainActivity;
import com.example.mystoryapp.R;
import pl.droidsonroids.gif.GifImageView;

public class SplashActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     * Displays a splash screen with an animated GIF and starts MainActivity after a delay.
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most recently
     * supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize and display GIF image in the splash screen
        GifImageView gifImageView = findViewById(R.id.gifImageView);

        // Delayed transition to MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 6300); // Delay set to 6300 milliseconds (6.3 seconds)
    }
}
