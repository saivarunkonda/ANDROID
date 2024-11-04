package com.example.menuw.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.menuw.R;
import com.example.menuw.fragments.FoodItemFragment;
import com.example.menuw.fragments.MenusFragment;
import com.example.menuw.fragments.RestaurantFragment;
import com.example.menuw.fragments.RestaurantViewFragment;
import com.example.menuw.utilities.AppConstants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class User extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar toolbar = findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottom_navigation_user);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.user_fragment_container, new MenusFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            Fragment selectedFragment = null;

            if (item.getItemId() == AppConstants.MENU_FRAGMENT) {
                selectedFragment = new RestaurantFragment();
            } else if (item.getItemId() == AppConstants.ORDERS_FRAGMENT) {
                selectedFragment = new FoodItemFragment();
            } else if (item.getItemId() == AppConstants.ORDERS_HISTORY_FRAGMENT) {
                selectedFragment = new RestaurantViewFragment();
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.user_fragment_container, selectedFragment).commit();
            }
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(AppConstants.USER_LOGIN_SUCCESSFUL);
            editor.remove(AppConstants.USER_NAME);
            editor.apply();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}