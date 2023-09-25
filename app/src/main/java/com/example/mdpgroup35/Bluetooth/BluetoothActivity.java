package com.example.mdpgroup35.Bluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//Intents are used for various purposes, such as starting activities, services, and broadcasting messages to notify components of events or data changes.
//Intents can be explicit (targeting a specific component within the same app) or implicit (specifying an action that can be handled by one or more components).
import android.content.IntentFilter;
//    An IntentFilter is used to declare what types of intents a component can respond to. It specifies the conditions (e.g., action, category, data, MIME type) under which a component should be activated.
//    An IntentFilter is often used in the manifest file of an Android app to specify which intents the app's components (e.g., activities, services, broadcast receivers) can handle.
//    When an intent is sent or broadcast, Android's system matches the intent's attributes with the intent filters of registered components to determine which component should respond to the intent.
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mdpgroup35.R;

import java.util.Set;
/*
Context class is a fundamental and crucial component of the application framework. It represents the current state of the application and provides access to various application-specific resources and services. Essentially, it serves as a gateway to the Android system and allows an Android application to interact with its environment.

 */





public class BluetoothActivity extends AppCompatActivity {
    private ListView listPairedDevices, listAvailableDevices;
    private ProgressBar progressScanDevices;

    private ArrayAdapter<String> adapterPairedDevices, adapterAvailableDevices; // These ArrayAdapter objects are used to populate the ListView widgets with Bluetooth device information.
    private Context context;// It stores a reference to the current Context, which is typically the activity itself.
    private BluetoothAdapter bluetoothAdapter;
    private static final String TAG = "DeviceListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) { //This method is called when the activity is first created. It initializes the activity's layout and calls the init() method to set up Bluetooth-related functionality.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        context = this;

        init();
    }

    private void init() {
        showLog("Entering init");
        listPairedDevices = findViewById(R.id.list_paired_devices);
        listAvailableDevices = findViewById(R.id.list_available_devices);
        progressScanDevices = findViewById(R.id.progress_scan_devices);

        adapterPairedDevices = new ArrayAdapter<String>(context, R.layout.device_list_item);//The adapters are initialized with a reference to the activity's context (context) and a custom layout resource
        adapterAvailableDevices = new ArrayAdapter<String>(context, R.layout.device_list_item);

        listPairedDevices.setAdapter(adapterPairedDevices); //These lines set the adapters for the ListView widgets, which means that data will be displayed in these lists using the adapters.
        listAvailableDevices.setAdapter(adapterAvailableDevices);

        listAvailableDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17); //The last 17 characters of info (presumably a Bluetooth device address) are extracted and stored in address.

                Intent intent = new Intent();
                intent.putExtra("deviceAddress", address);
                setResult(RESULT_OK, intent); //indicates success
                finish();
            }
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); //This line initializes the bluetoothAdapter with the default Bluetooth adapter for the device. It's used for various Bluetooth operations.
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices(); //This retrieves a set of paired Bluetooth devices. These are devices that have been previously connected and paired with the Android device.
        if (pairedDevices != null && pairedDevices.size() > 0) { //This checks if there are any paired devices. If there are, it iterates through the set of paired devices and adds their names and addresses to adapterPairedDevices. This populates the list of paired devices.
            for (BluetoothDevice device : pairedDevices) {
                adapterPairedDevices.add(device.getName() + "\n" + device.getAddress());
            }
        }

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND); //created to specify which Bluetooth-related events the activity is interested in. These filters are used to register a BroadcastReceiver (bluetoothDeviceListener) to listen for Bluetooth device discovery events and the end of discovery.
        registerReceiver(bluetoothDeviceListener, intentFilter); //dynamically register a broadcast receiver using this method, you specify the intentfilter that defines the type of intents the receiver should listen for
        IntentFilter intentFilter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothDeviceListener, intentFilter1);

        listPairedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                bluetoothAdapter.cancelDiscovery(); //Before proceeding, it cancels any ongoing Bluetooth device discovery. This is done to prevent any interference with the pairing process and ensure that the device discovery process is stopped before attempting to interact with a paired device.

                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                Log.d("Address", address);

                Intent intent = new Intent(); //An Intent object is created. Intents are used for sending and receiving messages between components of an Android application or between different applications.
                intent.putExtra("deviceAddress", address); // In this line, the Bluetooth device address extracted earlier is added as an extra to the Intent. The key "deviceAddress" is used to identify this extra.

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        showLog("Exiting init");
    }

    private BroadcastReceiver bluetoothDeviceListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction(); //This line retrieves the action string from the received Intent. In Android, an action is a string that specifies the type of operation or event that the Intent represents. The action is used to determine what kind of broadcast event has occurred.

            if (BluetoothDevice.ACTION_FOUND.equals(action)) { //If the action is BluetoothDevice.ACTION_FOUND, it means a new Bluetooth device has been discovered
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); // If a new Bluetooth device is found, this line extracts the BluetoothDevice object from the received Intent. The BluetoothDevice object represents the discovered device and contains information about it.
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) { //This condition checks if the discovered Bluetooth device is not already bonded (paired). If the device is not already paired, it proceeds to add the device's name and address to the adapterAvailableDevices. This is typically done to display newly discovered devices in a list or UI for the user to select.
                    adapterAvailableDevices.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) { //If the action is BluetoothAdapter.ACTION_DISCOVERY_FINISHED, it means that the device discovery process has finished.
                progressScanDevices.setVisibility(View.GONE); // This is typically done to hide the progress bar when device discovery is complete.
                if (adapterAvailableDevices.getCount() == 0) {
                    Toast.makeText(context, "No new devices found", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Click on the device to start the chat", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_device_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId==R.id.menu_scan_devices)
        {
            scanDevices();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }


    }

    private void scanDevices() { // This method is responsible for initiating the Bluetooth device discovery process.
        showLog("Entering scanDevices");
        progressScanDevices.setVisibility(View.VISIBLE);
        adapterAvailableDevices.clear();
        Toast.makeText(context, "Scan started", Toast.LENGTH_SHORT).show();

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();
        showLog("Exiting scanDevices");
    }

    @Override
    protected void onDestroy() { //This method is part of the activity's lifecycle and is called when the activity is being destroyed (e.g., when the user navigates away from it or the app is closed). In this case, it's used to unregister the bluetoothDeviceListener (the BroadcastReceiver) to prevent memory leaks and ensure that the receiver is no longer active when the activity is destroyed.
        super.onDestroy();

        if (bluetoothDeviceListener != null) {
            unregisterReceiver(bluetoothDeviceListener); //This line unregisters the bluetoothDeviceListener, which was previously registered to listen for Bluetooth-related broadcast events. Unregistering it ensures that it no longer receives and processes broadcast events after the activity is destroyed.
        }
    }

    private static void showLog(String message) {
        Log.d(TAG, message);
    }
}