package com.autarky.request;

import java.io.Serializable;

/**
 * Created by Vishal Bhanot on 25-08-2020,August,2020
 * Company: Autarky Services
 * Support E-mail: vishu.bhanot18@gmail.com
 * Web: https://theautarky.com
 * Phone: +91-78769-22777
 * Skype: vishu1602
 */


public class UserSignUpRequest implements Serializable {
    private String firstName;
    private String lastName;
    private String merchantDeviceId;
    private String profileImg;
    private String email;
    private String phoneNo;
    private String audience;


    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMerchantDeviceId() {
        return merchantDeviceId;
    }

    public void setMerchantDeviceId(String merchantDeviceId) {
        this.merchantDeviceId = merchantDeviceId;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }
}
