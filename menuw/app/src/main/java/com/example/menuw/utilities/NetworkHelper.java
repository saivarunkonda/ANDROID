package com.example.menuw.utilities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.provider.Settings;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class NetworkHelper {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);

                return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            }
        } else {
            return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
        }
        return false;
    }

    public static void showNoInternetSnackbar(android.app.Activity activity, View view) {
        Snackbar.make(view, "No internet connection available.", Snackbar.LENGTH_LONG)
                .setAction("Settings", v -> {
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    activity.startActivity(intent);
                })
                .show();
    }
}
