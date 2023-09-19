package com.example.mdpgroup35.RpiHelper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import androidx.core.app.ActivityCompat;

import java.util.List;

public class WifiUtils {//utility class for handling Wi-Fi connections in an Android application
    private String networkSSID = "rpisquare"; //may need to change this
    private String networkPass = "grp32grp32"; //may need to change this
    private WifiConfiguration conf;

    private static WifiUtils INSTANCE;

    public static WifiUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WifiUtils();
        }

        return INSTANCE;
    }

    private WifiUtils() { //The connect method is used to establish a connection to the specified Wi-Fi network.
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
        wifiManager.setWifiEnabled(true); //It ensures that Wi-Fi is enabled (wifiManager.setWifiEnabled(true)) if it's not already enabled.
        wifiManager.addNetwork(conf);//It adds the WifiConfiguration object (conf) to the list of configured networks using wifiManager.addNetwork(conf)
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {//t searches for the network with the SSID matching the target network (networkSSID). If found, it disconnects from the current network (if any), enables the target network, and initiates a reconnection.
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
    }
}
