package com.autarky.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.autarky.R;
import com.autarky.constant.ApplicationConstants;
import com.autarky.network.APIServiceGenerator;
import com.autarky.request.MailExistanceRequest;
import com.autarky.request.PhoneExistanceRequest;
import com.autarky.request.UserSignUpRequest;
import com.autarky.response.ApplicationBaseResponse;
import com.autarky.utils.ApplicationUtils;
import com.autarky.utils.BaseFragment;
import com.autarky.utils.RetryClickListener;
import com.theartofdev.edmodo.cropper.CropImage;

import org.apache.commons.httpclient.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.autarky.constant.ApplicationConstants.PERMISSIONS_REQUEST;
import static com.autarky.constant.ApplicationConstants.PICK_IMAGE_CAMERA;
import static com.autarky.constant.ApplicationConstants.PICK_IMAGE_GALLERY;
import static com.autarky.utils.ApplicationUtils.hideProgressDialog;
import static com.autarky.utils.ApplicationUtils.showProgressDialog;
import static com.autarky.variables.AppGolabalVariables.isNetworkConnected;

public class SignUpFragment extends BaseFragment implements View.OnClickListener {

    CircleImageView ciProfilePicRegistration;
    EditText etFirstNameReg;
    EditText etLastNameReg;
    EditText etEmailReg;
    EditText etMobileReg;
    TextView btnRegister;
    private Uri imageURI;
    private String loginType;
    private String profileImage;

    Bitmap bitmap;

