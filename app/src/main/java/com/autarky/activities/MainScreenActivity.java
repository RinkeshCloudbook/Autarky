package com.autarky.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Base64;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.autarky.R;
import com.autarky.constant.ApplicationConstants;
import com.autarky.fragment.AddDeviceFragment;
import com.autarky.fragment.DashboardFragment;
import com.autarky.fragment.SplashFragment;
import com.autarky.fragment.UserAuthFragment;
import com.autarky.fragment.WebViewFragment;
import com.autarky.utils.BaseActivity;
import com.autarky.wifi.WifiMethods;

public class MainScreenActivity extends BaseActivity {

    int backStateName;
    Fragment fragment;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String BACK_STACK_ROOT_TAG = "root_fragment";
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

//        System.out.println(new AppSignatureHelper(mContext).getAppSignatures());
        this.fragmentManager = getSupportFragmentManager();
        this.fragmentTransaction = fragmentManager.beginTransaction();
        this.fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        replaceFragment(new SplashFragment(), true, ApplicationConstants.SPLASH);
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack, String backStackValue/*, String toolbarTile*/) {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flMainFrameLayout, fragment);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(backStackValue);
        } else {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.commit();


    }


    @Override
    public void onBackPressed() {
        this.fragment = fragmentManager.findFragmentById(R.id.flMainFrameLayout);
        if (this.fragment instanceof SplashFragment ||
                this.fragment instanceof DashboardFragment ||
                this.fragment instanceof UserAuthFragment) {
            finishAffinity();
        } else if (this.fragment instanceof WebViewFragment) {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            WifiMethods.WifiDisConnect(wifiManager, mContext);

            Fragment fragment = new AddDeviceFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(ApplicationConstants.backFromWeb, true);
            fragment.setArguments(bundle);
            replaceFragment(fragment, true, ApplicationConstants.AddDevice);
        } else {
            super.onBackPressed();
        }


    }


    private void moveToFragment(Fragment fragment, boolean addToBackStack, String backStackValue, String toolbarTile) {
        Bundle args = new Bundle();
        args.putString("ToolBarTitle", toolbarTile);
        fragment.setArguments(args);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flMainFrameLayout, fragment);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(backStackValue);
        } else {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.flMainFrameLayout);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public Bitmap base64ToBitmap(String imageBase64) {
        byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }


}