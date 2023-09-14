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
    private EditText edCreateMessage;




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
                        String message = "f";
                        BluetoothUtils.write(message.getBytes());
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
                        String message = "r";
                        BluetoothUtils.write(message.getBytes());

                        Action action = new Action(Action.MOVE, "back", 0, Action.CMD_BACK, "0,1,1");
                        String s = action.toJSON();
                        System.out.println("s is "+ s);
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
                        String message = "tr";
                        BluetoothUtils.write(message.getBytes());
                        Action action = new Action(Action.MOVE, "forward_right", 90, Action.CMD_FORWARD_RIGHT, "0,1,1");
                        String s = action.toJSON();
                        System.out.println("s is "+ s);
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
                        String message = "tl";
                        BluetoothUtils.write(message.getBytes());
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


    private static void showLog(String message) {
        Log.d(TAG, message);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
