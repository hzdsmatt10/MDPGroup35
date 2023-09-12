package com.example.mdpgroup35.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mdpgroup35.Bluetooth.BluetoothUtils;
import com.example.mdpgroup35.Grid.GridMap;
import com.example.mdpgroup35.MainActivity;
import com.example.mdpgroup35.R;
import com.example.mdpgroup35.RpiHelper.Action;

import org.json.JSONException;


public class FragmentControl extends Fragment {

    ImageButton forwardImageBtn, rightImageBtn, backwardImageBtn, leftImageBtn, stopImageBtn;
    ImageButton forwardRightImageBtn, forwardLeftImageBtn, backwardRightImageBtn, backwardLeftImageBtn;
    private Button btn_calibrate;
    TextView movementStatusTextView;
    Switch autoManualSwitch;
    Button calibrateButton;
    GridMap gridMap;

    private final String STM = "STM,";

    private final String forward = "W";
    private final String backward = "R";
    private final String forwardRight = "d";
    private final String forwardLeft = "a";
    private final String backwardRight = "e";
    private final String backwardLeft = "q";
    private final String rotateRight = "D";
    private final String rotateLeft = "A";
    private final String stop = "S";

    private final String forwardText = "Forward";
    private final String backwardText = "Reverse";
    private final String forwardRightText = "Forward Right";
    private final String forwardLeftText = "Forward Left";
    private final String backwardRightText = "Backward Right";
    private final String backwardLeftText = "Backward Left";
    private final String rotateRightText = "Rotate Right";
    private final String rotateLeftText = "Rotate Left";
    private final String stopText = "Stop";

    private EditText calibrateForward;
    private EditText calibrateBackward;
    private EditText calibrateForwardLeft;
    private EditText calibrateForwardRight;
    private EditText calibrateBackwardLeft;
    private EditText calibrateBackwardRight;

//    private Handler manualControllerHandler = new Handler ();

    private final static String TAG = "FragmentControl";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        gridMap = MainActivity.getGridMap();

        View root = inflater.inflate(R.layout.fragment_control, container, false);

        forwardImageBtn = root.findViewById(R.id.northImageBtn);
//        rightImageBtn = root.findViewById(R.id.eastImageBtn);
        backwardImageBtn = root.findViewById(R.id.southImageBtn);
//        leftImageBtn = root.findViewById(R.id.westImageBtn);
        stopImageBtn = root.findViewById(R.id.stopBtn);

        forwardRightImageBtn = root.findViewById(R.id.northEastImageBtn);
        forwardLeftImageBtn = root.findViewById(R.id.northWestImageBtn);
        backwardRightImageBtn = root.findViewById(R.id.southEastImageBtn);
        backwardLeftImageBtn = root.findViewById(R.id.southWestImageBtn);

//        autoManualSwitch = root.findViewById(R.id.autoManualSwitch);
        //btn_calibrate = root.findViewById(R.id.btn_calibrate);
        movementStatusTextView = root.findViewById(R.id.movementStatusTV);


//        calibrateForward = root.findViewById(R.id.calib_F);
//        calibrateBackward = root.findViewById(R.id.calib_B);
//        calibrateForwardLeft = root.findViewById(R.id.calib_FL);
//        calibrateForwardRight = root.findViewById(R.id.calib_FR);
//        calibrateBackwardLeft= root.findViewById(R.id.calib_BL);
//        calibrateBackwardRight = root.findViewById(R.id.calib_BR);


