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
public class MailExistanceRequest {
    private String email;
    private String merchantDeviceId;

    public String getMerchantDeviceId() {
        return merchantDeviceId;
    }

    public void setMerchantDeviceId(String merchantDeviceId) {
        this.merchantDeviceId = merchantDeviceId;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
