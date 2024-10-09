package com.rtnsmartconfig;

import android.Manifest;
import android.os.Build;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.util.Log;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import java.util.Map;
import java.util.HashMap;
import com.rtnsmartconfig.NativeRTNSmartconfigSpec;

public class SmartconfigModule extends NativeRTNSmartconfigSpec {

    public static String NAME = "RTNSmartconfig";
    final private ReactApplicationContext reactContext;

    SmartconfigModule(ReactApplicationContext context) {
        super(context);
        this.reactContext = context;
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

    @Override
    public void checkLocation() {
        String TAG = "wifi";
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this.reactContext, permission)
                    != PackageManager.PERMISSION_GRANTED ) {
                 Log.e(TAG, "requestPermissions");
            } else {
                 Log.e(TAG, "ELSErequestPermissions");
            }
            return;
        }
    }
}
