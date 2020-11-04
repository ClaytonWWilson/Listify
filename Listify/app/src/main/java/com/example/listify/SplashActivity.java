package com.example.listify;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    public static boolean showSplash = true;

    @Override
    public void onBackPressed() {}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showSplash = false;
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra("prev", "Splash");
                startActivity(intent);
            }
        }, 3000);
    }
}