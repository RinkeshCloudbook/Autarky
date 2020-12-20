package com.autarky.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.autarky.R;
import com.autarky.constant.ApplicationConstants;
import com.autarky.network.APIServiceGenerator;
import com.autarky.request.MailExistanceRequest;
import com.autarky.request.PhoneExistanceRequest;
import com.autarky.request.PreviousLoginRequest;
import com.autarky.response.ApplicationBaseResponse;
import com.autarky.response.UserSignUpResponse;
import com.autarky.utils.ApplicationUtils;
import com.autarky.utils.BaseFragment;
import com.autarky.utils.RetryClickListener;
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

import org.apache.commons.httpclient.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

import static com.autarky.constant.ApplicationConstants.RC_SIGN_IN;
import static com.autarky.utils.ApplicationUtils.hideProgressDialog;
import static com.autarky.utils.ApplicationUtils.showProgressDialog;
import static com.autarky.variables.AppGolabalVariables.isNetworkConnected;

public class UserAuthFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = UserAuthFragment.class.toString();
    GoogleSignInClient mGoogleSignInClient;

    LoginButton btnFacebookButton;
    private CallbackManager mFBCallbackManager;
    Bundle bundle;
    ImageView logo;

    CircleImageView profile_image;
    ImageView ivLastLoginType;
    TextView tvPreviousTxt;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_user_auth, container, false);
//        printHashKey(mContext);

        btnFacebookButton = view.findViewById(R.id.btnFacebookButton);
        profile_image = view.findViewById(R.id.profile_image);
        ivLastLoginType = view.findViewById(R.id.ivLastLoginType);
        tvPreviousTxt = view.findViewById(R.id.tvPreviousTxt);
        logo = view.findViewById(R.id.logo);
        //facebook
        mFBCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logOut();

        view.findViewById(R.id.facebook_signin).setOnClickListener(this);
        view.findViewById(R.id.google_signin).setOnClickListener(this);
        view.findViewById(R.id.mobile_signin).setOnClickListener(this);

        setUpGoogleClient();

        setUpPreviousProfile();
        return view;
    }


    private void setUpPreviousProfile() {
        if (sessionManager.getPreviousResponse() == null) {
            profile_image.setVisibility(View.GONE);
        } else {
            PreviousLoginRequest loginRequest = sessionManager.getPreviousResponse();
            if (TextUtils.isEmpty(loginRequest.getProfilePic())) {
                profile_image.setVisibility(View.GONE);
            } else {
                profile_image.setVisibility(View.VISIBLE);
                if (URLUtil.isValidUrl(loginRequest.getProfilePic())) {
                    ApplicationUtils.loadImageViaGlide(mContext, loginRequest.getProfilePic(), profile_image, R.drawable.ic_user);
                } else {
                    Bitmap bitmap = mContext.base64ToBitmap(loginRequest.getProfilePic());
                    if (bitmap == null) {
                        profile_image.setImageResource(R.drawable.ic_user);

                    } else {
                        profile_image.setImageBitmap(bitmap);
                    }
                }
            }

            tvPreviousTxt.setText(loginRequest.getName());

            switch (loginRequest.getLoginFrom()) {
                case ApplicationConstants.Facebook:
                    ivLastLoginType.setImageResource(R.drawable.ic_facebook);
                    break;
                case ApplicationConstants.Google:
                    ivLastLoginType.setImageResource(R.drawable.ic_google);
                    break;
                case ApplicationConstants.Mobile:
                    ivLastLoginType.setImageResource(R.drawable.ic_phone);
                    break;
            }
        }
    }

    private void setUpGoogleClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(mContext, gso);

        mGoogleSignInClient.signOut();

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.facebook_signin:
                // facebook login
                if (isNetworkConnected) {
                    showProgressDialog(mContext);
                    LoginManager.getInstance().logOut();
                    LoginManager.getInstance().logInWithReadPermissions(mContext,
                            Collections.singletonList("public_profile, email"));
                    userinfo(btnFacebookButton);
                } else {
                    ApplicationUtils.showToastSingle(mContext, view, getResources().getString(R.string.net_connection));

                }
                break;
            case R.id.google_signin:
                // google login
                if (isNetworkConnected) {
                    showProgressDialog(mContext);
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                } else {
                    ApplicationUtils.showToastSingle(mContext, view, getResources().getString(R.string.net_connection));
                }
                break;
            case R.id.mobile_signin:
//                mContext.replaceFragment(new DashboardFragment(), ApplicationConstants.DASHBOARD);
                mobileLogin();
