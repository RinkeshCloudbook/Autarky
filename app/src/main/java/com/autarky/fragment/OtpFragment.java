package com.autarky.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.autarky.R;
import com.autarky.constant.ApplicationConstants;
import com.autarky.network.APIServiceGenerator;
import com.autarky.request.OTPVerifyRequest;
import com.autarky.request.PhoneExistanceRequest;
import com.autarky.request.PreviousLoginRequest;
import com.autarky.request.UserSignUpRequest;
import com.autarky.response.ApplicationBaseResponse;
import com.autarky.response.UserSignUpResponse;
import com.autarky.utils.ApplicationUtils;
import com.autarky.utils.BaseFragment;
import com.autarky.utils.RetryClickListener;
import com.autarky.utils.SmsBroadcastReceiver;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.apache.commons.httpclient.HttpStatus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.autarky.utils.ApplicationUtils.hideProgressDialog;
import static com.autarky.utils.ApplicationUtils.showProgressDialog;
import static com.autarky.variables.AppGolabalVariables.isNetworkConnected;

public class OtpFragment extends BaseFragment implements View.OnClickListener {

    EditText etOTP1;
    EditText etOTP2;
    EditText etOTP3;
    EditText etOTP4;
    EditText etOTP5;
    private static final int REQ_USER_CONSENT = 200;
    TextView btnOtpButton;

    String mobileString;
    TextView tvResendOtp;

