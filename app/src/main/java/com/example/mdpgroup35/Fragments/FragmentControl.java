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
import com.example.mdpgroup35.RpiHelper.NewAction;

import org.json.JSONException;


public class FragmentControl extends Fragment {

    ImageButton forwardImageBtn, rightImageBtn, backwardImageBtn, leftImageBtn, stopImageBtn;
    ImageButton forwardRightImageBtn, forwardLeftImageBtn, backwardRightImageBtn, backwardLeftImageBtn;

    TextView movementStatusTextView;
    Switch autoManualSwitch;

    GridMap gridMap;




    private final static String TAG = "FragmentControl";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        gridMap = MainActivity.getGridMap();
//view is when an xml is inflated
        View root = inflater.inflate(R.layout.fragment_control, container, false); //inflate.inflate is used to inflate the XML layout file and create a corresponding view hierachy

        forwardImageBtn = root.findViewById(R.id.northImageBtn);
        rightImageBtn = root.findViewById(R.id.EastImageBtn);
        backwardImageBtn = root.findViewById(R.id.southImageBtn);
        leftImageBtn = root.findViewById(R.id.WestImageBtn);
        stopImageBtn = root.findViewById(R.id.stopBtn);

        forwardRightImageBtn = root.findViewById(R.id.northEastImageBtn);
        forwardLeftImageBtn = root.findViewById(R.id.northWestImageBtn);
        backwardRightImageBtn = root.findViewById(R.id.southEastImageBtn);
        backwardLeftImageBtn = root.findViewById(R.id.southWestImageBtn);


        movementStatusTextView = root.findViewById(R.id.movementStatusTV);



        stopImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                    try {
                        Action action = new Action("capture", "stop", 0, "0", "0,1,1");
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
                   // try {
                        String message = "f";
                        BluetoothUtils.write(message.getBytes());
                        //Action action = new Action(Action.MOVE, "forward", 0, Action.CMD_FORWARD, "0,1,1");
                       // String s = action.toJSON();
                       // movementStatusTextView.setText(action.action);
                        NewAction newaction = new NewAction(NewAction.MOVE,"F","C",25);
                        movementStatusTextView.setText("forward");
                        String s = newaction.convertToSTMFormat(newaction);
                        BluetoothUtils.write(s.getBytes());
                 //   } //catch (JSONException e) {
                     //   e.printStackTrace();
                  //  }
                }
            }
        });

        backwardImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                 //   try {
                        String message = "r";
                        BluetoothUtils.write(message.getBytes());

                        //Action action = new Action(Action.MOVE, "back", 0, Action.CMD_BACK, "0,1,1");
                       // String s = action.toJSON();
                       // System.out.println("s is "+ s);
                       // movementStatusTextView.setText(action.action);
                        NewAction newaction = new NewAction(NewAction.MOVE,"B","C",25);
                        movementStatusTextView.setText("forward");
                        String s = newaction.convertToSTMFormat(newaction);
                        BluetoothUtils.write(s.getBytes());
                  //  } catch (JSONException e) {
                   //     e.printStackTrace();
                  //  }
                }
            }
        });

        forwardRightImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                 //   try {

                       // Action action = new Action(Action.MOVE, "forward_right", 90, Action.CMD_FORWARD_RIGHT, "0,1,1");
                       // String s = action.toJSON();
                       // System.out.println("s is "+ s);
                      //  movementStatusTextView.setText(action.action);

                        NewAction newaction = new NewAction(NewAction.MOVE,"F","R",25);
                        movementStatusTextView.setText("forward right");
                        String s = newaction.convertToSTMFormat(newaction);
                    BluetoothUtils.write(s.getBytes());
                  //  } catch (JSONException e) {
                   //     e.printStackTrace();
                  //  }
                }
            }
        });

        forwardLeftImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                   // try {

                       // Action action = new Action(Action.MOVE, "forward_left", 90, Action.CMD_FORWARD_LEFT, "0,1,1");
                      //  String s = action.toJSON();
                        NewAction newaction = new NewAction(NewAction.MOVE,"F","L",25);
                        movementStatusTextView.setText("forward left");
                        String s = newaction.convertToSTMFormat(newaction);
                        BluetoothUtils.write(s.getBytes());
               ////     } catch (JSONException e) {
                //       e.printStackTrace();
                 //   }
                }
            }
        });

        backwardRightImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                   // try {
                     //   Action action = new Action(Action.MOVE, "back_right", 90, Action.CMD_BACK_RIGHT, "0,1,1");
                       // String s = action.toJSON();
                       // movementStatusTextView.setText(action.action);
                        NewAction newaction = new NewAction(NewAction.MOVE,"B","R",25);
                        movementStatusTextView.setText("backward right");
                        String s = newaction.convertToSTMFormat(newaction);
                        BluetoothUtils.write(s.getBytes());
                   // } catch (JSONException e) {
                   //     e.printStackTrace();
                   // }
                }
            }
        });

        backwardLeftImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                //    try {
                     //   Action action = new Action(Action.MOVE, "back_left", 90, Action.CMD_BACK_LEFT, "0,1,1");
                       // String s = action.toJSON();
                     //   movementStatusTextView.setText(action.action);
                        NewAction newaction = new NewAction(NewAction.MOVE,"B","L",25);
                        movementStatusTextView.setText("backward left");
                        String s = newaction.convertToSTMFormat(newaction);
                        BluetoothUtils.write(s.getBytes());
                //    } catch (JSONException e) {
                //        e.printStackTrace();
                 //   }
                }
            }
        });

        rightImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                    try {
                        String message = "tr";
                        BluetoothUtils.write(message.getBytes());
                        Action action = new Action(Action.MOVE, "right", 90, Action.CMD_BACK_RIGHT, "0,1,1");
                        String s = action.toJSON();
                        movementStatusTextView.setText(action.action);
                        BluetoothUtils.write(s.getBytes());
                        // String tmp = rotateLeft;
                        //  movementStatusTextView.setText(rotateLeftText);
                        //  BluetoothUtils.write(tmp.getBytes());
                    }
                    catch(JSONException e)
                    {
                        e.printStackTrace();
                    }

                    //String tmp = rotateRight;
                   // movementStatusTextView.setText(rotateRightText);
                   // BluetoothUtils.write(tmp.getBytes());
                }
            }
        });

        leftImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                    try {
                        String message = "tl";
                        BluetoothUtils.write(message.getBytes());
                        Action action = new Action(Action.MOVE, "left", 90, Action.CMD_BACK_LEFT, "0,1,1");
                        String s = action.toJSON();
                        movementStatusTextView.setText(action.action);
                        BluetoothUtils.write(s.getBytes());
                        // String tmp = rotateLeft;
                        //  movementStatusTextView.setText(rotateLeftText);
                        //  BluetoothUtils.write(tmp.getBytes());
                    }
                    catch(JSONException e)
                    {
                        e.printStackTrace();
                    }


                }
            }
        });

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
