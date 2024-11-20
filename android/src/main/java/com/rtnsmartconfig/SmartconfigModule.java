package com.rtnsmartconfig;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.SupplicantState;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;

import com.espressif.iot.esptouch.util.TouchNetUtil;
import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchListener;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;

import java.net.InetAddress;
import java.util.List;
import java.lang.Thread;

import org.json.JSONException;
import org.json.JSONObject;
import com.rtnsmartconfig.NativeRTNSmartconfigSpec;

public class SmartconfigModule extends NativeRTNSmartconfigSpec implements PermissionListener {
    private final ReactApplicationContext context;
    public static String NAME = "RTNSmartconfig";
    String TAG = "wifi";
    Promise espPromise;
    Promise checkPromise;

    IEsptouchTask mEsptouchTask;

    private static final int REQUEST_LOCATION = 1503;
    private final String permission = Manifest.permission.ACCESS_FINE_LOCATION;
    AlertDialog alertDialog = null;

    SmartconfigModule(ReactApplicationContext context) {
        super(context);
        this.context = context;
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    public void isConnected() {
        LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (gps || network) {
            checkPromise.resolve("isConnected");
        } else {
            checkPromise.reject("NotConnected");
        }
    }

    public void requestLocationPermission() {
        PermissionAwareActivity activity = getPermissionAwareActivity();
        String messagePermission = "У приложения нет доступа к геопозиции.\n" + "Разрешите доступ к Вашей геопозиции в настройках устройства.";
        DialogInterface.OnClickListener onCancelListener = (dialog, which) -> {
            alertDialog = null;
            checkPromise.reject("NOT_GRANTED");
        };
        DialogInterface.OnClickListener onOkListener = (dialog, which) -> {
            activity.requestPermissions(new String[]{permission}, REQUEST_LOCATION, this);
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
            activity.requestPermissions(new String[]{permission}, REQUEST_LOCATION, this);
        }
    }

    @Override
    public boolean onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isConnected();
            } else {
                checkPromise.reject("NOT_GRANTED");
            }
        }
        return true;
    }

    public void checkLocation(Promise promise) {
        checkPromise = promise;
        Context context = getReactApplicationContext().getBaseContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            } else {
                isConnected();
            }
            return;
        }
        promise.resolve("isConnected");
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

    public void getConnectedInfo(Callback successCallback, Callback errorCallback) {
        final WritableMap result = new WritableNativeMap();

        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        try {
            if (!TouchNetUtil.isWifiConnected(wifiInfo)) {
                result.putString("message", "NotConnected");
                errorCallback.invoke(result);
                return;
            }
            if (!wifiInfo.getSupplicantState().equals(SupplicantState.COMPLETED)) {
                result.putString("message", "Connecting");
                errorCallback.invoke(result);
                return;
            }
            String ssid = TouchNetUtil.getSsidString(wifiInfo);
            InetAddress ip;
            int ipValue = wifiInfo.getIpAddress();
            if (ipValue != 0) {
                ip = TouchNetUtil.getAddress(wifiInfo.getIpAddress());
            } else {
                ip = TouchNetUtil.getIPv4Address();
                if (ip == null) {
                    ip = TouchNetUtil.getIPv6Address();
                }
            }
            String ipAddress = ip.getHostAddress();
            result.putString("ip", ipAddress);
            result.putBoolean("is5G", TouchNetUtil.is5G(wifiInfo.getFrequency()));
            result.putString("ssid", ssid);
            result.putString("bssid", wifiInfo.getBSSID());
            result.putString("state", "Connected");
            successCallback.invoke(result);
        } catch (Exception e) {
            Log.e(TAG, "unexpected JSON exception", e);
        }
    }

    public static byte[] strToByteArray(String str) {
        if (str == null) {
            return null;
        }
        byte[] byteArray = str.getBytes();
        return byteArray;
    }

    private IEsptouchListener myListener = new IEsptouchListener() {
        @Override
        public void onEsptouchResultAdded(final IEsptouchResult result) {
            if (result.isSuc()) {
                espPromise.resolve(result.getBssid());
                Log.e(TAG, "onEsptouchResultAdded");
            }
        }
    };

    public void startEspTouch(String apSsid,
                              String apBssid,
                              String apPassword, Promise promise) {
        final Object mLock = new Object();
        int taskResultCount;
        Activity activity = getCurrentActivity();
        espPromise = promise;

        synchronized (mLock) {
            final byte[] mApSsid = strToByteArray(apSsid);
            final byte[] mApBssid = {0, 0, 0, 0, 0, 0};
            final byte[] mApPassword = strToByteArray(apPassword);
            final byte[] deviceCountData = strToByteArray("1");
            final byte[] broadcastData = strToByteArray("1");
            taskResultCount = deviceCountData.length == 0 ? -1 : Integer.parseInt(new String(deviceCountData));
            mEsptouchTask = new EsptouchTask(mApSsid, mApBssid, mApPassword, context);
            mEsptouchTask.setPackageBroadcast(broadcastData[0] == 1);
            mEsptouchTask.setEsptouchListener(myListener);
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<IEsptouchResult> resultList = mEsptouchTask.executeForResults(taskResultCount);
                IEsptouchResult firstResult = resultList.get(0);
                if (!firstResult.isCancelled()) {
                    if (!firstResult.isSuc()) {
                        espPromise.reject("No Device Found");
                    }
                }
            }
        });
        thread.start();
    }

    public void stopEspTouch(Promise promise) {
        if (mEsptouchTask != null) {
                    mEsptouchTask.interrupt();
        }
        promise.resolve("OK");
    }
}
