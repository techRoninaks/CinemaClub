package com.create.sidhu.movbox.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Request required permissions for the application
 */

public class PermissionsHelper{
    //System
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    public static final int REQUEST_READ_EXTERNAL_STORAGE = 3;
    public static final int REQUEST_INTERNET_PERMISSION = 5;
    public static final int REQUEST_ACCESS_NETWORK_STATE = 6;
    public static final int REQUEST_ACCESS_FINE_LOCATION = 7;
    public static final int REQUEST_ACCESS_COARSE_LOCATION = 8;
    //Google
    public static final int REQUEST_GOOGLE_AUTHENTICATION = 4;
    //Facebook
    public static final int REQUEST_FACEBOOK_AUTHENTICATION = 64206;
    public static final String FACEBOOK_EMAIL = "email";
    public static final String FACEBOOK_USERNAME = "public_profile";

    private Context context;

    /***
     * Public Constructor
     * @param context- Current context variable. <br/>Context variable is required for various permissions
     */
    public PermissionsHelper(Context context){
        this.context = context;
    }

    public boolean requestPermissions(int requestCode){
        switch (requestCode){
            case REQUEST_IMAGE_CAPTURE:{
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                            Manifest.permission.CAMERA)) {
                    } else {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{Manifest.permission.CAMERA},
                                REQUEST_IMAGE_CAPTURE);
                    }
                    return false;
                }else
                    return true;
            }
            case REQUEST_WRITE_EXTERNAL_STORAGE:{
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    } else {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                    return false;
                }else
                    return true;
            }
            case REQUEST_READ_EXTERNAL_STORAGE:{
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    } else {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_READ_EXTERNAL_STORAGE);
                    }
                    return false;
                }else
                    return true;
            }
            case REQUEST_ACCESS_NETWORK_STATE:{
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                            Manifest.permission.ACCESS_NETWORK_STATE)) {
                    } else {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                                REQUEST_ACCESS_NETWORK_STATE);
                    }
                    return false;
                }else
                    return true;
            }
            case REQUEST_INTERNET_PERMISSION:{
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                            Manifest.permission.INTERNET)) {
                    } else {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{Manifest.permission.INTERNET},
                                REQUEST_INTERNET_PERMISSION);
                    }
                    return false;
                }else
                    return true;
            }
            case REQUEST_ACCESS_COARSE_LOCATION:{
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                            Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    } else {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                REQUEST_ACCESS_COARSE_LOCATION);
                    }
                    return false;
                }else
                    return true;
            }
            case REQUEST_ACCESS_FINE_LOCATION:{
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                    } else {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_ACCESS_FINE_LOCATION);
                    }
                    return false;
                }else
                    return true;
            }
        }
        return false;
    }


}
