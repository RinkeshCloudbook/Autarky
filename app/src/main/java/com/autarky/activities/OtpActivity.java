package com.autarky.activities;

import android.os.Bundle;

import com.autarky.R;
import com.autarky.utils.BaseActivity;

public class OtpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        setToolbarTitle(getResources().getString(R.string.otp));
    }
}