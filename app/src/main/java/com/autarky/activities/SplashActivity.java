package com.autarky.activities;

import android.os.Bundle;

import com.autarky.R;
import com.autarky.utils.BaseActivity;
import com.autarky.utils.NetworkService;

/**
 * Created by Khushboo Jain on 28-08-2020,August,2020
 * Company: Autarky Services
 * Support E-mail: khushboojain942@gmail.com
 * Web: https://theautarky.com
 * Phone: +91-78799-77162
 * Skype: khushboojain942
 */

public class SplashActivity extends BaseActivity {
    NetworkService networkService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        this.networkService = NetworkService.getNetworkServiceInstance();
        this.networkService.registerNetworkCallback(this);
        startActivityHandler();
    }

    private void startActivityHandler() {
//        new Handler(Looper.getMainLooper()).postDelayed(() ->
//                sessionManager.CheckLogin(), SPLASH_DISPLAY_LENGTH);

    }

    @Override
    protected void onResume() {
        super.onResume();
        startActivityHandler();
    }
}