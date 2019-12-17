package com.example.mobdev_project.Helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Arrays;

public class PermissionHelpers {
    public final static int PERMISSION_REQUEST_CODE = 123;

    public static boolean hasRequeredPermissions(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static void requestRequiredPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    public static boolean isPermissionRequestSuccessful(int requestCode, String[] permissions, int[] grantResult) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                // If request is cancelled, result is empty.
                if (grantResult.length > 0) {
                    if (permissionsGranted(grantResult)) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            default:
                return false;
        }
    }

    private static boolean permissionsGranted(int[] grantResult) {
        for (int p : grantResult) if (p != PackageManager.PERMISSION_GRANTED) return false;
        return true;
    }

}
