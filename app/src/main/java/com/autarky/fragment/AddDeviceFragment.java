package com.autarky.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.autarky.R;
import com.autarky.adapter.AddDeviceListAdapter;
import com.autarky.constant.ApplicationConstants;
import com.autarky.network.APIServiceGenerator;
import com.autarky.request.DeviceInfoRequest;
import com.autarky.response.UserCheckDeviceResponse;
import com.autarky.services.RefreshTokenClass;
import com.autarky.utils.ApplicationUtils;
import com.autarky.utils.BaseFragment;
import com.autarky.utils.OnClickDevice;
import com.autarky.utils.RetryClickListener;
import com.autarky.utils.SessionManager;
import com.autarky.wifi.OnWifiRefresh;
import com.autarky.wifi.WifiMethods;
import com.autarky.wifi.WifiReceiver;

import org.apache.commons.httpclient.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

import static com.autarky.constant.ApplicationConstants.PERMISSIONS_REQUEST;
import static com.autarky.utils.ApplicationUtils.hideProgressDialog;
import static com.autarky.utils.ApplicationUtils.showProgressDialog;
import static com.autarky.variables.AppGolabalVariables.isNetworkConnected;
import static com.facebook.FacebookSdk.getApplicationContext;


public class AddDeviceFragment extends BaseFragment implements OnClickDevice {

    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    WifiReceiver receiverWifi;
    RecyclerView rvWifiList;
    TextView tvNotFound;
    private WifiManager wifiManager;

    int clickConnect = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_device, container, false);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
//            Toast.makeText(getApplicationContext(), "Turning WiFi ON...", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        TextView tvToolbarTitle = view.findViewById(R.id.tvToolbarTitle);
        tvToolbarTitle.setText(getResources().getString(R.string.add_device));
        view.findViewById(R.id.ivBackPress).setOnClickListener(view1 -> mContext.onBackPressed());
//
        tvNotFound = view.findViewById(R.id.tvNotFound);
        tvNotFound.setVisibility(View.GONE);
        rvWifiList = view.findViewById(R.id.rvWifiList);
        if (rvWifiList.getLayoutManager() == null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()
                    , LinearLayoutManager.VERTICAL, false);
            rvWifiList.setLayoutManager(layoutManager);
            rvWifiList.setItemAnimator(new DefaultItemAnimator());
//            rvWifiList.addItemDecoration(new SimpleDividerItemDecoration(mContext));
        }
