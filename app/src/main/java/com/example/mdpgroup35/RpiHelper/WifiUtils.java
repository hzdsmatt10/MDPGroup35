package com.example.mdpgroup35.RpiHelper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import androidx.core.app.ActivityCompat;

import java.util.List;

public class WifiUtils {
    private String networkSSID = "rpisquare";
    private String networkPass = "grp32grp32";
    private WifiConfiguration conf;

    private static WifiUtils INSTANCE;

    public static WifiUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WifiUtils();
        }

        return INSTANCE;
    }

    private WifiUtils() {
        conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
        conf.wepKeys[0] = "\"" + networkPass + "\"";
        // wpa
        conf.preSharedKey = "\"" + networkPass + "\"";
        conf.wepTxKeyIndex = 0;
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
    }

    public void connect(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // Turn on wifi
        wifiManager.setWifiEnabled(true);
        wifiManager.addNetwork(conf);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
    }
}
