package com.example.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkManager {
    Context mContext;

    public NetworkManager(Context context) {
        mContext = context;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (networkCapabilities == null) return false;
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);

        }

        return false;
    }

}
