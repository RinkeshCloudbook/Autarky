package com.autarky.fragment;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.autarky.R;
import com.autarky.constant.ApplicationConstants;
import com.autarky.network.APIServiceGenerator;
import com.autarky.request.DeviceAddRequest;
import com.autarky.response.ApplicationBaseResponse;
import com.autarky.response.UserCheckDeviceResponse;
import com.autarky.services.RefreshTokenClass;
import com.autarky.utils.ApplicationUtils;
import com.autarky.utils.BaseFragment;
import com.autarky.utils.RetryClickListener;
import com.autarky.utils.SessionManager;

import org.apache.commons.httpclient.HttpStatus;
import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

import static com.autarky.utils.ApplicationUtils.hideProgressDialog;
import static com.autarky.utils.ApplicationUtils.showProgressDialog;
import static com.autarky.variables.AppGolabalVariables.isNetworkConnected;


public class WebViewFragment extends BaseFragment {


    WebView wvWebView;
    UserCheckDeviceResponse userCheckDeviceResponse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);
        wvWebView = view.findViewById(R.id.wvWebView);

        if (getArguments() != null) {
            userCheckDeviceResponse = (UserCheckDeviceResponse) getArguments().getSerializable(ApplicationConstants.UserCheckDeviceResponse);
            WebSettings webSettings = wvWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            wvWebView.loadUrl(userCheckDeviceResponse.getDeviceURL());
        }



     /*try {
            TimeUnit.MINUTES.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        showProgressDialog(mContext);
        wvWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO show you progress image
                super.onPageStarted(view, url, favicon);
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO hide your progress image
                hideProgressDialog();
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                super.shouldOverrideUrlLoading(view, request);
                String url = request.getUrl().toString();
                System.out.println(request.getMethod() + " uri : " + request.getUrl().toString());
                if (url.contains("Success")) {

                } else {

                }
                return true;

            }
        });
        return view;
    }

    public void callDeviceAdd() {
        if (isNetworkConnected) {
            showProgressDialog(mContext);
            SessionManager sessionManager = new SessionManager(mContext);
            apiServcies = APIServiceGenerator.getClient(mContext);
            DeviceAddRequest request = new DeviceAddRequest();
            request.setMerchantDeviceId(sessionManager.getUUID());
            request.setMerchantId(sessionManager.getUserResponse().getMerchantId());
            request.setSerialNo(userCheckDeviceResponse.getSerialNo());


            Call<ApplicationBaseResponse> applicationBaseResponseCall;
            applicationBaseResponseCall = apiServcies.API_DEVICE_ADD(request);
            applicationBaseResponseCall.enqueue(new Callback<ApplicationBaseResponse>() {
                @Override
                public void onResponse(@NotNull Call<ApplicationBaseResponse> call
                        , @NotNull Response<ApplicationBaseResponse> response) {
                    if (response.code() == HttpStatus.SC_OK) {
                        ApplicationBaseResponse userCheckDeviceResponse = response.body();
                        if (userCheckDeviceResponse != null && userCheckDeviceResponse.isSuccess()) {

                            hideProgressDialog();
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
                            builder.setMessage("Your device add successfully")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", (dialog, id) -> {
                                        mContext.onBackPressed();
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ApplicationBaseResponse> call, @NotNull Throwable t) {
                    hideProgressDialog();
                    if (t instanceof HttpException) {
                        HttpException exception = (HttpException) t;
                        if (exception.code() == HttpStatus.SC_UNAUTHORIZED) {
                            RefreshTokenClass.callRefreshToken(() -> callDeviceAdd(), mContext);
                        } else if (exception.code() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                            ApplicationUtils.showSnackBarRetry(wvWebView,
                                    mContext.getString(R.string.something_went_wrong_please_try_again_later)
                                    , new RetryClickListener() {
                                        @Override
                                        public void onRetryClick() {
                                            callDeviceAdd();
                                        }
                                    });
                        }
                    }
                }
            });

        }

    }

}