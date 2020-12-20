package com.autarky.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkRequest;
import android.util.Log;

import androidx.annotation.NonNull;

import com.autarky.variables.AppGolabalVariables;

/**
 * Created by Vishal Bhanot on 30-07-2020,July,2020
 * Company: Autarky Services
 * Support E-mail: vishu.bhanot18@gmail.com
 * Web: https://theautarky.com
 * Phone: +91-78769-22777
 * Skype: vishu1602
 */
public class NetworkService {

    private static final String TAG = NetworkService.class.getSimpleName();
    private static  NetworkService networkServiceInstance=null;

    private NetworkService() {

    }

    public static NetworkService getNetworkServiceInstance(){
        if(networkServiceInstance==null){
            networkServiceInstance= new NetworkService();
        }
             return networkServiceInstance;
    }

    public void registerNetworkCallback(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkRequest.Builder builder = new NetworkRequest.Builder();

            connectivityManager.registerNetworkCallback(builder.build(), new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull android.net.Network network) {
                    super.onAvailable(network);
                    AppGolabalVariables.isNetworkConnected = true;
                    Log.i(TAG, "Connected to internet");
                }

                @Override
                public void onLost(@NonNull android.net.Network network) {
                    super.onLost(network);
                    AppGolabalVariables.isNetworkConnected = false;
                    Log.i(TAG, "Internet not available");
                }
            });

        } catch (Exception e) {
            Log.i(TAG, "Cannot register callbacks");
            AppGolabalVariables.isNetworkConnected = false;
        }
    }
}
