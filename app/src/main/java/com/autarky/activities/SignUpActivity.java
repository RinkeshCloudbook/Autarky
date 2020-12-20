package com.autarky.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.autarky.R;
import com.autarky.utils.BaseActivity;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setToolbarTitle(getResources().getString(R.string.sign_up));
        findViewById(R.id.rlProfilePic).setOnClickListener(this);
        findViewById(R.id.btnRegister).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlProfilePic:
                getPermissionToStorage();
                break;
            case R.id.btnRegister:
                startActivity(new Intent(mContext, OtpActivity.class));
                break;
        }
    }
}