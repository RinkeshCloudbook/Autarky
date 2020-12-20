package com.autarky.services;

import android.os.Build;

import com.autarky.activities.MainScreenActivity;
import com.autarky.network.APIServiceGenerator;
import com.autarky.request.RefreshTokenRequest;
import com.autarky.response.UserSignUpResponse;
import com.autarky.utils.ApplicationUtils;
import com.autarky.utils.SessionManager;

import org.apache.commons.httpclient.HttpStatus;
import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

import static com.autarky.variables.AppGolabalVariables.isNetworkConnected;

/**
 * Created by Khushboo Jain on 29-09-2020,September,2020
 * Company:
 * Support E-mail: khushboojain942@gmail.com
 * Web:
 * Phone: +91-78799-77162
 * Skype: khushboojain942
 */


public class RefreshTokenClass {
    public static void callRefreshToken(OnSuccessToken onSuccessToken, MainScreenActivity mContext) {
        if (isNetworkConnected) {
            SessionManager sessionManager = new SessionManager(mContext);
            UserServices apiServcies = APIServiceGenerator.getClient(mContext);
            RefreshTokenRequest request = new RefreshTokenRequest();
            request.setMerchantDeviceId(sessionManager.getUUID());
            request.setMerchantId(sessionManager.getUserResponse().getMerchantId());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                request.setOldToken(ApplicationUtils.base64PasswordEconder(sessionManager.getUserResponse().getAccessToken()));
            }

            Call<UserSignUpResponse> applicationBaseResponseCall;
            if (apiServcies != null) {
                applicationBaseResponseCall = apiServcies.API_REFRESH_AUTH_TOKEN(request);
                applicationBaseResponseCall.enqueue(new Callback<UserSignUpResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<UserSignUpResponse> call
                            , @NotNull Response<UserSignUpResponse> response) {
                        if (response.code() == HttpStatus.SC_OK) {
                            UserSignUpResponse userSignUpResponse = response.body();
                            if (userSignUpResponse != null && userSignUpResponse.isSuccess()) {
                                sessionManager.setUserResponse(userSignUpResponse);
                                if (onSuccessToken != null) {
                                    onSuccessToken.onSuccessClick();
                                }

                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<UserSignUpResponse> call, @NotNull Throwable t) {
                        if (t instanceof HttpException) {
                            HttpException exception = (HttpException) t;
                        }
                    }
                });
            }


        }

    }

}
