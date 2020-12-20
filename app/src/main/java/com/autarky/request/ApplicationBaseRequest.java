package com.autarky.request;

/**
 * Created by Vishal Bhanot on 26-08-2020,August,2020
 * Company: Autarky Services
 * Support E-mail: vishu.bhanot18@gmail.com
 * Web: https://theautarky.com
 * Phone: +91-78769-22777
 * Skype: vishu1602
 */

public class ApplicationBaseRequest {
    private String merchantId;
    private String merchantDeviceId;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantDeviceId() {
        return merchantDeviceId;
    }

    public void setMerchantDeviceId(String merchantDeviceId) {
        this.merchantDeviceId = merchantDeviceId;
    }
}