//                startActivity(new Intent(mContext, SignUpActivity.class));

                break;
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        hideProgressDialog();
        if (completedTask.isSuccessful()) {
            try {
                GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                if (account != null) {
                    mGoogleSignInClient.signOut();


                    bundle = new Bundle();
                    bundle.putString("first_name", account.getGivenName());
                    bundle.putString("last_name", account.getFamilyName());
                    bundle.putString(ApplicationConstants.loginType, ApplicationConstants.Google);
                    bundle.putString(ApplicationConstants.email, account.getEmail());
                    bundle.putString("social_id", account.getId());
                    if (account.getPhotoUrl() != null) {
                        bundle.putString("profile_pic", account.getPhotoUrl().toString());

                    }
                    callMailExits();

                }

            } catch (ApiException e) {
                e.printStackTrace();
                ApplicationUtils.showToastSingle(mContext, logo, "Google Plus Authentication failed. Please try later.");
            }
        } else {
            ApplicationUtils.showToastSingle(mContext, logo, "Google Plus Authentication failed.");
        }
    }

    /*login using facebook*/
    private void userinfo(LoginButton loginButton) {
        hideProgressDialog();
        loginButton.registerCallback(mFBCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String accessToken = loginResult.getAccessToken().getToken();
//                printLog(TAG, "237 : onSuccess: " + accessToken);
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
//                        printLog(TAG, "244 : onCompleted: " + response.toString());
                        bundle = getFacebookData(object);
                        callMailExits();
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
                ApplicationUtils.showToastSingle(mContext, loginButton, "Facebook Authentication failed.");
            }

            @Override
            public void onError(FacebookException exception) {
//                printLog(TAG, "262 : onError: " + exception.getMessage());
                hideProgressDialog();
                ApplicationUtils.showToastSingle(mContext, loginButton, "Connection failed. Retry.");
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
                bundle.putString(ApplicationConstants.profile_pic, profile_pic.toString());
                String myPicture = profile_pic.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
            bundle.putString(ApplicationConstants.loginType, ApplicationConstants.Facebook);
            bundle.putString(ApplicationConstants.social_id, id);
            if (object.has("first_name"))
                bundle.putString(ApplicationConstants.first_name, object.getString("first_name"));
            if (object.has("middle_name"))
                bundle.putString(ApplicationConstants.middle_name, object.getString("middle_name"));
            if (object.has("last_name"))
                bundle.putString(ApplicationConstants.last_name, object.getString("last_name"));
            if (object.has("email"))
                bundle.putString(ApplicationConstants.email, object.getString("email"));
            if (object.has("gender"))
                bundle.putString(ApplicationConstants.gender, object.getString("gender"));
            if (object.has("location"))
                bundle.putString(ApplicationConstants.location, object.getJSONObject("location").getString("name"));

            hideProgressDialog();
            LoginManager.getInstance().logOut();
            return bundle;
        } catch (JSONException e) {
            e.printStackTrace();
            hideProgressDialog();
        }
        return null;
    }


    private void callMailExits() {
        if (isNetworkConnected) {
            showProgressDialog(mContext);
            apiServcies = APIServiceGenerator.getClient(mContext);
            MailExistanceRequest request = new MailExistanceRequest();
            request.setEmail(bundle.getString(ApplicationConstants.email));
            request.setMerchantDeviceId(sessionManager.getUUID());
            Call<ApplicationBaseResponse> mailExistanceCall = apiServcies.API_MAIL_EXISTS(request);
            mailExistanceCall.enqueue(new Callback<ApplicationBaseResponse>() {
                @Override
                public void onResponse(Call<ApplicationBaseResponse> call, Response<ApplicationBaseResponse> response) {
                    if (response.code() == HttpStatus.SC_OK) {
                        ApplicationBaseResponse applicationBaseResponse = response.body();
                        if (applicationBaseResponse != null && applicationBaseResponse.isSuccess()) {
                            callSignInMail();
                        } else {
                            Fragment fragment = new SignUpFragment();
                            fragment.setArguments(bundle);
                            mContext.replaceFragment(fragment, true, ApplicationConstants.SIGN_UP);
                        }
                    } else {
                        hideProgressDialog();
                        ApplicationUtils.showSnackBarRetry(logo,
                                mContext.getString(R.string.something_went_wrong_please_try_again_later)
                                , new RetryClickListener() {
                                    @Override
                                    public void onRetryClick() {
                                        callMailExits();
                                    }
                                });

                    }
                }

                @Override
                public void onFailure(Call<ApplicationBaseResponse> call, Throwable t) {
                    t.printStackTrace();
                    hideProgressDialog();
                    if (t instanceof HttpException) {
                        HttpException exception = (HttpException) t;
                        if (exception.code() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                            ApplicationUtils.showSnackBarRetry(logo,
                                    mContext.getString(R.string.something_went_wrong_please_try_again_later)
                                    , new RetryClickListener() {
                                        @Override
                                        public void onRetryClick() {
                                            callMailExits();
                                        }
                                    });
                        }
                    }

                }
            });

        } else {
            ApplicationUtils.showToastSingle(mContext, logo, getResources().getString(R.string.net_connection));

        }

    }

    private void callPhoneExits(String mobile) {
        if (isNetworkConnected) {
            showProgressDialog(mContext);
            apiServcies = APIServiceGenerator.getClient(mContext);
            PhoneExistanceRequest request = new PhoneExistanceRequest();
            request.setPhoneNo(mobile);
            request.setMerchantDeviceId(sessionManager.getUUID());
            Call<ApplicationBaseResponse> mailExistanceCall = apiServcies.API_PHONE_EXISTS(request);
            mailExistanceCall.enqueue(new Callback<ApplicationBaseResponse>() {
                @Override
                public void onResponse(Call<ApplicationBaseResponse> call, Response<ApplicationBaseResponse> response) {

                    if (response.code() == HttpStatus.SC_OK) {

                        ApplicationBaseResponse applicationBaseResponse = response.body();
                        bundle = new Bundle();
                        bundle.putString(ApplicationConstants.mobile, mobile);
                        bundle.putString(ApplicationConstants.loginType, ApplicationConstants.Mobile);

                        if (applicationBaseResponse != null && applicationBaseResponse.isSuccess()) {
                            callRequestApi();
                        } else {
                            hideProgressDialog();
                            Fragment fragment = new SignUpFragment();
                            fragment.setArguments(bundle);
                            mContext.replaceFragment(fragment, true, ApplicationConstants.SIGN_UP);
                        }
                    } else {
                        hideProgressDialog();
                        ApplicationUtils.showSnackBarRetry(logo,
                                mContext.getString(R.string.something_went_wrong_please_try_again_later)
                                , new RetryClickListener() {
                                    @Override
                                    public void onRetryClick() {
                                        callPhoneExits(mobile);
                                    }
                                });

                    }

                }

                @Override
                public void onFailure(Call<ApplicationBaseResponse> call, Throwable t) {
                    hideProgressDialog();
                    if (t instanceof HttpException) {
                        HttpException exception = (HttpException) t;
                        if (exception.code() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                            ApplicationUtils.showSnackBarRetry(logo,
                                    mContext.getString(R.string.something_went_wrong_please_try_again_later)
                                    , new RetryClickListener() {
                                        @Override
                                        public void onRetryClick() {
                                            callPhoneExits(mobile);
                                        }
                                    });
                        }
                    }


                }
            });

        } else {
            ApplicationUtils.showToastSingle(mContext, logo, getResources().getString(R.string.net_connection));

        }

    }

    private void callSignInMail() {
        if (isNetworkConnected) {
            apiServcies = APIServiceGenerator.getClient(mContext);
            MailExistanceRequest request = new MailExistanceRequest();

            request.setEmail(bundle.getString(ApplicationConstants.email));

            request.setMerchantDeviceId(sessionManager.getUUID());
            Call<UserSignUpResponse> mailExistanceCall;

            mailExistanceCall = apiServcies.API_SIGNIN_MAIL(request);
            mailExistanceCall.enqueue(new Callback<UserSignUpResponse>() {
                @Override
                public void onResponse(Call<UserSignUpResponse> call, Response<UserSignUpResponse> response) {
                    hideProgressDialog();
                    if (response.code() == HttpStatus.SC_OK) {
                        UserSignUpResponse applicationBaseResponse = response.body();
                        if (applicationBaseResponse != null && applicationBaseResponse.isSuccess()) {
                            sessionManager.setUserResponse(applicationBaseResponse);
                            sessionManager.SetLoginSession(true);

                            PreviousLoginRequest previousLoginRequest = new PreviousLoginRequest();
                            previousLoginRequest.setLoginFrom(bundle.getString(ApplicationConstants.loginType));
                            previousLoginRequest.setProfilePic(applicationBaseResponse.getProfileImg());
                            previousLoginRequest.setName(applicationBaseResponse.getLoginName());
                            previousLoginRequest.setEmailAddress(request.getEmail());

                            if (!TextUtils.isEmpty(bundle.getString(ApplicationConstants.mobile))) {
                                previousLoginRequest.setPhoneNumber(bundle.getString(ApplicationConstants.mobile));
                            }
                            sessionManager.setPreviousLoginSession(previousLoginRequest);
                            sessionManager.CheckLogin();


                        } else {
                            hideProgressDialog();
                            if (applicationBaseResponse != null) {
                                ApplicationUtils.showToastSingle(mContext, logo, applicationBaseResponse.getMessage());
                            }
                        }
                    } else {
                        ApplicationUtils.showSnackBarRetry(logo,
                                mContext.getString(R.string.something_went_wrong_please_try_again_later)
                                , new RetryClickListener() {
                                    @Override
                                    public void onRetryClick() {
                                        callSignInMail();
                                    }
                                });

                    }
                }

                @Override
                public void onFailure(Call<UserSignUpResponse> call, Throwable t) {
                    hideProgressDialog();
                    if (t instanceof HttpException) {
                        HttpException exception = (HttpException) t;
                        if (exception.code() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                            ApplicationUtils.showSnackBarRetry(logo,
                                    mContext.getString(R.string.something_went_wrong_please_try_again_later)
                                    , new RetryClickListener() {
                                        @Override
                                        public void onRetryClick() {
                                            callSignInMail();
                                        }
                                    });
                        }
                    }

                }
            });

        } else {
            ApplicationUtils.showToastSingle(mContext, logo, getResources().getString(R.string.net_connection));

        }

    }

    private void callRequestApi() {
        if (isNetworkConnected) {
            String mobileString = bundle.getString(ApplicationConstants.mobile);
            apiServcies = APIServiceGenerator.getClient(mContext);
            PhoneExistanceRequest request = new PhoneExistanceRequest();
            request.setPhoneNo(mobileString);

            request.setMerchantDeviceId(sessionManager.getUUID());
            Call<ApplicationBaseResponse> mailExistanceCall;

            mailExistanceCall = apiServcies.API_REQ_OTP(request);
            mailExistanceCall.enqueue(new Callback<ApplicationBaseResponse>() {
                @Override
                public void onResponse(Call<ApplicationBaseResponse> call, Response<ApplicationBaseResponse> response) {
                    hideProgressDialog();
                    if (response.code() == HttpStatus.SC_OK) {
                        ApplicationBaseResponse applicationBaseResponse = response.body();
                        if (applicationBaseResponse != null && applicationBaseResponse.isSuccess()) {
                            Fragment fragment = new OtpFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString(ApplicationConstants.mobile, mobileString);
                            fragment.setArguments(bundle);
                            mContext.replaceFragment(fragment, true, ApplicationConstants.OTP);

                        } else {
                            hideProgressDialog();
                            if (applicationBaseResponse != null) {
                                ApplicationUtils.showToastSingle(mContext, logo, applicationBaseResponse.getMessage());
                            }
                        }
                    } else {
                        ApplicationUtils.showSnackBarRetry(logo,
                                mContext.getString(R.string.something_went_wrong_please_try_again_later)
                                , new RetryClickListener() {
                                    @Override
                                    public void onRetryClick() {
                                        callRequestApi();
                                    }
                                });

                    }
                }

                @Override
                public void onFailure(Call<ApplicationBaseResponse> call, Throwable t) {
                    hideProgressDialog();
                    if (t instanceof HttpException) {
                        HttpException exception = (HttpException) t;
                        if (exception.code() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                            ApplicationUtils.showSnackBarRetry(logo,
                                    mContext.getString(R.string.something_went_wrong_please_try_again_later)
                                    , new RetryClickListener() {
                                        @Override
                                        public void onRetryClick() {
                                            callRequestApi();
                                        }
                                    });
                        }
                    }
                }
            });

        } else {
            ApplicationUtils.showToastSingle(mContext, logo, getResources().getString(R.string.net_connection));

        }

    }

    private void mobileLogin() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_popup_mobile);

        EditText et_phoneNumber_popup = dialog.findViewById(R.id.et_phoneNumber_popup);

        dialog.findViewById(R.id.btnMobileNumber).setOnClickListener(v1 -> {

            String mobileNumber = et_phoneNumber_popup.getText().toString();
            if (TextUtils.isEmpty(mobileNumber) || mobileNumber.length() != 10) {
                ApplicationUtils.showToastSingle(mContext, logo, getResources().getString(R.string.valid_number));
            } else {
                callPhoneExits(mobileNumber);

                ApplicationUtils.hideKeyboardFrom(mContext, et_phoneNumber_popup);
            }
            dialog.dismiss();

        });
        dialog.show();
    }
}