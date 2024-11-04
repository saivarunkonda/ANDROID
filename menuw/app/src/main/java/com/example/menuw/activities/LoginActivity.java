package com.example.menuw.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.menuw.R;
import com.example.menuw.utilities.AppConstants;
import com.example.menuw.utilities.DBHelper;

public class LoginActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private EditText userName;
    private Button button;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userName = findViewById(R.id.editTextUsername);
        EditText password = findViewById(R.id.editTextPassword);
        button = findViewById(R.id.btnLogin);
        dbHelper = DBHelper.getInstance(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userName.getText().toString().trim();
                String passwd = password.getText().toString().trim();

                if (username.isEmpty()) {
                    userName.setError("Please enter username");
                    setLoginUnsuccessful();
                } else if (passwd.isEmpty()) {
                    password.setError("Please enter password");
                    setLoginUnsuccessful();
                } else {
                    if (username.equals(AppConstants.ADMIN)) {
                        if (dbHelper.getUserLoginCredentials(AppConstants.ADMIN) != null && passwd.equals(AppConstants.ADMIN)) {
                            setAdminLoginSuccessful(username);
                            Intent intent = new Intent(LoginActivity.this, Admin.class);
                            startActivity(intent);
                            finish();
                        } else {
                            setLoginUnsuccessful();
                            Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (dbHelper.getUserLoginCredentials(username) != null) {
                            if (dbHelper.checkLogin(username, passwd) || (dbHelper.getUserLoginCredentials(username) != null && dbHelper.getUserLoginCredentials(username).second.equals(passwd))) {
                                setUserLoginSuccessful(username);
                            }
                        } else if (dbHelper.numberOfUsers() < 10) {
                            dbHelper.insertUser(username, passwd);
                            setUserLoginSuccessful(username);
                        } else {
                            setLoginUnsuccessful();
                            Toast.makeText(LoginActivity.this, "Maximum number of users reached. Please try with existing username.", Toast.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent(LoginActivity.this, User.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }

    private void setLoginSuccessful(String userName, boolean admin, boolean user) {
        sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AppConstants.USER_NAME, userName);
        editor.putBoolean(AppConstants.ADMIN_LOGIN_SUCCESSFUL, admin);
        editor.putBoolean(AppConstants.USER_LOGIN_SUCCESSFUL, user);
        editor.apply();
    }

    private void setUserLoginSuccessful(String user) {
        setLoginSuccessful(user, false, true);
    }

    private void setAdminLoginSuccessful(String user) {
        setLoginSuccessful(user, true, false);
    }

    private void setLoginUnsuccessful() {
        setLoginSuccessful("", false, false);
    }
}