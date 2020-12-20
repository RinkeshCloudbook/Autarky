package com.autarky.wifi;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Khushboo Jain on 29-09-2020,September,2020
 * Company:
 * Support E-mail: khushboojain942@gmail.com
 * Web:
 * Phone: +91-78799-77162
 * Skype: khushboojain942
 */


public class WifiMethods {
    public void ConnectWifi(String ssid, String password, Context mContext) {
        WifiNetworkSpecifier wifiNetworkSpecifier = new WifiNetworkSpecifier.Builder()
                .setSsid(ssid)
                .setWpa2Passphrase(password)
                .build();
        NetworkRequest networkRequest = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            networkRequest = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .setNetworkSpecifier(wifiNetworkSpecifier)
                    .build();
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.requestNetwork(networkRequest, new ConnectivityManager.NetworkCallback());
    }

    public static void WifiDisConnect(/*String networkSSID,*/ WifiManager wifiManager, Context mContext) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
//                wifiManager.setWifiEnabled(true);
            }
        } else {
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list) {
//                if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.removeNetwork(i.networkId);
                wifiManager.saveConfiguration();
                break;
//                }
            }
        }
    }

    public static boolean WifiConnect(String networkPass, String networkSSID, WifiManager wifiManager) {

//        if (!wifiManager.isWifiEnabled()) {
//            wifiManager.setWifiEnabled(true);
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            WifiNetworkSuggestion networkSuggestion1 =
                    new WifiNetworkSuggestion.Builder()
                            .setSsid(networkSSID)
                            .setWpa2Passphrase(networkPass)
                            .build();

            WifiNetworkSuggestion networkSuggestion2 =
                    new WifiNetworkSuggestion.Builder()
                            .setSsid(networkSSID)
                            .setWpa3Passphrase(networkPass)
                            .build();

            List<WifiNetworkSuggestion> suggestionsList = new ArrayList<>();
            suggestionsList.add(networkSuggestion1);
            suggestionsList.add(networkSuggestion2);

            wifiManager.addNetworkSuggestions(suggestionsList);
            return true;
        } else {
            WifiConfiguration wifiConfiguration = new WifiConfiguration();
            wifiConfiguration.SSID = String.format("\"%s\"", networkSSID);
            wifiConfiguration.preSharedKey = String.format("\"%s\"", networkPass);
            int wifiID = wifiManager.addNetwork(wifiConfiguration);
            wifiManager.enableNetwork(wifiID, true);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState()) == NetworkInfo.DetailedState.CONNECTED) {
                String ssid = wifiInfo.getSSID();

                return ssid.equalsIgnoreCase(networkSSID);
            } else {
                return false;
            }
        }
    }


}
