package com.autarky.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

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


public class WifiReceiver extends BroadcastReceiver {
    WifiManager wifiManager;
    StringBuilder sb;
    OnWifiRefresh mOnWifiRefresh;

    public WifiReceiver(WifiManager wifiManager, OnWifiRefresh onWifiRefresh) {
        this.wifiManager = wifiManager;
        this.mOnWifiRefresh = onWifiRefresh;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            sb = new StringBuilder();
            List<ScanResult> wifiList = wifiManager.getScanResults();
            List<String> deviceList = new ArrayList<>();
            wifiList.forEach(Cosnumer -> {
//                    if (Cosnumer.SSID.contains("ATH-LFM")) {
                deviceList.add(Cosnumer.SSID/* + " - " + scanResult.capabilities*/);
//                    }
            });

            if (mOnWifiRefresh != null) {
                mOnWifiRefresh.onWifiListGet(deviceList);
            }

        }
    }
}