        // Button Listener
//        btn_calibrate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showLog("Calibrate Button Clicked");
//                String calibrateForwardString = calibrateForward.getText().toString();
//                String calibrateBackwardString = calibrateBackward.getText().toString();
//                String calibrateForwardLeftString = calibrateForwardLeft.getText().toString();
//                String calibrateForwardRightString = calibrateForwardRight.getText().toString();
//                String calibrateBackwardLeftString = calibrateBackwardLeft.getText().toString();
//                String calibrateBackwardRightString = calibrateBackwardRight.getText().toString();
//                String message = String.format("F %s,B %s,FR %s,BR %s,FL %s,BL %s", calibrateForwardString, calibrateBackwardString, calibrateForwardRightString, calibrateBackwardRightString, calibrateForwardLeftString, calibrateBackwardLeftString);
//
//                try {
//                    Action.setActionValues(message);
//                    showToast("Successfully Calibrated!\n"+message);
//                } catch (Exception e) {
//                    showToast("Error Calibrating");
//                }
//            }
//        });


//        forwardImageBtn.setOnTouchListener(new View.OnTouchListener() {
//
//            @SuppressLint("ClickableViewAccessibility")
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        manualControllerHandler.postDelayed(mAction, 10);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        manualControllerHandler.removeCallbacks(mAction);
//                        break;
//                }
//
//                return true;
//
//            }
//            Runnable mAction = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, FORWARD";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(forward.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("FORWARD");
//                    manualControllerHandler.postDelayed(this, 500);
//                }
//            };
//        });
//
//        forwardRightImageBtn.setOnTouchListener(new View.OnTouchListener() {
//
//            @SuppressLint("ClickableViewAccessibility")
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        manualControllerHandler.postDelayed(mAction, 10);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        manualControllerHandler.removeCallbacks(mAction);
//                        manualControllerHandler.removeCallbacks(mAction2);
//                        manualControllerHandler.removeCallbacks(mAction3);
//                        break;
//                }
//
//                return true;
//
//            }
//            Runnable mAction = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, FORWARDRIGHT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(forward.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("FORWARDRIGHT");
//                    manualControllerHandler.postDelayed(mAction2, 300);
//                }
//            };
//            Runnable mAction2 = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, FORWARDRIGHT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(turnRight.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("FORWARDRIGHT");
//                    manualControllerHandler.postDelayed(mAction3, 300);
//                }
//            };
//            Runnable mAction3 = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, FORWARDRIGHT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(forward.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("FORWARDRIGHT");
//                    manualControllerHandler.postDelayed(mAction, 300);
//                }
//            };
//        });
//
//        rightImageBtn.setOnTouchListener(new View.OnTouchListener() {
//
//            @SuppressLint("ClickableViewAccessibility")
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        manualControllerHandler.postDelayed(mAction, 10);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        manualControllerHandler.removeCallbacks(mAction);
//                        break;
//                }
//
//                return true;
//
//            }
//            Runnable mAction = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, RIGHT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(turnRight.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("RIGHT");
//                    manualControllerHandler.postDelayed(this, 500);
//                }
//            };
//        });
//
//        backwardRightImageBtn.setOnTouchListener(new View.OnTouchListener() {
//
//            @SuppressLint("ClickableViewAccessibility")
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        manualControllerHandler.postDelayed(mAction, 10);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        manualControllerHandler.removeCallbacks(mAction);
//                        manualControllerHandler.removeCallbacks(mAction2);
//                        manualControllerHandler.removeCallbacks(mAction3);
//                        break;
//                }
//
//                return true;
//
//            }
//            Runnable mAction = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, BACKWARDRIGHT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(reverse.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("BACKWARDRIGHT");
//                    manualControllerHandler.postDelayed(mAction2, 300);
//                }
//            };
//            Runnable mAction2 = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, BACKWARDRIGHT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(turnRight.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("BACKWARDRIGHT");
//                    manualControllerHandler.postDelayed(mAction3, 300);
//                }
//            };
//            Runnable mAction3 = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, BACKWARDRIGHT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(reverse.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("BACKWARDRIGHT");
//                    manualControllerHandler.postDelayed(mAction, 300);
//                }
//            };
//        });
//
//        backwardImageBtn.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        manualControllerHandler.postDelayed(mAction, 10);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        manualControllerHandler.removeCallbacks(mAction);
//                        break;
//                }
//
//                return true;
//
//            }
//            Runnable mAction = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, BACKWARD";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(reverse.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("BACKWARD");
//                    manualControllerHandler.postDelayed(this, 500);
//                }
//            };
//        });
//
//        backwardLeftImageBtn.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        manualControllerHandler.postDelayed(mAction, 10);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        manualControllerHandler.removeCallbacks(mAction);
//                        manualControllerHandler.removeCallbacks(mAction2);
//                        manualControllerHandler.removeCallbacks(mAction3);
//                        break;
//                }
//
//                return true;
//
//            }
//            Runnable mAction = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, BACKWARDLEFT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(reverse.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("BACKWARDLEFT");
//                    manualControllerHandler.postDelayed(mAction2, 300);
//                }
//            };
//            Runnable mAction2 = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, BACKWARDLEFT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(turnLeft.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("BACKWARDLEFT");
//                    manualControllerHandler.postDelayed(mAction3, 300);
//                }
//            };
//            Runnable mAction3 = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, BACKWARDLEFT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(reverse.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("BACKWARDLEFT");
//                    manualControllerHandler.postDelayed(mAction, 300);
//                }
//            };
//        });
//
//        leftImageBtn.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        manualControllerHandler.postDelayed(mAction, 10);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        manualControllerHandler.removeCallbacks(mAction);
//                        break;
//                }
//
//                return true;
//
//            }
//            Runnable mAction = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, LEFT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(turnLeft.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("LEFT");
//                    manualControllerHandler.postDelayed(this, 500);
//                }
//            };
//        });
//
//        forwardLeftImageBtn.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        manualControllerHandler.postDelayed(mAction, 10);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        manualControllerHandler.removeCallbacks(mAction);
//                        manualControllerHandler.removeCallbacks(mAction2);
//                        manualControllerHandler.removeCallbacks(mAction3);
//                        break;
//                }
//
//                return true;
//
//            }
//
//            Runnable mAction = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, FORWARDLEFT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(forward.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("FORWARDLEFT");
//                    manualControllerHandler.postDelayed(mAction2, 300);
//                }
//            };
//            Runnable mAction2 = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, FORWARDLEFT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(turnLeft.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("FORWARDLEFT");
//                    manualControllerHandler.postDelayed(mAction3, 300);
//                }
//            };
//            Runnable mAction3 = new Runnable() {
//                @Override
//                public void run() {
//                    if (autoManualSwitch.isChecked() && BluetoothUtils.getState() == 3) {
//                        String tmp = "STM, FORWARDLEFT";
//                        movementStatusTextView.setText(tmp);
//                        BluetoothUtils.write(forward.getBytes());
//                        //BluetoothUtils.write(tmp.getBytes());
//                    }
//                    showLog("FORWARDLEFT");
//                    manualControllerHandler.postDelayed(mAction, 300);
//                }
//            };
//        });

        stopImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                    try {
                        Action action = new Action("capture", "forward", 10, "010", "0,1,1");
                        String s = action.toJSON();
                        movementStatusTextView.setText(action.action);
                        BluetoothUtils.write(s.getBytes());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        forwardImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                    try {
                        Action action = new Action(Action.MOVE, "forward", 0, Action.CMD_FORWARD, "0,1,1");
                        String s = action.toJSON();
                        movementStatusTextView.setText(action.action);
                        BluetoothUtils.write(s.getBytes());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        backwardImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                    try {
                        Action action = new Action(Action.MOVE, "back", 0, Action.CMD_BACK, "0,1,1");
                        String s = action.toJSON();
                        movementStatusTextView.setText(action.action);
                        BluetoothUtils.write(s.getBytes());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        forwardRightImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                    try {
                        Action action = new Action(Action.MOVE, "forward_right", 90, Action.CMD_FORWARD_RIGHT, "0,1,1");
                        String s = action.toJSON();
                        movementStatusTextView.setText(action.action);
                        BluetoothUtils.write(s.getBytes());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        forwardLeftImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                    try {
                        Action action = new Action(Action.MOVE, "forward_left", 90, Action.CMD_FORWARD_LEFT, "0,1,1");
                        String s = action.toJSON();
                        movementStatusTextView.setText(action.action);
                        BluetoothUtils.write(s.getBytes());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        backwardRightImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                    try {
                        Action action = new Action(Action.MOVE, "back_right", 90, Action.CMD_BACK_RIGHT, "0,1,1");
                        String s = action.toJSON();
                        movementStatusTextView.setText(action.action);
                        BluetoothUtils.write(s.getBytes());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        backwardLeftImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                    try {
                        Action action = new Action(Action.MOVE, "back_left", 90, Action.CMD_BACK_LEFT, "0,1,1");
                        String s = action.toJSON();
                        movementStatusTextView.setText(action.action);
                        BluetoothUtils.write(s.getBytes());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

//        rightImageBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
//                    String tmp = rotateRight;
//                    movementStatusTextView.setText(rotateRightText);
//                    BluetoothUtils.write(tmp.getBytes());
//                }
//            }
//        });
//
//        leftImageBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
//                    String tmp = rotateLeft;
//                    movementStatusTextView.setText(rotateLeftText);
//                    BluetoothUtils.write(tmp.getBytes());
//                }
//            }
//        });

        // Inflate the layout for this fragment
        return root;
    }

//    private void delay() {
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    private static void showLog(String message) {
        Log.d(TAG, message);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
