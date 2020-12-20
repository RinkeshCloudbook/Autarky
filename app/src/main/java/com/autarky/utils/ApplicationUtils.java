package com.autarky.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.autarky.R;
import com.autarky.activities.MainScreenActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Vishal Bhanot on 30-07-2020,July,2020
 * Company: Autarky Services
 * Support E-mail: vishu.bhanot18@gmail.com
 * Web: https://theautarky.com
 * Phone: +91-78769-22777
 * Skype: vishu1602
 */


public class ApplicationUtils {
    private static ApplicationUtils applicationUtilsInstance = null;
    private static final String TAG = NetworkService.class.getSimpleName();


    private ApplicationUtils() {

    }

    public static ApplicationUtils getApplicationUtilsInstance() {
        if (applicationUtilsInstance == null) {
            applicationUtilsInstance = new ApplicationUtils();
        }
        return applicationUtilsInstance;
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Show single toast when multiple click
     *
     * @param context activity
     * @param text    message
     */
    @SuppressLint("ShowToast")
    public static void showToastSingle(Context context, View view, String text) {
        Snackbar sb = Snackbar.make(view, text, BaseTransientBottomBar.LENGTH_LONG);
        sb.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.color_232d36));
        sb.show();
    }


    public static void showSnackBarRetry(View view, String message, RetryClickListener retryClickListener) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (retryClickListener != null) {
                            retryClickListener.onRetryClick();
                        }
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
       /* TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);*/
        snackbar.show();

    }

    public static void showSnackBar(View cl, int color, String message, Context context) {
        Snackbar sb = Snackbar.make(cl, message, BaseTransientBottomBar.LENGTH_LONG);
        sb.getView().setBackgroundColor(ContextCompat.getColor(context, color));
        sb.show();
    }

    public String getAuthToken() {

        return "test";
    }

    @SuppressLint("HardwareIds")
    public static String deviceId() {
        /*final TelephonyManager tm = (TelephonyManager) mContext.getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = tm.getDeviceId();
        tmSerial = tm.getSimSerialNumber();
        androidId = android.provider.Settings.Secure.getString(mContext.getContentResolver()
                , android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
      */
        return UUID.randomUUID().toString();

    }

    //
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String base64PasswordEconder(String passwordToEncode) {
        return Base64.getEncoder().withoutPadding().encodeToString(passwordToEncode.getBytes());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String base64passwordDecoder(String passwordToDecode) {
        byte[] decodedBytes = Base64.getDecoder().decode(passwordToDecode);
        return new String(decodedBytes);

        /*byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);*/
    }

    /**
     * Validate email with regular expression
     *
     * @param email password for validation
     * @return true valid email, false invalid email
     */
    public static boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return !matcher.matches();
    }

    public String getMerchantID() {
        return "merchant-id";
    }

    public String getMerchantDeviceID() {
        return "merchant-device-id";
    }

    public void logToConsole(String TAG, String message) {
        Log.d(TAG, message);
    }

    public static void loadImageViaGlide(Context context, String url, ImageView imageView, int errorDrawable) {
        Log.i("Url", "_________" + url);
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions()
                        .skipMemoryCache(false)
                        .placeholder(errorDrawable)
                        .error(errorDrawable)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                .into(imageView);
    }

    private static CustomProgressDialog progressDialog;

    public static void showProgressDialog(MainScreenActivity mContext) {
        if (progressDialog == null) progressDialog = new CustomProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void hideProgressDialog() {
        if (progressDialog != null) progressDialog.dismiss();
    }


}
