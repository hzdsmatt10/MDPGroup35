package com.example.mdpgroup35;

import androidx.annotation.NonNull;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.mdpgroup35.Algo.Capture;
import com.example.mdpgroup35.Algo.DijkstraPath;
import com.example.mdpgroup35.Algo.Node;
import com.example.mdpgroup35.Algo.STMCommands;
import com.example.mdpgroup35.Algo.State;
import com.example.mdpgroup35.Bluetooth.BluetoothUtils;
import com.example.mdpgroup35.Bluetooth.BluetoothActivity;
import com.example.mdpgroup35.Fragments.FragmentMessage;
import com.example.mdpgroup35.Grid.GridMap;
import com.example.mdpgroup35.RpiHelper.API;
import com.example.mdpgroup35.RpiHelper.Action;
import com.example.mdpgroup35.RpiHelper.Response;
import com.example.mdpgroup35.RpiHelper.WifiUtils;
import com.example.mdpgroup35.Views.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final int MY_BLUETOOTH_SCAN_PERMISSION_REQUEST = 123;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };
    private static String[] PERMISSIONS_LOCATION = {
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
    private WifiUtils wifiUtils;
    private API api;
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
    //for checking point for strategy
    final int SATE_DIST = 1;
    final int MAP_SIZE = 20;
    public static int[][] AlgoMap;

    static TextView xAxisTextView, yAxisTextView;

    // TODO: REMOVE after checklist
    private boolean isBullseyeDone = false;
    private int swap = 1;
    // For first initial turn for A5
    private boolean executedOnce = false;
    // Dynamic distance for A5
    private boolean moveForward = true;

    // STM commands helper
    STMCommands stm;

    private int[] tabIcons = {
            R.drawable.ic_baseline_message_24,
            R.drawable.ic_baseline_map_24,
            R.drawable.ic_baseline_control_camera_24,
            R.drawable.baseline_straighten_24
    };

    ToggleButton startTimerBtn, exploreTypeBtn;
    private Chronometer startTimer;
    private boolean running;

    private Action currentSeriesAction;

    private String tempMsg;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            System.out.println(message.what);
            switch (message.what) {
                case MESSAGE_STATE_CHANGED:
                    switch (message.arg1) {
                        case BluetoothUtils.STATE_NONE:
                            setState("Not Connected");
                            break;
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
                    Toast.makeText(context, "MESSAGE WRITE", Toast.LENGTH_SHORT).show();
                    byte[] buffer1 = (byte[]) message.obj;
                    String outputBuffer = new String(buffer1);
                    FragmentMessage.addToAdapterSentMessages("Me: ", outputBuffer);
                    break;
                case MESSAGE_READ:
                    Toast.makeText(context, "MESSAGE READ", Toast.LENGTH_SHORT).show();
                    String inputBuffer = (String) message.obj;
                    System.out.println("i am out");

                    // Process response message
                    onResponse(inputBuffer);

                    if (inputBuffer.equals("s") && running) {
                        startTimer.stop();
                        running = false;
                        startTimerBtn.toggle();
                    }
                    FragmentMessage.addToAdapterReceivedMessages(connectedDevice + ": ", inputBuffer);

                    String[] parsed;
                    String header;
                    try {
                        parsed = inputBuffer.split(" ");
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
                        default:
                            break;
                    }

                    break;
                case MESSAGE_READ_STATUS:
                    String inputBufferStatus = (String) message.obj;
                    String inputBufferStatusExtra = null;
                    String[] inputArray = inputBufferStatus.split("\\s*,\\s*");
                    String[] inputArrayAdditional = null;

                    if (inputArray.length > 5 && !inputBufferStatus.contains("STM")) {
                        int index = inputBufferStatus.indexOf("AN", inputBufferStatus.indexOf("AN") + 1);
                        inputBufferStatusExtra = inputBufferStatus.substring(index);
                        inputBufferStatus = inputBufferStatus.substring(0, index);
                        inputArray = inputBufferStatus.split("\\s*,\\s*");
                        inputArrayAdditional = inputBufferStatusExtra.split("\\s*,\\s*");
                    }
                    if (inputBufferStatus.startsWith("AN") && running) {
                        handleMessageRead(inputBufferStatus, inputArray);
                        FragmentMessage.addToAdapterReceivedMessages(connectedDevice + ": ", inputBufferStatus);
                    }
                    if (inputBufferStatusExtra != null && inputBufferStatusExtra.startsWith("AN") && running) {
                        handleMessageRead(inputBufferStatusExtra, inputArrayAdditional);
                        FragmentMessage.addToAdapterReceivedMessages(connectedDevice + ": ", inputBufferStatusExtra);
                    }
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

    private void onResponseCapture(Response res) {
        int allowance = Action.DISTANCE_ALLOWABLE;
        Action a = null;
        String coord = res.prevCoordinate != null ||
                !res.prevCoordinate.isEmpty() ? res.prevCoordinate : "0,0,0";

        String[] parsed = coord.split(",");
        int col = Integer.parseInt(parsed[0]);
        int row = Integer.parseInt(parsed[1]);
        int dir = Integer.parseInt(parsed[2]);
        State robotCoord = new State(col, row, dir);

        String[] obstacleParsed = res.coordinate.split(",");
        int oCol = Integer.parseInt(obstacleParsed[0]);
        int oRow = Integer.parseInt(obstacleParsed[1]);
        int oDir = Integer.parseInt(obstacleParsed[2]);
        State obstacleCoord = new State(oCol, oRow, oDir);

        boolean fail = res.status != 1 || res.result.equalsIgnoreCase("10") || res.result.equalsIgnoreCase("-1");

        // Get result from strategy and if it fails
        if (res.action.contains(Action.STRATEGY) &&
                res.action.split(":")[1].equalsIgnoreCase(Action.FORWARD) &&
                fail) {
            backStrategy(robotCoord, obstacleCoord);
            return;
        }

        // Run strategy if obstacle is not detected
        // Do not run if it's a calibrate capture
        if (!res.action.contains(Action.CALIBRATE) &&
                !res.action.contains(Action.STRATEGY) &&
                fail
        ) {
            strategy(robotCoord, obstacleCoord);
            return;
        }

        // Do not update if it's a calibrate capture
        if (!res.action.contains(Action.CALIBRATE))
            gridMap.updateImageWithID(res);

        try {
            // Do not correct if it's last obstacle
            if (obstacleCoord.getCoord().equalsIgnoreCase(currentSeriesAction.data.get(currentSeriesAction.data.size() - 1).coordinate))
                return;

            // Correction
            int correction = 0;
            if (res.action.contains(Action.CALIBRATE))
                // CALIBRATE:1 num is on second index
                correction = Integer.parseInt(res.action.split(":")[1]);

            // Might move right
            if (res.distance < 0 && Math.abs(res.distance) > allowance) {
                a = Action.slideToRight(robotCoord, correction + 1);
            } else if (res.distance > allowance) { // Move left
                a = Action.slideToLeft(robotCoord, correction + 1);
            }

            if (correction >= Action.MAX_CORRECTION || a == null) {
                api.post(Action.getInterleaveNoop().toJSONObject());
                return;
            }
            api.post(a.toJSONObject());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private boolean canMoveForward(State robotCoord) {
        if (robotCoord.dir == 0) {
            if (checkPoint(robotCoord.x + 1, robotCoord.y, AlgoMap))
                return false;
        } else if (robotCoord.dir == 1) {
            if (checkPoint(robotCoord.x, robotCoord.y + 1, AlgoMap))
                return false;
        } else if (robotCoord.dir == 2) {
            if (checkPoint(robotCoord.x - 1, robotCoord.y, AlgoMap))
                return false;
        } else {
            if (checkPoint(robotCoord.x, robotCoord.y - 1, AlgoMap))
                return false;
        }
        return true;
    }

    private boolean canMoveBackward(State robotCoord) {
        if (robotCoord.dir == 0) {
            if (checkPoint(robotCoord.x - 1, robotCoord.y, AlgoMap))
                return false;
        } else if (robotCoord.dir == 1) {
            if (checkPoint(robotCoord.x, robotCoord.y - 1, AlgoMap))
                return false;
        } else if (robotCoord.dir == 2) {
            if (checkPoint(robotCoord.x + 1, robotCoord.y, AlgoMap))
                return false;
        } else {
            if (checkPoint(robotCoord.x, robotCoord.y + 1, AlgoMap))
                return false;
        }
        return true;
    }

    /**
     * Get the previous response to
     *
     * @param
     */
    public void strategy(State robotCoord, State obstacleCoord) {
        try {
            Action noop = Action.getInterleaveNoop();
            if (robotCoord == null || obstacleCoord == null) {
                API.getInstance().post(noop.toJSONObject());
                return;
            }
            Action forward = Action.forwardStrategy(robotCoord, obstacleCoord);
            Action backward = Action.backStrategy(robotCoord, obstacleCoord);

            boolean canMoveForward = canMoveForward(robotCoord);
            boolean canMoveBackward = canMoveBackward(robotCoord);
            // Send noop
            if (canMoveForward)
                API.getInstance().post(forward.toJSONObject());
            else if (canMoveBackward)
                API.getInstance().post(backward.toJSONObject());
            else
                API.getInstance().post(noop.toJSONObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void backStrategy(State robotCoord, State obstacleCoord) {
        Action backward = Action.backStrategy(robotCoord, obstacleCoord);
        Action noop = Action.getInterleaveNoop();

        boolean canMoveBackward = canMoveBackward(robotCoord);

        try {
            // Send noop
            if (canMoveBackward)
                API.getInstance().post(backward.toJSONObject());
            else
                API.getInstance().post(noop.toJSONObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void forwardStrategy(State robotCoord, State obstacleCoord) {
        Action forward = Action.forwardStrategy(robotCoord, obstacleCoord);
        Action noop = Action.getInterleaveNoop();

        boolean canMoveForward = canMoveForward(robotCoord);

        try {
            // Send noop
            if (canMoveForward)
                API.getInstance().post(forward.toJSONObject());
            else
                API.getInstance().post(noop.toJSONObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Bluetooth response handler
     *
     * @param inputBuffer
     */
    private void onResponse(String inputBuffer) {
        Response res = null;
        try {
            res = Response.fromJSON(inputBuffer);

            if (res.type.equalsIgnoreCase(Action.MOVE)) {
                // Update Move
                gridMap.updateRobot(res);
            } else if (res.type.equalsIgnoreCase(Action.CAPTURE)) {
                onResponseCapture(res);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onBullseye(Response res) {
        if (isBullseyeDone) {
            return;
        }

        // if not a delimiter
        if (!res.coordinate.equalsIgnoreCase(Action.BULLSEYE))
            return;

        // Check
        if (!res.result.isEmpty() && !res.result.equalsIgnoreCase(Capture.OBSTACLE) && !res.result.equalsIgnoreCase(Capture.NO_IMAGE)) {
            isBullseyeDone = true;
            return;
        }

        // Turns anti clockwise
        // Converted actions
        // FR x3B FL xF BR
        ArrayList<Action> actions = new ArrayList<>();
        if (moveForward && res.distance >= 40) {
            // Stop moving forward
            moveForward = false;
        }

        // HARD CODE MOVE FORWARD
        if (moveForward) {
            if (swap % 2 != 0) {
                actions.add(new Action("capture", "", 0, "010", Action.BULLSEYE));
            } else {
                actions.add(new Action("move", "forward", 0, Action.CMD_FORWARD, Action.BULLSEYE));
            }
            swap++;
        } else {
            if (!executedOnce) {
                executedOnce = true;
                actions.add(new Action("move", "back", 0, Action.CMD_BACK, ""));
                actions.add(new Action("move", "back", 0, Action.CMD_BACK, ""));
            }
            actions.add(new Action("move", "forward_right", 0, Action.CMD_FORWARD_RIGHT, ""));
            actions.add(new Action("move", "forward_left", 0, Action.CMD_FORWARD_LEFT, ""));
            actions.add(new Action("move", "forward", 0, Action.CMD_FORWARD, ""));
            actions.add(new Action("move", "forward", 0, Action.CMD_FORWARD, ""));
            actions.add(new Action("move", "forward", 0, Action.CMD_FORWARD, ""));
            actions.add(new Action("move", "back_right", 0, Action.CMD_BACK_RIGHT, ""));
            actions.add(new Action("capture", "", 0, "010", Action.BULLSEYE));
        }
        // bb fr fl  fff br c |  fr fl fff br c | fr fl fff br c | fr fl fff br c 27

        // Wrapper action api
        Action action = new Action(Action.SERIES, actions);

        // Send Command
        BluetoothUtils.writeActions(action);

    }

    private void handleMessageRead(String inputBuffer, String[] inputArray) {
        if (inputBuffer.contains("StopIE") && running) {
            startTimer.stop();
            running = false;
            startTimerBtn.toggle();
        } else if (inputBuffer.contains("StopFP") && running) {
            startTimer.stop();
            running = false;
            startTimerBtn.toggle();
        }
    }

    private void setState(CharSequence subTitle) {
        showLog("Entering setBluetoothState");
        getSupportActionBar().setSubtitle(subTitle);
        showLog("Exiting setBluetoothState");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // STM commands helper
        stm = new STMCommands();

        context = this;

        initBluetooth();
        bluetoothUtils = new BluetoothUtils(context, handler);
        initLayout();

        // Wifi
        wifiUtils = WifiUtils.getInstance();
        wifiUtils.connect(context);

        // API
        api = API.getInstance();
        api.init(context);

        // Map
        gridMap = new GridMap(this);
        gridMap = findViewById(R.id.gridView);
        xAxisTextView = findViewById(R.id.xAxisTextView);
        yAxisTextView = findViewById(R.id.yAxisTextView);

        // Timer
        startTimerBtn = findViewById(R.id.startTimerBtn);
        startTimer = findViewById(R.id.startTimer);
        exploreTypeBtn = findViewById(R.id.exploreTypeBtn);
        initStartTimer();
    }

    //functions for initializing map from algo and checking point for strategy

    public boolean checkPoint(int x, int y, int[][] map) {
        if (x < 0 || y < 0 || x >= MAP_SIZE || y >= MAP_SIZE) {
            return false;
        }
        if (map[x][y] == 1) {
            return true;
        }
        return false;
    }

    public int[][] SetUpMap(ArrayList<State> obstacles) {
        int[][] map = new int[20][20];
        for (int a = 0; a < 20; a++)
            for (int b = 0; b < 20; b++)
                map[a][b] = 0;
        for (State i : obstacles) {
            map[i.x][i.y] = 1;
            for (int dx = -SATE_DIST; dx < SATE_DIST + 1; dx++) {
                for (int dy = -SATE_DIST; dy < SATE_DIST + 1; dy++) {
                    if ((i.x + dx) >= MAP_SIZE || (i.x + dx) < 0 || (i.y + dy) >= MAP_SIZE || (i.y + dy) < 0) {
                        continue;
                    }
                    map[i.x + dx][i.y + dy] = 1;
                }
            }
        }
        for (int i = 0; i < MAP_SIZE; i++) {
//            map[i][1] = 1;
            map[i][0] = 1;
            map[i][MAP_SIZE - 1] = 1;
            map[0][i] = 1;
//            map[1][i] = 1;
            map[MAP_SIZE - 1][i] = 1;
//            map[MAP_SIZE - 2][i] = 1;
//            map[i][MAP_SIZE - 2] = 1;
        }
        // for i in range(21):
        // map[20][i]=1
        // for i in range(21):
        // map[i][20]=1
        // print(map)
        return map;
    }


    private Action findShortestPath() {
        State start = new State(
                getGridMap().getCurCoord()[0] - 1,
                getGridMap().getCurCoord()[0] - 1,
                State.compassToDirection(getGridMap().getRobotDirection())
        );
        ArrayList<State> obstacles = gridMap.getObstacles();

        AlgoMap = SetUpMap(obstacles);

        return Action.findShortestPath(start, obstacles);
    }

    private void runBullseye() {
        isBullseyeDone = false;
        isBullseyeDone = false;
        swap = 1;
        executedOnce = false;
        moveForward = true;
        onBullseye(new Response(0, "move", "", "-1", "", Action.BULLSEYE, 1));
    }

    private void runImagePath() {
        // Run shortest path
        currentSeriesAction = findShortestPath();
        for (Action a : currentSeriesAction.data) {
            showLog(a.getCommand());
        }
        try {
            api.post(currentSeriesAction.toJSONObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        BluetoothUtils.writeActions(action);
    }

    private void runFastest() {
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(new Action(Action.MOVE, Action.FORWARD, Action.CMD_FORWARD, 350));
        actions.add(new Action(Action.MOVE, Action.FORWARD_LEFT, Action.CMD_FORWARD_LEFT, 2000));
        actions.add(new Action(Action.MOVE, Action.FORWARD, Action.CMD_SPOT_BACK, 350));
        actions.add(new Action(Action.MOVE, Action.FORWARD_RIGHT, Action.CMD_SPOT_FORWARD_RIGHT, 350));
        actions.add(new Action(Action.MOVE, Action.FORWARD_RIGHT, Action.CMD_SPOT_FORWARD_RIGHT, 350));
        actions.add(new Action(Action.MOVE, Action.BACK, Action.CMD_SPOT_BACK, 350));
        actions.add(new Action(Action.MOVE, Action.FORWARD_RIGHT, Action.CMD_SPOT_FORWARD_RIGHT, 350));
        Action action = new Action(Action.SERIES, actions);
        try {
            api.post(action.toJSONObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initStartTimer() {
        showLog("Entering initStartTimer");
        startTimer.setFormat("%s");
        startTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempMsg = "PC,";
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                    if (startTimerBtn.getText().equals("STOP")) {
                        if (exploreTypeBtn.getText().equals("Image Exploration")) {
                            runFastest();
//                            runBullseye();
                        } else if (exploreTypeBtn.getText().equals("Fastest Path")) {
                            runFastest();
                        }
                        startTimer.setBase(SystemClock.elapsedRealtime());
                        startTimer.start();
                        running = true;
                    } else if (startTimerBtn.getText().equals("START")) {
                        if (exploreTypeBtn.getText().equals("Image Exploration")) {
                            tempMsg += "StopIE";
                            try {
                                BluetoothUtils.write(Action.getReset().toJSON().getBytes());
                                gridMap.updateRobot(Response.getReset());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (exploreTypeBtn.getText().equals("Fastest Path")) {
                            try {
                                BluetoothUtils.write(Action.getReset().toJSON().getBytes());
                                gridMap.updateRobot(Response.getReset());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        startTimer.stop();
                        running = false;
                    }
                } else {
                    startTimerBtn.toggle();
                    showToast("Not connected. Please connect to a bluetooth device");
                }
            }
        });
        showLog("Exiting initStartTimer");
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
                System.out.println("The wrong place");
                ActivityCompat.requestPermissions(
                        this,
                        PERMISSIONS_STORAGE,
                        6

                );
                return;
            }
            bluetoothAdapter.enable();
        }
        System.out.println("continuing");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,
                    MY_BLUETOOTH_SCAN_PERMISSION_REQUEST);
        }

        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            System.out.println("continuing1 ");
            Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoveryIntent);
        };
        System.out.println("continuing2 ");

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
            }
            else if(position ==3){
                tab.setText("Recalibration");
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