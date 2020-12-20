package com.autarky.utils;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import com.autarky.activities.MainScreenActivity;
import com.autarky.constant.ApplicationConstants;
import com.autarky.fragment.DashboardFragment;
import com.autarky.fragment.UserAuthFragment;
import com.autarky.request.PreviousLoginRequest;
import com.autarky.response.UserSignUpResponse;
import com.google.gson.Gson;

import static com.autarky.constant.SharePrefConstants.IS_LOGIN;
import static com.autarky.constant.SharePrefConstants.PREF_NAME;

public class SessionManager {

    // Shared Preferences reference
    private static SharedPreferences preferences;
    // Context
    private MainScreenActivity mContext;
    // Editor reference for Shared preferences
    private SharedPreferences.Editor editor;


    // Constructor
    @SuppressLint("CommitPrefEdits")
    public SessionManager(MainScreenActivity context) {
        this.mContext = context;
        int PRIVATE_MODE = 0;
        preferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    public String getUUID() {
        return preferences.getString("UUID", null);
    }

    public void setUUID() {
        editor.putString("UUID", ApplicationUtils.deviceId());
        editor.commit();
    }


    // Check for login
    public boolean islogin() {
        return preferences.getBoolean(IS_LOGIN, false);
    }

    public void SetLoginSession(boolean isLogin) {
        editor.putBoolean(IS_LOGIN, isLogin);
        editor.apply();
    }

    public UserSignUpResponse getUserResponse() {
        Gson gson = new Gson();
        String json = preferences.getString("UserSignUpResponse", "");
        return gson.fromJson(json, UserSignUpResponse.class);
    }

    public void setUserResponse(UserSignUpResponse userModel) {
        Gson gson = new Gson();
        String json = gson.toJson(userModel);
        editor.putString("UserSignUpResponse", json);
        editor.commit();
    }

    public void setPreviousLoginSession(PreviousLoginRequest previousLoginRequest) {
        Gson gson = new Gson();
        String json = gson.toJson(previousLoginRequest);
        editor.putString("PreviousLoginRequest", json);
        editor.commit();
    }

    public PreviousLoginRequest getPreviousResponse() {
        Gson gson = new Gson();
        String json = preferences.getString("PreviousLoginRequest", "");
        return gson.fromJson(json, PreviousLoginRequest.class);
    }


    public void CheckLogin() {
        if (this.islogin()) {

            mContext.replaceFragment(new DashboardFragment(), true, ApplicationConstants.DASHBOARD);
        } else {

            mContext.replaceFragment(new UserAuthFragment(), true, ApplicationConstants.AUTH);

        }
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        setUserResponse(null);
        SetLoginSession(false);
        mContext.replaceFragment(new UserAuthFragment(),
                false, ApplicationConstants.AUTH);


    }

}
