package com.example.mdpgroup35;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mdpgroup35.Bluetooth.BluetoothUtils;
import com.example.mdpgroup35.Bluetooth.BluetoothActivity;
import com.example.mdpgroup35.Fragments.FragmentMessage;
import com.example.mdpgroup35.Fragments.FragmentRecalibrate;
import com.example.mdpgroup35.Grid.GridMap;
import com.example.mdpgroup35.Views.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
@RequiresApi(api = Build.VERSION_CODES.S)
public class MainActivity extends AppCompatActivity {
    private static final int MY_BLUETOOTH_SCAN_PERMISSION_REQUEST = 123;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };
    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothUtils bluetoothUtils;
    private static final String TAG = "MainActivity";
    private final int LOCATION_PERMISSION_REQUEST = 101;
    private final int SELECT_DEVICE = 102;
    public static final int MESSAGE_STATE_CHANGED = 0;
    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_WRITE = 2;
    public static final int MESSAGE_DEVICE_NAME = 3;
    public static final int MESSAGE_TOAST = 4;
    public static final int MESSAGE_READ_STATUS = 5;
    public static final String DEVICE_NAME = "deviceName";
    public static final String TOAST = "toast";
    private String connectedDevice;
    public static GridMap gridMap;
    static TextView xAxisTextView, yAxisTextView;
    private int[] tabIcons = {
            R.drawable.ic_baseline_message_24,
            R.drawable.ic_baseline_map_24,
            R.drawable.ic_baseline_control_camera_24,
            R.drawable.baseline_straighten_24
    };
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {

            switch (message.what) {
                case MESSAGE_STATE_CHANGED:
                    switch (message.arg1) {
                        case BluetoothUtils.STATE_NONE:
                        case BluetoothUtils.STATE_LISTEN:
                            setState("Not Connected");
                            break;
                        case BluetoothUtils.STATE_CONNECTING:
                            setState("Connecting...");
                            break;
                        case BluetoothUtils.STATE_CONNECTED:
                            setState("Connected: " + connectedDevice);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] buffer1 = (byte[]) message.obj;
                    String outputBuffer = new String(buffer1);
                    FragmentMessage.addToAdapterSentMessages("Me: ", outputBuffer);
                    break;
                case MESSAGE_READ:
                    String inputBuffer = (String) message.obj;

                    gridMap.handleBluetoothMessage(inputBuffer);
                    // Process response message
                    //onResponse(inputBuffer);
                    if (inputBuffer.equals("end")) {

                        FragmentRecalibrate.startTimer.stop();
                        if (FragmentRecalibrate.startTimerBtn.isChecked()) {
                            FragmentRecalibrate.startTimerBtn.toggle();
                        }
                    }
                    FragmentMessage.addToAdapterReceivedMessages(connectedDevice + ": ", inputBuffer);
                    // int xaxe =0;
                    // int yaxe =0;
                    //  char direction='N';

                    String[] parsed;
                    String header;
                    try {//////////////////////////////////////////
                        ////this section might not need anymore
                        parsed = inputBuffer.split(" ");
                        //System.out.println("parsed[0] is " + parsed[0]);
                        // xaxe = Integer.parseInt(parsed[2].substring(1,parsed[2].length()-1));

                        // yaxe = Integer.parseInt(parsed[3].substring(0,parsed[3].length()-1));

                        // int angle = Integer.parseInt(parsed[4].substring(0,parsed[4].length()-2));

/*
                        if (angle==0)
                        {
                            direction ='N';
                        }
                        else if (angle==90)
                        {
                            direction ='E';
                        } else if (angle==180)
                        {
                            direction ='S';
                        } else if (angle==270)
                        {
                            direction ='W';
                        }
                        else {
                            direction ='N';
                        }
*/

                        // * TO PAD 6 CHAR HEADER
                        header = parsed[0];
                    } catch (Exception e) {
                        header = inputBuffer;
                    }


                    switch (header) {
                        case "ROBOT":
                            //if(parsed.length<)
                            gridMap.updateRobot(inputBuffer);
                            showLog("ENTER ROBOT PARSER: " + inputBuffer);

                            break;
                        case "TARGET":

                            gridMap.updateImageWithID(inputBuffer);

                            showLog("ENTER TARGET PARSER: " + inputBuffer);
                            break;
                            /*
                        case "{\"robotPosition\"": //may delete, used for AMD tool only

                            System.out.println("i am in robot position");
                            System.out.println("xaxe is "+xaxe);
                            System.out.println("xaxe is "+yaxe);
                            System.out.println("xaxe is "+direction);
                            gridMap.setCurCoord(xaxe+1,yaxe+1, String.valueOf(direction));
                            System.out.println("current coor is " + gridMap.getRobotDirection());
                            gridMap.updateRobotAxis(xaxe,yaxe,String.valueOf(direction));
*/
                        default:
                            break;
                    }

                    break;
                case MESSAGE_READ_STATUS:
                    break;

                case MESSAGE_DEVICE_NAME:
                    connectedDevice = message.getData().getString(DEVICE_NAME);
                    showLog(connectedDevice);
                    break;
                case MESSAGE_TOAST:
                    showLog(message.getData().getString(TOAST));
                    break;
            }
            return false;
        }
    });
    private void setState(CharSequence subTitle) {
        showLog("Entering setBluetoothState");
        getSupportActionBar().setSubtitle(subTitle);
        showLog("Exiting setBluetoothState");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        initBluetooth();
        bluetoothUtils = new BluetoothUtils(context, handler);
        initLayout();

        // Map
        gridMap = new GridMap(this);
        gridMap = findViewById(R.id.gridView);
        xAxisTextView = findViewById(R.id.xAxisTextView);
        yAxisTextView = findViewById(R.id.yAxisTextView);

    }
    private void initBluetooth() {
        showLog("Entering initBluetooth");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "No bluetooth found", Toast.LENGTH_SHORT).show();
        }
        showLog("Exiting initBluetooth");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_search_devices) {
            System.out.println("scan_devices option");
            checkPermissions();
            return true;
        } else if (itemId == R.id.menu_enable_bluetooth) {
            System.out.println("enable the bluetooth");
            enableBluetooth();
            return true;
        } else if (itemId == R.id.menu_disconnect_devices) {
            //BluetoothUtils.write("RESET".getBytes());
            bluetoothUtils.stop();
            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }


    }
    private void checkPermissions() {
        showLog("Entering checkPermissions");
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        } else {
            Intent intent = new Intent(context, BluetoothActivity.class);
            startActivityForResult(intent, SELECT_DEVICE);
        }
        showLog("Exiting checkPermissions");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_DEVICE && resultCode == RESULT_OK) {
            String address = data.getStringExtra("deviceAddress");
            bluetoothUtils.connect(bluetoothAdapter.getRemoteDevice(address));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(context, BluetoothActivity.class);
                startActivityForResult(intent, SELECT_DEVICE);
            } else {
                new AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setMessage("Location permission is required.\n Please grant")
                        .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                checkPermissions();
                            }
                        })
                        .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.this.finish();
                            }
                        }).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private void enableBluetooth() {
        showLog("Entering enableBluetooth");
        if (!bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        PERMISSIONS_STORAGE,
                        6

                );
                return;
            }
            bluetoothAdapter.enable();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,
                    MY_BLUETOOTH_SCAN_PERMISSION_REQUEST);
        }

        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {

            Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoveryIntent);
        }
        ;


        showLog("Exiting enableBluetooth");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothUtils != null) {
            bluetoothUtils.stop();
        }
    }

    private void initLayout() {
        showLog("Entering initLayout");
        TabLayout tabLayout = findViewById(R.id.tabsLayout);
        ViewPager2 viewPager2 = findViewById(R.id.viewPager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            if (position == 0) {
                tab.setText("Message");
                tab.setIcon(tabIcons[position]);
            } else if (position == 1) {
                tab.setText("Map");
                tab.setIcon(tabIcons[position]);
            } else if (position == 2) {
                tab.setText("Controller");
                tab.setIcon(tabIcons[position]);
            } else if (position == 3) {
                tab.setText("Tasks");
                tab.setIcon(tabIcons[position]);
            }
        }).attach();
        showLog("Exiting initLayout");
    }
    public static GridMap getGridMap() {
        return gridMap;
    }

    private static void showLog(String message) {
        Log.d(TAG, message);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}