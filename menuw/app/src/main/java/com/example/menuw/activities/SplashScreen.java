package com.example.menuw.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.menuw.R;
import com.example.menuw.utilities.AppConstants;
import com.squareup.picasso.Picasso;

public class SplashScreen extends AppCompatActivity {

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView imageView = findViewById(R.id.fullscreen_content);
        Picasso.get()
                .load(R.drawable.restaurant)
                .into(imageView);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (checkAdminLogin()) {
                    Intent intent = new Intent(SplashScreen.this, Admin.class);
                    startActivity(intent);
                    finish();
                } else if (checkUserLogin()) {
                    Intent intent = new Intent(SplashScreen.this, User.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 3000);
    }

    private boolean checkAdminLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(AppConstants.ADMIN_LOGIN_SUCCESSFUL, false);
    }

    private boolean checkUserLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(AppConstants.USER_LOGIN_SUCCESSFUL, false);
    }
}