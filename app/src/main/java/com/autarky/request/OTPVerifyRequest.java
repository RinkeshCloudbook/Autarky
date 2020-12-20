package com.autarky.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Vishal Bhanot on 26-08-2020,August,2020
 * Company: Autarky Services
 * Support E-mail: vishu.bhanot18@gmail.com
 * Web: https://theautarky.com
 * Phone: +91-78769-22777
 * Skype: vishu1602
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class OTPVerifyRequest {
    private String phoneNo;
    private String OTP;
    private String merchantDeviceId;

    public String getOTP() {
        return OTP;
    }

    public void setOTP(String OTP) {
        this.OTP = OTP;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getMerchantDeviceId() {
        return merchantDeviceId;
    }

    public void setMerchantDeviceId(String merchantDeviceId) {
        this.merchantDeviceId = merchantDeviceId;
    }


}