    UserSignUpRequest userSignUpRequest;
    String loginType;
    SmsBroadcastReceiver smsBroadcastReceiver;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_otp, container, false);
        etOTP1 = view.findViewById(R.id.etOTP1);
        etOTP2 = view.findViewById(R.id.etOTP2);
        etOTP3 = view.findViewById(R.id.etOTP3);
        etOTP4 = view.findViewById(R.id.etOTP4);
        etOTP5 = view.findViewById(R.id.etOTP5);
        btnOtpButton = view.findViewById(R.id.btnOtpButton);
        tvResendOtp = view.findViewById(R.id.tvResendOtp);
        if (getArguments() != null) {
            loginType = getArguments().getString(ApplicationConstants.loginType);
            mobileString = getArguments().getString(ApplicationConstants.mobile);
            userSignUpRequest = (UserSignUpRequest) getArguments().getSerializable(ApplicationConstants.UserSignUpRequest);
        }

        if (userSignUpRequest != null && !TextUtils.isEmpty(userSignUpRequest.getPhoneNo())) {
            mobileString = userSignUpRequest.getPhoneNo();
        }

        etOTP1.addTextChangedListener(new GenericTextWatcher(etOTP1));
        etOTP2.addTextChangedListener(new GenericTextWatcher(etOTP2));
        etOTP3.addTextChangedListener(new GenericTextWatcher(etOTP3));
        etOTP4.addTextChangedListener(new GenericTextWatcher(etOTP4));
        etOTP5.addTextChangedListener(new GenericTextWatcher(etOTP5));

        TextView tvToolbarTitle = view.findViewById(R.id.tvToolbarTitle);
        tvToolbarTitle.setText(getResources().getString(R.string.otp));
        view.findViewById(R.id.ivBackPress).setOnClickListener(view1 -> mContext.onBackPressed());

        btnOtpButton.setOnClickListener(this);

        startSmsUserConsent();

        StartCountDownTimer(30000, 1000);

        return view;
    }

    public void StartCountDownTimer(long totalMilliseconds, long interval) {
        //CountDownTimer(long millisInFuture, long countDownInterval)
        new CountDownTimer(totalMilliseconds, interval) {
            //textview widget to display count down
            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished / 1000) / 60;
                int seconds = (int) ((millisUntilFinished / 1000) % 60);

                tvResendOtp.setText(minutes + " : " + seconds);
                tvResendOtp.setOnClickListener(null);

            }

            public void onFinish() {
                tvResendOtp.setText(getResources().getString(R.string.resend_otp));
                tvResendOtp.setOnClickListener(OtpFragment.this);
            }
        }.start();
    }

    private void getOtpFromMessage(String message) {
        // This will match any 6 digit number in the message
        Pattern pattern = Pattern.compile("(|^)\\d{5}");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            if (!TextUtils.isEmpty(matcher.group(0))) {
                etOTP1.setText(String.valueOf(matcher.group(0).charAt(0)));
                etOTP2.setText(String.valueOf(matcher.group(0).charAt(1)));
                etOTP3.setText(String.valueOf(matcher.group(0).charAt(2)));
                etOTP4.setText(String.valueOf(matcher.group(0).charAt(3)));
                etOTP5.setText(String.valueOf(matcher.group(0).charAt(4)));
                btnOtpButton.callOnClick();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnOtpButton:
                OTPVerifyRequest otpVerifyRequest = new OTPVerifyRequest();
                otpVerifyRequest.setPhoneNo(mobileString);
                otpVerifyRequest.setOTP(etOTP1.getText().toString() +
                        etOTP2.getText().toString() +
                        etOTP3.getText().toString() +
                        etOTP4.getText().toString() +
                        etOTP5.getText().toString());
                otpVerifyRequest.setMerchantDeviceId(sessionManager.getUUID());
                if (TextUtils.isEmpty(otpVerifyRequest.getOTP()) || otpVerifyRequest.getOTP().length() != 5) {
                    ApplicationUtils.showToastSingle(mContext, view, "Please enter valid Otp");
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        otpVerifyRequest.setOTP(ApplicationUtils.base64PasswordEconder(otpVerifyRequest.getOTP()));
                    }
                    callVerifyOtp(otpVerifyRequest);
                }

                break;
            case R.id.tvResendOtp:

                PhoneExistanceRequest request = new PhoneExistanceRequest();
                request.setPhoneNo(mobileString);
                request.setMerchantDeviceId(sessionManager.getUUID());
                callResendOtp(request);
                break;
        }
    }

    private void callVerifyOtp(OTPVerifyRequest request) {
        if (isNetworkConnected) {
            showProgressDialog(mContext);
            apiServcies = APIServiceGenerator.getClient(mContext);
            Call<ApplicationBaseResponse> mailExistanceCall = apiServcies.API_VERIFY_OTP(request);
            mailExistanceCall.enqueue(new Callback<ApplicationBaseResponse>() {
                @Override
                public void onResponse(Call<ApplicationBaseResponse> call, Response<ApplicationBaseResponse> response) {
                    if (response.code() == HttpStatus.SC_OK) {
                        ApplicationBaseResponse applicationBaseResponse = response.body();
                        if (applicationBaseResponse != null) {
                            if (applicationBaseResponse.isSuccess()) {

                                if (userSignUpRequest != null) {
                                    callSignUp(userSignUpRequest);
                                } else {
                                    callSignPhone();
                                }
                            } else {
                                hideProgressDialog();
                                ApplicationUtils.showToastSingle(mContext, btnOtpButton, applicationBaseResponse.getMessage());
                            }
                        }
                    } else {
                        hideProgressDialog();
                        ApplicationUtils.showSnackBarRetry(btnOtpButton,
                                mContext.getString(R.string.something_went_wrong_please_try_again_later)
                                , new RetryClickListener() {
                                    @Override
                                    public void onRetryClick() {
                                        callVerifyOtp(request);
                                    }
                                });

                    }
                }

                @Override
                public void onFailure(Call<ApplicationBaseResponse> call, Throwable t) {
                    hideProgressDialog();
                    t.printStackTrace();
                    ApplicationUtils.showSnackBarRetry(btnOtpButton,
                            mContext.getString(R.string.something_went_wrong_please_try_again_later)
                            , new RetryClickListener() {
                                @Override
                                public void onRetryClick() {
                                    callVerifyOtp(request);
                                }
                            });
                }
            });

        } else {
            ApplicationUtils.showToastSingle(mContext, btnOtpButton, getResources().getString(R.string.net_connection));


        }

    }

    private void callResendOtp(PhoneExistanceRequest request) {
        if (isNetworkConnected) {
            showProgressDialog(mContext);
            apiServcies = APIServiceGenerator.getClient(mContext);
            Call<ApplicationBaseResponse> mailExistanceCall = apiServcies.API_RESESND_OTP(request);
            mailExistanceCall.enqueue(new Callback<ApplicationBaseResponse>() {
                @Override
                public void onResponse(Call<ApplicationBaseResponse> call, Response<ApplicationBaseResponse> response) {
                    hideProgressDialog();
                    if (response.code() == HttpStatus.SC_OK) {
                        ApplicationBaseResponse applicationBaseResponse = response.body();

                        if (applicationBaseResponse != null) {
                            if (applicationBaseResponse.isSuccess()) {
                                StartCountDownTimer(30000, 1000);

                            }
                            ApplicationUtils.showToastSingle(mContext, btnOtpButton, applicationBaseResponse.getMessage());
                        }
                    } else {
                        ApplicationUtils.showSnackBarRetry(btnOtpButton,
                                mContext.getString(R.string.something_went_wrong_please_try_again_later)
                                , new RetryClickListener() {
                                    @Override
                                    public void onRetryClick() {
                                        callResendOtp(request);
                                    }
                                });

                    }

                }

                @Override
                public void onFailure(Call<ApplicationBaseResponse> call, Throwable t) {
                    hideProgressDialog();
                    ApplicationUtils.showSnackBarRetry(btnOtpButton,
                            mContext.getString(R.string.something_went_wrong_please_try_again_later)
                            , new RetryClickListener() {
                                @Override
                                public void onRetryClick() {
                                    callResendOtp(request);
                                }
                            });
                    t.printStackTrace();
                }
            });

        } else {
            ApplicationUtils.showToastSingle(mContext, btnOtpButton, getResources().getString(R.string.net_connection));


        }

    }

    private class GenericTextWatcher implements TextWatcher {

        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch (view.getId()) {
                case R.id.etOTP1:
                    if (text.length() == 1) {
                        etOTP2.requestFocus();
                    }
                    break;
                case R.id.etOTP2:
                    if (text.length() == 1) {

                        etOTP3.requestFocus();
                    } else if (text.length() == 0) {
                        etOTP1.requestFocus();

                    }
                    break;
                case R.id.etOTP3:
                    if (text.length() == 1) {

                        etOTP4.requestFocus();
                    } else if (text.length() == 0) {

                        etOTP2.requestFocus();
                    }
                    break;
                case R.id.etOTP4:
                    if (text.length() == 1) {

                        etOTP5.requestFocus();
                    } else if (text.length() == 0) {

                        etOTP3.requestFocus();
                    }
                    break;
                case R.id.etOTP5:
                    if (text.length() == 1) {
                        ApplicationUtils.hideKeyboardFrom(mContext, etOTP5);
                    } else if (text.length() == 0) {

                        etOTP4.requestFocus();
                    }
                    break;
            }
        }
    }

    private void startSmsUserConsent() {
        SmsRetrieverClient client = SmsRetriever.getClient(mContext);
        //We can add sender phone number or leave it blank
        // I'm adding null here
        client.startSmsUserConsent(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                Toast.makeText(getApplicationContext(), "On Success", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(), "On OnFailure", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registerBroadcastReceiver() {
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.smsBroadcastReceiverListener =
                new SmsBroadcastReceiver.SmsBroadcastReceiverListener() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, REQ_USER_CONSENT);
                    }

                    @Override
                    public void onFailure() {
                    }
                };
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        mContext.registerReceiver(smsBroadcastReceiver, intentFilter);
    }

    @Override
    public void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }

    @Override
    public void onStop() {
        super.onStop();
        mContext.unregisterReceiver(smsBroadcastReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_USER_CONSENT) {
            if ((resultCode == RESULT_OK) && (data != null)) {
                //That gives all message to us.
                // We need to get the code from inside with regex
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//                textViewMessage.setText(
//                        String.format("%s - %s", getString(R.string.received_message), message));
                getOtpFromMessage(message);
            }
        }
    }

    private void callSignUp(UserSignUpRequest request) {
        if (isNetworkConnected) {
            apiServcies = APIServiceGenerator.getClient(mContext);
            Call<UserSignUpResponse> mailExistanceCall = apiServcies.API_SIGNUP(request);
            mailExistanceCall.enqueue(new Callback<UserSignUpResponse>() {
                @Override
                public void onResponse(Call<UserSignUpResponse> call, Response<UserSignUpResponse> response) {
                    UserSignUpResponse applicationBaseResponse = response.body();
                    hideProgressDialog();
                    if (response.code() == HttpStatus.SC_OK) {

                        if (applicationBaseResponse != null) {
                            if (applicationBaseResponse.isSuccess()) {
                                sessionManager.setUserResponse(applicationBaseResponse);
                                sessionManager.SetLoginSession(true);

                                PreviousLoginRequest previousLoginRequest = new PreviousLoginRequest();
                                previousLoginRequest.setLoginFrom(loginType);
                                previousLoginRequest.setProfilePic(applicationBaseResponse.getProfileImg());
                                previousLoginRequest.setName(applicationBaseResponse.getLoginName());
                                previousLoginRequest.setEmailAddress(request.getEmail());

                                previousLoginRequest.setPhoneNumber(request.getPhoneNo());

                                sessionManager.setPreviousLoginSession(previousLoginRequest);

                                sessionManager.CheckLogin();


                            }
                            ApplicationUtils.showToastSingle(mContext, btnOtpButton, applicationBaseResponse.getMessage());
                        }
                    } else {
                        ApplicationUtils.showSnackBarRetry(btnOtpButton,
                                mContext.getString(R.string.something_went_wrong_please_try_again_later)
                                , new RetryClickListener() {
                                    @Override
                                    public void onRetryClick() {
                                        callSignUp(request);
                                    }
                                });
                    }

                }

                @Override
                public void onFailure(Call<UserSignUpResponse> call, Throwable t) {
                    hideProgressDialog();
                    t.printStackTrace();
                    if (t instanceof HttpException) {
                        HttpException exception = (HttpException) t;
                        if (exception.code() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                            ApplicationUtils.showSnackBarRetry(btnOtpButton,
                                    mContext.getString(R.string.something_went_wrong_please_try_again_later)
                                    , new RetryClickListener() {
                                        @Override
                                        public void onRetryClick() {
                                            callSignUp(request);
                                        }
                                    });
                        }
                    }

                }
            });

        } else {
            ApplicationUtils.showToastSingle(mContext, btnOtpButton, getResources().getString(R.string.net_connection));

        }

    }

    private void callSignPhone() {
        if (isNetworkConnected) {
            apiServcies = APIServiceGenerator.getClient(mContext);
            PhoneExistanceRequest request = new PhoneExistanceRequest();
            request.setPhoneNo(mobileString);

            request.setMerchantDeviceId(sessionManager.getUUID());
            Call<UserSignUpResponse> mailExistanceCall;
            mailExistanceCall = apiServcies.API_SIGNIN_PHONE(request);
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
                            previousLoginRequest.setLoginFrom(ApplicationConstants.Mobile);
                            previousLoginRequest.setProfilePic(applicationBaseResponse.getProfileImg());
                            previousLoginRequest.setName(applicationBaseResponse.getLoginName());
                            previousLoginRequest.setEmailAddress("");

                            previousLoginRequest.setPhoneNumber(mobileString);
                            sessionManager.setPreviousLoginSession(previousLoginRequest);

                            sessionManager.CheckLogin();

                        } else {
                            if (applicationBaseResponse != null) {
                                ApplicationUtils.showToastSingle(mContext, btnOtpButton, applicationBaseResponse.getMessage());
                            }
                        }
                    } else {
                        ApplicationUtils.showSnackBarRetry(btnOtpButton,
                                mContext.getString(R.string.something_went_wrong_please_try_again_later)
                                , new RetryClickListener() {
                                    @Override
                                    public void onRetryClick() {
                                        callSignPhone();
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
                            ApplicationUtils.showSnackBarRetry(btnOtpButton,
                                    mContext.getString(R.string.something_went_wrong_please_try_again_later)
                                    , new RetryClickListener() {
                                        @Override
                                        public void onRetryClick() {
                                            callSignPhone();
                                        }
                                    });
                        }
                    }
                }
            });

        } else {
            ApplicationUtils.showToastSingle(mContext, btnOtpButton, getResources().getString(R.string.net_connection));

        }

    }

}