package com.rtnsmartconfig;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;

import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
import com.facebook.react.ReactActivity;

import java.util.Map;
import java.util.HashMap;
import com.rtnsmartconfig.NativeRTNSmartconfigSpec;

public class SmartconfigModule extends NativeRTNSmartconfigSpec implements PermissionListener{

    public static String NAME = "RTNSmartconfig";
    String TAG = "wifi";
    private final SparseArray<Callback> mCallbacks;

    private PermissionListener permissionListener;
    private static final int REQUEST_LOCATION = 1503;
    private final String permission = Manifest.permission.ACCESS_FINE_LOCATION;
    AlertDialog alertDialog = null;

    SmartconfigModule(ReactApplicationContext context) {
        super(context);
        mCallbacks = new SparseArray<Callback>();
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    @Override
    public void add(double a, double b, Promise promise) {
        promise.resolve(a + b);
    }


    public void requestLocationPermission() {
        Log.e(TAG, "requestLocationPermission");
        PermissionAwareActivity activity = getPermissionAwareActivity();
        String messagePermission = "У приложения нет доступа к геопозиции.\n" + "Разрешите доступ к Вашей геопозиции в настройках устройства.";
        DialogInterface.OnClickListener onCancelListener = (dialog, which) -> {
            Log.e(TAG, "Позже");
            alertDialog = null;};
        DialogInterface.OnClickListener onOkListener = (dialog, which) -> {
            activity.requestPermissions(new String[] {permission}, REQUEST_LOCATION, this);
        };

        if (activity.shouldShowRequestPermissionRationale(permission)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getCurrentActivity())
                    .setMessage(messagePermission)
                    .setPositiveButton("Перейти в настройки", onOkListener)
                    .setNegativeButton("Позже", onCancelListener);

            if (alertDialog == null) {
                alertDialog = builder.show();
            }
        } else {
            activity.requestPermissions(new String[] {permission}, REQUEST_LOCATION, this);
        }
    }

    @Override
    public boolean onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        Log.e("TAG", "onRequestPermissionsResult");
//        if (requestCode == REQUEST_LOCATION) {
//            if (grantResults.length > 0 &&
//                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                isConnected();
//            } else {
//                wifiCallbackContext.error("NOT_GRANTED");
//            }
//        }
//        mCallbacks.get(requestCode).invoke(grantResults, getPermissionAwareActivity());
//        mCallbacks.remove(requestCode);
        return true;
    }


    public void checkLocation() {
        Context context = getReactApplicationContext().getBaseContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED ) {
                 requestLocationPermission();
            } else {
                 Log.e(TAG, "GRANTED");
            }
            return;
        }
    }

    private PermissionAwareActivity getPermissionAwareActivity() {
        Activity activity = getCurrentActivity();
        if (activity == null) {
            throw new IllegalStateException(
                    "Tried to use permissions API while not attached to an " + "Activity.");
        } else if (!(activity instanceof PermissionAwareActivity)) {
            throw new IllegalStateException(
                    "Tried to use permissions API but the host Activity doesn't"
                            + " implement PermissionAwareActivity.");
        }
        return (PermissionAwareActivity) activity;
    }
}