//
//        if (getArguments() != null && getArguments().getBoolean(ApplicationConstants.backFromWeb)) {
//
//        }
        return view;
    }

    private void getWifi() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext, Manifest.permission.CHANGE_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            requestPermissions(new String[]{Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST);

        } else {
//            Toast.makeText(mContext, "location turned on", Toast.LENGTH_SHORT).show();
            showProgressDialog(mContext);
            wifiManager.startScan();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mContext.unregisterReceiver(receiverWifi);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_ACCESS_COARSE_LOCATION) {
            try {

                if (ActivityCompat.shouldShowRequestPermissionRationale(mContext, permissions[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(mContext, permissions[1])
                        || ActivityCompat.shouldShowRequestPermissionRationale(mContext, permissions[2])
                        || ActivityCompat.shouldShowRequestPermissionRationale(mContext, permissions[3])) {

                } else if (ActivityCompat.checkSelfPermission(mContext, permissions[0]) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(mContext, permissions[1]) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(mContext, permissions[2]) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(mContext, permissions[3]) == PackageManager.PERMISSION_GRANTED) {
                    showProgressDialog(mContext);
                    wifiManager.getScanResults();
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }


    }

    @Override
    public void onResume() {
        super.onResume();
        receiverWifi = new WifiReceiver(wifiManager, new OnWifiRefresh() {
            @Override
            public void onWifiListGet(List<String> scanResultList) {
                AddDeviceListAdapter arrayAdapter = new AddDeviceListAdapter(scanResultList, mContext, AddDeviceFragment.this);
                rvWifiList.setAdapter(arrayAdapter);
                hideProgressDialog();
            }
        });
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            mContext.registerReceiver(receiverWifi, intentFilter);
            getWifi();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClickDevice(String deviceName) {
//        showProgressDialog(mContext);
//       if (clickConnect == 1) {
//            clickConnect = 2;
//            WifiMethods.WifiConnect("Avisha@Chiku"
//                    , deviceName, wifiManager);
        callDeviceInfo(/*deviceName*/"ATH-LFM-LAB78B9ILA");
//        } else {
//            clickConnect = 1;
//        }
    }


    public void callDeviceInfo(String deviceName) {
        if (isNetworkConnected) {
            showProgressDialog(mContext);
            SessionManager sessionManager = new SessionManager(mContext);
            apiServcies = APIServiceGenerator.getClient(mContext);
            DeviceInfoRequest request = new DeviceInfoRequest();
            request.setMerchantDeviceId(sessionManager.getUUID());
            request.setMerchantId(sessionManager.getUserResponse().getMerchantId());
            request.setDeviceName(deviceName);


            Call<UserCheckDeviceResponse> applicationBaseResponseCall;
            applicationBaseResponseCall = apiServcies.API_DEVINFO(request);
            applicationBaseResponseCall.enqueue(new Callback<UserCheckDeviceResponse>() {
                @Override
                public void onResponse(@NotNull Call<UserCheckDeviceResponse> call
                        , @NotNull Response<UserCheckDeviceResponse> response) {
                    if (response.code() == HttpStatus.SC_OK) {
                        UserCheckDeviceResponse userCheckDeviceResponse = response.body();
                        if (userCheckDeviceResponse != null && userCheckDeviceResponse.isSuccess()) {
                            if (userCheckDeviceResponse.isAllowed()) {
                                if (WifiMethods.WifiConnect(userCheckDeviceResponse.getDevicePSK()
                                        , userCheckDeviceResponse.getDeviceSSID(), wifiManager)) {
                                    getUrlCheck(userCheckDeviceResponse.getDeviceURL(), userCheckDeviceResponse);

                                } else {
                                    hideProgressDialog();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
                                    builder.setMessage("Check your device is on or not")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", (dialog, id) -> {
                                                //do things
                                                dialog.dismiss();

                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            } else {
                                hideProgressDialog();
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
                                builder.setMessage("getting not allowed from response.")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", (dialog, id) -> {
                                            //do things
                                            dialog.dismiss();

                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<UserCheckDeviceResponse> call, @NotNull Throwable t) {
                    hideProgressDialog();
                    if (t instanceof HttpException) {
                        HttpException exception = (HttpException) t;
                        if (exception.code() == HttpStatus.SC_UNAUTHORIZED) {
                            RefreshTokenClass.callRefreshToken(() -> callDeviceInfo(deviceName), mContext);
                        } else if (exception.code() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                            ApplicationUtils.showSnackBarRetry(rvWifiList,
                                    mContext.getString(R.string.something_went_wrong_please_try_again_later)
                                    , new RetryClickListener() {
                                        @Override
                                        public void onRetryClick() {
                                            callDeviceInfo(deviceName);
                                        }
                                    });
                        }
                    }
                }
            });

        } else {
            ApplicationUtils.showToastSingle(mContext, rvWifiList, getResources().getString(R.string.net_connection));

        }

    }

    public void getUrlCheck(String urlString, UserCheckDeviceResponse userCheckDeviceResponse) {
        if (isNetworkConnected) {
            apiServcies = APIServiceGenerator.getClient(mContext);
            Call<String> applicationBaseResponseCall;
            if (apiServcies != null) {
                applicationBaseResponseCall = apiServcies.API_URL_CHECKING(urlString);
                applicationBaseResponseCall.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NotNull Call<String> call
                            , @NotNull Response<String> response) {
                        hideProgressDialog();
                        if (response.code() == HttpStatus.SC_OK) {
                            Fragment fragment = new WebViewFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(ApplicationConstants.UserCheckDeviceResponse, userCheckDeviceResponse);
                            fragment.setArguments(bundle);
                            mContext.replaceFragment(fragment, true, ApplicationConstants.WebView);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
                            builder.setMessage("getting " + response.code() + " from url.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //do things
                                            sessionManager.CheckLogin();

                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                        hideProgressDialog();
                    }
                });
            }


        } else {
            ApplicationUtils.showToastSingle(mContext, rvWifiList, getResources().getString(R.string.net_connection));

        }

    }
}