    boolean isFRomEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_sign_up, container, false);

        btnRegister = view.findViewById(R.id.btnRegister);
        etEmailReg = view.findViewById(R.id.etEmailReg);
        etLastNameReg = view.findViewById(R.id.etLastNameReg);
        etMobileReg = view.findViewById(R.id.etMobileReg);
        etFirstNameReg = view.findViewById(R.id.etFirstNameReg);
        ciProfilePicRegistration = view.findViewById(R.id.ciProfilePicRegistration);
        TextView tvToolbarTitle = view.findViewById(R.id.tvToolbarTitle);
        tvToolbarTitle.setText(getResources().getString(R.string.sign_up));
        view.findViewById(R.id.ivBackPress).setOnClickListener(view1 -> mContext.onBackPressed());

        view.findViewById(R.id.rlProfilePic).setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        getBundle();
        return view;
    }

    public void getBundle() {

        if (getArguments() != null) {
            etEmailReg.setText(getArguments().getString(ApplicationConstants.email));
            etFirstNameReg.setText(getArguments().getString(ApplicationConstants.first_name));
            etLastNameReg.setText(getArguments().getString(ApplicationConstants.last_name));
            etMobileReg.setText(getArguments().getString(ApplicationConstants.mobile));
            loginType = getArguments().getString(ApplicationConstants.loginType);

            profileImage = getArguments().getString(ApplicationConstants.profile_pic);

            if (!TextUtils.isEmpty(profileImage)) {
                ApplicationUtils.loadImageViaGlide(mContext, profileImage
                        , ciProfilePicRegistration, R.drawable.ic_user);
            }
            if (TextUtils.isEmpty(getArguments().getString(ApplicationConstants.email))) {
                etMobileReg.setEnabled(false);
                isFRomEmail = true;
            } else {
                etEmailReg.setEnabled(false);
                isFRomEmail = false;

            }
        }


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlProfilePic:
                getPermissionToStorage();
                break;
            case R.id.btnRegister:
                ApplicationUtils.hideKeyboardFrom(mContext, etEmailReg);

                UserSignUpRequest request = new UserSignUpRequest();
                request.setEmail(etEmailReg.getText().toString());
                request.setFirstName(etFirstNameReg.getText().toString());
                request.setLastName(etLastNameReg.getText().toString());
                request.setPhoneNo(etMobileReg.getText().toString());
                request.setMerchantDeviceId(sessionManager.getUUID());
                request.setAudience("App");


                if (TextUtils.isEmpty(request.getFirstName())) {
                    ApplicationUtils.showToastSingle(mContext, view, getResources().getString(R.string.userName_empty));
                } else if (TextUtils.isEmpty(request.getLastName())) {
                    ApplicationUtils.showToastSingle(mContext, view, getResources().getString(R.string.lastName_empty));
                } else if (TextUtils.isEmpty(request.getEmail()) && ApplicationUtils.emailValidator(request.getEmail())) {
                    ApplicationUtils.showToastSingle(mContext, view, getResources().getString(R.string.valid_email));
                } else if (TextUtils.isEmpty(request.getPhoneNo())
                        && request.getPhoneNo().length() != 10) {
                    ApplicationUtils.showToastSingle(mContext, view, getResources().getString(R.string.valid_mobilenumber));

                } else if (bitmap == null && TextUtils.isEmpty(profileImage)) {

                    ApplicationUtils.showToastSingle(mContext, view, "Please select profile pic.");

                } else {
                    showProgressDialog(mContext);

                    if (bitmap != null) {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        profileImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    }

                    request.setProfileImg(profileImage);

                    if (isFRomEmail) {
                        callMailExits(request);

                    } else {
                        callPhoneExits(request);

                    }


                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropImage.ActivityResult result = CropImage.getActivityResult(data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_CAMERA && data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                if (photo != null) {
                    imageURI = getImageUri(mContext, photo);
                }
                Log.e("", "tempUri= " + imageURI);
                try {
                    if (imageURI != null) {
                        CropImage.activity(imageURI)
                                .setAllowFlipping(false)
                                .setMaxCropResultSize(200, 200)
                                .setMinCropResultSize(100, 100)
                                .setCropMenuCropButtonTitle("Save")
                                .start(mContext);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == PICK_IMAGE_GALLERY) {
                try {
                    imageURI = data.getData();
                    if (imageURI != null) {
                        CropImage.activity(imageURI)
                                .setAllowFlipping(false)
                                /* .setMaxCropResultSize(200 ,200)
                                 .setMinCropResultSize(100 ,100)
                                 */.setCropMenuCropButtonTitle("Save")
                                .start(mContext);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                imageURI = result.getUri();
                try {
                    if (imageURI != null) {
                        bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), imageURI);
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                        ciProfilePicRegistration.setImageBitmap(bitmap);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Exception error = result.getError();
            error.printStackTrace();
        }
    }

    private void callPhoneExits(UserSignUpRequest userSignUpRequest) {
        if (isNetworkConnected) {
            apiServcies = APIServiceGenerator.getClient(mContext);
            PhoneExistanceRequest request = new PhoneExistanceRequest();
            request.setPhoneNo(userSignUpRequest.getPhoneNo());
            request.setMerchantDeviceId(sessionManager.getUUID());
            Call<ApplicationBaseResponse> mailExistanceCall = apiServcies.API_PHONE_EXISTS(request);
            mailExistanceCall.enqueue(new Callback<ApplicationBaseResponse>() {
                @Override
                public void onResponse(Call<ApplicationBaseResponse> call, Response<ApplicationBaseResponse> response) {
                    if (response.code() == HttpStatus.SC_OK) {
                        ApplicationBaseResponse applicationBaseResponse = response.body();

                        if (applicationBaseResponse != null && !applicationBaseResponse.isSuccess()) {
                            callRequestApi(userSignUpRequest);
                        } else {
                            hideProgressDialog();
                            if (applicationBaseResponse != null) {
                                etMobileReg.setError(applicationBaseResponse.getMessage());
                                etMobileReg.requestFocus();
//                                ApplicationUtils.showToastSingle(mContext, btnRegister, applicationBaseResponse.getMessage());
                            }

                        }
                    } else {
                        hideProgressDialog();
                        ApplicationUtils.showSnackBarRetry(btnRegister,
                                mContext.getString(R.string.something_went_wrong_please_try_again_later)
                                , new RetryClickListener() {
                                    @Override
                                    public void onRetryClick() {
                                        callPhoneExits(userSignUpRequest);
                                    }
                                });
                    }

                }

                @Override
                public void onFailure(Call<ApplicationBaseResponse> call, Throwable t) {

                    t.printStackTrace();
                    hideProgressDialog();
                    if (t instanceof HttpException) {
                        HttpException exception = (HttpException) t;
                        if (exception.code() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                            ApplicationUtils.showSnackBarRetry(btnRegister,
                                    mContext.getString(R.string.something_went_wrong_please_try_again_later)
                                    , new RetryClickListener() {
                                        @Override
                                        public void onRetryClick() {
                                            callPhoneExits(userSignUpRequest);
                                        }
                                    });
                        }
                    }

                }
            });

        } else {
            ApplicationUtils.showToastSingle(mContext, btnRegister, getResources().getString(R.string.net_connection));
            hideProgressDialog();


        }

    }

    private void callMailExits(UserSignUpRequest userSignUpRequest) {
        if (isNetworkConnected) {
            apiServcies = APIServiceGenerator.getClient(mContext);
            MailExistanceRequest request = new MailExistanceRequest();
            request.setEmail(userSignUpRequest.getEmail());
            request.setMerchantDeviceId(sessionManager.getUUID());
            Call<ApplicationBaseResponse> mailExistanceCall = apiServcies.API_MAIL_EXISTS(request);
            mailExistanceCall.enqueue(new Callback<ApplicationBaseResponse>() {
                @Override
                public void onResponse(Call<ApplicationBaseResponse> call, Response<ApplicationBaseResponse> response) {
                    if (response.code() == HttpStatus.SC_OK) {
                        ApplicationBaseResponse applicationBaseResponse = response.body();
                        if (applicationBaseResponse != null && !applicationBaseResponse.isSuccess()) {
                            callRequestApi(userSignUpRequest);

                        } else {
                            if (applicationBaseResponse != null) {
                                etEmailReg.requestFocus();
                                etEmailReg.setError(applicationBaseResponse.getMessage());
//                                ApplicationUtils.showToastSingle(mContext, btnRegister, applicationBaseResponse.getMessage());
                            }
                        }
                    } else {
                        hideProgressDialog();
                        ApplicationUtils.showSnackBarRetry(btnRegister,
                                mContext.getString(R.string.something_went_wrong_please_try_again_later)
                                , new RetryClickListener() {
                                    @Override
                                    public void onRetryClick() {
                                        callMailExits(userSignUpRequest);
                                    }
                                });
                    }
                }

                @Override
                public void onFailure(Call<ApplicationBaseResponse> call, Throwable t) {
                    t.printStackTrace();
                    hideProgressDialog();
                    if (t instanceof HttpException) {
                        HttpException exception = (HttpException) t;
                        if (exception.code() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                            ApplicationUtils.showSnackBarRetry(btnRegister,
                                    mContext.getString(R.string.something_went_wrong_please_try_again_later)
                                    , new RetryClickListener() {
                                        @Override
                                        public void onRetryClick() {
                                            callMailExits(userSignUpRequest);
                                        }
                                    });
                        }
                    }

                }
            });

        } else {
            hideProgressDialog();
            ApplicationUtils.showToastSingle(mContext, btnRegister, getResources().getString(R.string.net_connection));

        }

    }

    private void callRequestApi(UserSignUpRequest userSignUpRequest) {
        if (isNetworkConnected) {
            apiServcies = APIServiceGenerator.getClient(mContext);
            PhoneExistanceRequest request = new PhoneExistanceRequest();
            request.setPhoneNo(userSignUpRequest.getPhoneNo());

            request.setMerchantDeviceId(sessionManager.getUUID());
            Call<ApplicationBaseResponse> mailExistanceCall;

            mailExistanceCall = apiServcies.API_REQ_OTP(request);
            mailExistanceCall.enqueue(new Callback<ApplicationBaseResponse>() {
                @Override
                public void onResponse(Call<ApplicationBaseResponse> call, Response<ApplicationBaseResponse> response) {
                    hideProgressDialog();
                    if (response.code() == HttpStatus.SC_OK) {
                        ApplicationBaseResponse applicationBaseResponse = response.body();
                        if (applicationBaseResponse != null && applicationBaseResponse.isSuccess()) {
                            Fragment fragment = new OtpFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(ApplicationConstants.loginType, loginType);
                            bundle.putSerializable(ApplicationConstants.UserSignUpRequest, userSignUpRequest);
                            fragment.setArguments(bundle);
                            mContext.replaceFragment(fragment, true, ApplicationConstants.OTP);

                        } else {
                            if (applicationBaseResponse != null) {
                                ApplicationUtils.showToastSingle(mContext, btnRegister, applicationBaseResponse.getMessage());
                            }
                        }
                    } else {
                        ApplicationUtils.showSnackBarRetry(btnRegister,
                                mContext.getString(R.string.something_went_wrong_please_try_again_later)
                                , new RetryClickListener() {
                                    @Override
                                    public void onRetryClick() {
                                        callRequestApi(userSignUpRequest);
                                    }
                                });

                        hideProgressDialog();
                    }

                }

                @Override
                public void onFailure(Call<ApplicationBaseResponse> call, Throwable t) {
                    hideProgressDialog();
                    if (t instanceof HttpException) {
                        HttpException exception = (HttpException) t;
                        if (exception.code() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                            ApplicationUtils.showSnackBarRetry(btnRegister,
                                    mContext.getString(R.string.something_went_wrong_please_try_again_later)
                                    , new RetryClickListener() {
                                        @Override
                                        public void onRetryClick() {
                                            callRequestApi(userSignUpRequest);
                                        }
                                    });
                        }
                    }

                }
            });

        } else {
            ApplicationUtils.showToastSingle(mContext, btnRegister, getResources().getString(R.string.net_connection));
            hideProgressDialog();


        }

    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        try {
            if (requestCode == PERMISSIONS_REQUEST) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(mContext, permissions[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(mContext, permissions[1])
                        || ActivityCompat.shouldShowRequestPermissionRationale(mContext, permissions[2])) {

                } else if (ActivityCompat.checkSelfPermission(mContext, permissions[0]) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(mContext, permissions[1]) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(mContext, permissions[2]) == PackageManager.PERMISSION_GRANTED) {
                    //allowed
                    showPictureDialog();
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);

                }
            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}