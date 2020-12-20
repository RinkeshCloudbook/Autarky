package com.autarky.network;

import android.text.TextUtils;

import com.autarky.activities.MainScreenActivity;
import com.autarky.constant.ApiConstants;
import com.autarky.constant.ApplicationConstants;
import com.autarky.services.UserServices;
import com.autarky.utils.SessionManager;

import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Vishal Bhanot on 25-08-2020,August,2020
 * Company: Autarky Services
 * Support E-mail: vishu.bhanot18@gmail.com
 * Web: https://theautarky.com
 * Phone: +91-78769-22777
 * Skype: vishu1602
 */


public class APIServiceGenerator {


    public static UserServices getClient(MainScreenActivity mContext) {

        try {
            SessionManager sessionManager = new SessionManager(mContext);


            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            Dispatcher dispatcher = new Dispatcher();
            dispatcher.setMaxRequests(1);

//            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
//                    .connectTimeout(1, TimeUnit.MINUTES)
//                    .readTimeout(30, TimeUnit.SECONDS)
//                    .writeTimeout(15, TimeUnit.SECONDS)
//                    .addInterceptor(interceptor)
//                    .retryOnConnectionFailure(false)
//                    .build();


            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(chain -> {
                Request request;

                if (sessionManager.islogin() && sessionManager.getUserResponse() != null
                        && !TextUtils.isEmpty(sessionManager.getUserResponse()
                        .getAuthType()) && !TextUtils.isEmpty(sessionManager.getUserResponse()
                        .getAccessToken())) {
                    request = chain.request().newBuilder()
                            .addHeader("User-Agent", ApplicationConstants.COMPANY_NAME)
                            .addHeader("Accept", "application/json")
                            .addHeader("Authorization", sessionManager.getUserResponse()
                                    .getAuthType() + " " + sessionManager.getUserResponse()
                                    .getAccessToken()).build();
                } else {
                    request = chain.request().newBuilder()
                            .addHeader("User-Agent", ApplicationConstants.COMPANY_NAME)
                            .addHeader("Accept", "application/json").build();
                }

                return chain.proceed(request);
            });

            // Timeouts for API'S
            httpClient.connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS);

            httpClient.addInterceptor(interceptor);
            OkHttpClient okHttpClient = httpClient.build();


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.APP_BASE_URL)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
            return retrofit.create(UserServices.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
