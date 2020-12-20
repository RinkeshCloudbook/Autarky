package com.autarky.constant;

public class ApiConstants {

    public final static String APP_BASE_URL = "https://44b9be93824a.ngrok.io";

    // Prefix  API'S for user Authorization and Authentication
    public static final String API_AUTH = "/apps/homeauto/apis/auth";

    // API'S for user Services after Authorization and Authentication
    // These API must pass valid auth token in headers
    public static final String API_USER = "/apps/homeauto/apis/user";

    public static final String API_MAIL_EXISTS = API_AUTH + "/mailexist";
    public static final String API_PHONE_EXISTS = API_AUTH + "/phonexist";
    public static final String API_SIGNIN_MAIL = API_AUTH + "/signingm";
    public static final String API_SIGNIN_PHONE = API_AUTH + "/signinph";
    public static final String API_REFRESH_AUTH_TOKEN = API_AUTH + "/refreshtoken";
    public static final String API_REQ_OTP = API_AUTH + "/requestotp";
    public static final String API_RESESND_OTP = API_AUTH + "/resendotp";
    public static final String API_VERIFY_OTP = API_AUTH + "/verifytotp";
    public static final String API_SIGNUP = API_AUTH + "/signup";

    public static final String API_DEVINFO = API_USER + "/devinfo";
    public static final String API_DEVICE_ADD = API_USER + "/deviceadd";


}
