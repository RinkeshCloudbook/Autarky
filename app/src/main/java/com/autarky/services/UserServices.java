package com.autarky.services;

import com.autarky.constant.ApiConstants;
import com.autarky.request.DeviceAddRequest;
import com.autarky.request.DeviceInfoRequest;
import com.autarky.request.MailExistanceRequest;
import com.autarky.request.OTPVerifyRequest;
import com.autarky.request.PhoneExistanceRequest;
import com.autarky.request.RefreshTokenRequest;
import com.autarky.request.UserSignUpRequest;
import com.autarky.response.ApplicationBaseResponse;
import com.autarky.response.UserCheckDeviceResponse;
import com.autarky.response.UserSignUpResponse;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by Vishal Bhanot on 26-08-2020,August,2020
 * Company: Autarky Services
 * Support E-mail: vishu.bhanot18@gmail.com
 * Web: https://theautarky.com
 * Phone: +91-78769-22777
 * Skype: vishu1602
 */


public interface UserServices {

    // Method for checking Whether email exists or not
    @POST(ApiConstants.API_MAIL_EXISTS)
    Call<ApplicationBaseResponse> API_MAIL_EXISTS(
            @Body @NotNull MailExistanceRequest request
    );

    @POST(ApiConstants.API_PHONE_EXISTS)
    Call<ApplicationBaseResponse> API_PHONE_EXISTS(
            @Body @NotNull PhoneExistanceRequest request
    );

    @POST(ApiConstants.API_SIGNIN_MAIL)
    Call<UserSignUpResponse> API_SIGNIN_MAIL(
            @Body @NotNull MailExistanceRequest request
    );

    @POST(ApiConstants.API_SIGNIN_PHONE)
    Call<UserSignUpResponse> API_SIGNIN_PHONE(
            @Body @NotNull PhoneExistanceRequest request
    );

    @POST(ApiConstants.API_REFRESH_AUTH_TOKEN)
    Call<UserSignUpResponse> API_REFRESH_AUTH_TOKEN(
            @Body @NotNull RefreshTokenRequest request
    );

    @POST(ApiConstants.API_REQ_OTP)
    Call<ApplicationBaseResponse> API_REQ_OTP(
            @Body @NotNull PhoneExistanceRequest request
    );

    @POST(ApiConstants.API_RESESND_OTP)
    Call<ApplicationBaseResponse> API_RESESND_OTP(
            @Body @NotNull PhoneExistanceRequest request
    );

    @POST(ApiConstants.API_VERIFY_OTP)
    Call<ApplicationBaseResponse> API_VERIFY_OTP(
            @Body @NotNull OTPVerifyRequest request
    );

    @POST(ApiConstants.API_SIGNUP)
    Call<UserSignUpResponse> API_SIGNUP(
            @Body @NotNull UserSignUpRequest request
    );

    @POST(ApiConstants.API_DEVINFO)
    Call<UserCheckDeviceResponse> API_DEVINFO(
            @Body @NotNull DeviceInfoRequest request
    );

    @POST(ApiConstants.API_DEVICE_ADD)
    Call<ApplicationBaseResponse> API_DEVICE_ADD(
            @Body @NotNull DeviceAddRequest request
    );

    @POST()
    Call<String> API_URL_CHECKING(
            @Url @NotNull String url
    );
}
