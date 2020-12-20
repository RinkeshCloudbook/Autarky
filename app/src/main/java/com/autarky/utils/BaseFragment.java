package com.autarky.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.autarky.R;
import com.autarky.activities.MainScreenActivity;
import com.autarky.services.UserServices;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import static com.autarky.constant.ApplicationConstants.PERMISSIONS_REQUEST;
import static com.autarky.constant.ApplicationConstants.PICK_IMAGE_CAMERA;
import static com.autarky.constant.ApplicationConstants.PICK_IMAGE_GALLERY;

public abstract class BaseFragment extends Fragment {

    public MainScreenActivity mContext;
    public SessionManager sessionManager;
    public UserServices apiServcies;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            mContext = (MainScreenActivity) getActivity();
            sessionManager = new SessionManager(mContext);
        }

    }


    public void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(mContext);
        pictureDialog.setTitle(getResources().getString(R.string.select_Action));
        String[] pictureDialogItems = {
                getResources().getString(R.string.select_photo_from_gallery),
                getResources().getString(R.string.capture_photo_from_camera)};
        pictureDialog.setItems(pictureDialogItems,
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            PickPhotoGallery();
                            break;
                        case 1:
                            TakePhoto();
                            break;
                    }
                });
        pictureDialog.show();
    }


    private void TakePhoto() {
        try {
            Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (pictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
                startActivityForResult(pictureIntent,
                        PICK_IMAGE_CAMERA);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("IntentReset")
    private void PickPhotoGallery() {
        try {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");
            startActivityForResult(pickIntent, PICK_IMAGE_GALLERY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void getPermissionToStorage() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {


            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST);

        } else {
            showPictureDialog();

        }
    }



    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }




}
