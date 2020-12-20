package com.autarky.response;

import java.io.Serializable;

/**
 * Created by Khushboo Jain on 28-09-2020,September,2020
 * Company:
 * Support E-mail: khushboojain942@gmail.com
 * Web:
 * Phone: +91-78799-77162
 * Skype: khushboojain942
 */


public class UserCheckDeviceResponse extends ApplicationBaseResponse implements Serializable {
    private String deviceURL;
    private String deviceSSID;
    private String devicePSK;
    private boolean isAllowed;
    private String serialNo;

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getDeviceURL() {
        return deviceURL;
    }

    public void setDeviceURL(String deviceURL) {
        this.deviceURL = deviceURL;
    }

    public String getDevicePSK() {
        return devicePSK;
    }

    public void setDevicePSK(String devicePSK) {
        this.devicePSK = devicePSK;
    }

    public boolean isAllowed() {
        return isAllowed;
    }

    public void setAllowed(boolean isAllowed) {
        this.isAllowed = isAllowed;
    }

    public String getDeviceSSID() {
        return deviceSSID;
    }

    public void setDeviceSSID(String deviceSSID) {
        this.deviceSSID = deviceSSID;
    }
}
