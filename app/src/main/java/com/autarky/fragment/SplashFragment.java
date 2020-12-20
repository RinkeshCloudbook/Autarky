package com.autarky.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.autarky.R;
import com.autarky.constant.ApplicationConstants;
import com.autarky.utils.BaseFragment;
import com.autarky.utils.NetworkService;

import static com.autarky.constant.ApplicationConstants.SPLASH_DISPLAY_LENGTH;

public class SplashFragment extends BaseFragment {
    NetworkService networkService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_splash, container, false);
        this.networkService = NetworkService.getNetworkServiceInstance();
        this.networkService.registerNetworkCallback(mContext);

        return view;
    }


    private void startActivityHandler() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

                    if (sessionManager.islogin()) {
                        mContext.replaceFragment(new DashboardFragment(), true, ApplicationConstants.DASHBOARD);
                    } else {
                        mContext.replaceFragment(new UserAuthFragment(), true, ApplicationConstants.AUTH);
                    }
                }
                , SPLASH_DISPLAY_LENGTH);

        if (TextUtils.isEmpty(sessionManager.getUUID())) {
            sessionManager.setUUID();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        startActivityHandler();
    }
}