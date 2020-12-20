package com.autarky.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.autarky.R;
import com.autarky.utils.BaseActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

import static com.autarky.constant.ApplicationConstants.RC_SIGN_IN;
import static com.autarky.variables.AppGolabalVariables.isNetworkConnected;

/**
 * Created by Khushboo Jain on 28-08-2020,August,2020
 * Company: Autarky Services
 * Support E-mail: khushboojain942@gmail.com
 * Web: https://theautarky.com
 * Phone: +91-78799-77162
 * Skype: khushboojain942
 */
public class UserAuthActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = UserAuthActivity.class.toString();
    GoogleSignInClient mGoogleSignInClient;

    LoginButton btnFacebookButton;
    private CallbackManager mFBCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_auth);

        btnFacebookButton = findViewById(R.id.btnFacebookButton);
        //facebook
        mFBCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logOut();

        findViewById(R.id.facebook_signin).setOnClickListener(this);
        findViewById(R.id.google_signin).setOnClickListener(this);
        findViewById(R.id.mobile_signin).setOnClickListener(this);

        setUpGoogleClient();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.facebook_signin:
                // facebook login
                if (isNetworkConnected) {
                    showProgressDialog();
                    LoginManager.getInstance().logOut();
                    LoginManager.getInstance().logInWithReadPermissions(mContext,
                            Collections.singletonList("public_profile, email"));
                    userinfo(btnFacebookButton);
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.net_connection), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.google_signin:
                // google login
                if (isNetworkConnected) {
                    showProgressDialog();
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.net_connection), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.mobile_signin:
                startActivity(new Intent(mContext, SignUpActivity.class));

                break;
        }
    }

    private void setUpGoogleClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFBCallbackManager.onActivityResult(requestCode, resultCode, data); //facebook
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        hideProgressDialog();
        if (completedTask.isSuccessful()) {
            try {
                GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                if (account != null) {
//                    socialId = account.getId();
//                    String personName = account.getDisplayName();
//                    firstName = account.getGivenName();
//                    lstName = account.getFamilyName();
//                    mobileNumber = "";
////            String personPhotoUrl = account.getPhotoUrl().toString();
//                    emailString = account.getEmail();
////                printLog(TAG, "477 : personId: " + personId);
////                printLog(TAG, "478 : personName: " + personName);
////                printLog(TAG, "479 : firstName: " + firstName);
////                printLog(TAG, "480 : lastName: " + lastName);
////                printLog(TAG, "481 : email: " + email);
//
//                    appType = "google";
//                    mProgressDialog.dismiss();
//                    mGoogleSignInClient.signOut();
//
////                    if (emailString != null) {
//                    if (socialId != null) {
//                        loginNow(socialId,
//                                "", "google");
//                    }

                }


            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(this, "Google Plus Authentication failed. Please try later.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Google Plus Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }

    /*login using facebook*/
    private void userinfo(LoginButton loginButton) {
        loginButton.registerCallback(mFBCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String accessToken = loginResult.getAccessToken().getToken();
//                printLog(TAG, "237 : onSuccess: " + accessToken);
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
//                        printLog(TAG, "244 : onCompleted: " + response.toString());
                        Bundle bFacebookData = getFacebookData(object);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name,middle_name, last_name, email,gender, location"); // Parámetros que pedimos a facebook
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                hideProgressDialog();
                Toast.makeText(mContext, "Facebook Authentication failed.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
//                printLog(TAG, "262 : onError: " + exception.getMessage());
                hideProgressDialog();
                Toast.makeText(mContext, "Connection failed. Retry", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Bundle getFacebookData(JSONObject object) {
        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");
            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.e("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());
                String myPicture = profile_pic.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("middle_name"))
                bundle.putString("middle_name", object.getString("middle_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
//            if (object.has("birthday"))
//                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

//            emailString = bundle.getString("email");
//            socialId = bundle.getString("idFacebook");
//            firstName = bundle.getString("first_name");
//            String middle_name = bundle.getString("middle_name");
//            lstName = bundle.getString("last_name");
//            gender = bundle.getString("gender");
////            String birthday = bundle.getString("birthday");
//            String location = bundle.getString("location");
//            mobileNumber = bundle.getString("user_mobile_phone");

//            printLog(TAG, "269 : getFacebookData: " + userEmail + "," + userFbId + "," + userName + "," + middle_name + "," + userLastName + "," + gender + ","  + "," + location + ",");
//            userFacebookLogin(userName + "," + middle_name + "," + userLastName, userEmail, userFbId);

            hideProgressDialog();
//            appType = "facebook";
            LoginManager.getInstance().logOut();
//            if (socialId != null) {
//                loginNow(socialId,
//                        "", "facebook");
//            }

            return bundle;
        } catch (JSONException e) {
            e.printStackTrace();
            hideProgressDialog();
        }
        return null;
    }

}