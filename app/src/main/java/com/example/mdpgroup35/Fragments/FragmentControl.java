package com.example.mdpgroup35.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.mdpgroup35.Bluetooth.BluetoothUtils;
import com.example.mdpgroup35.Grid.GridMap;
import com.example.mdpgroup35.MainActivity;
import com.example.mdpgroup35.R;
import com.example.mdpgroup35.RpiHelper.NewAction;

public class FragmentControl extends Fragment {

    ImageButton forwardImageBtn, rightImageBtn, backwardImageBtn, leftImageBtn, stopImageBtn;
    ImageButton forwardRightImageBtn, forwardLeftImageBtn, backwardRightImageBtn, backwardLeftImageBtn;
    TextView movementStatusTextView;
    GridMap gridMap;
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
                  BluetoothUtils.write("Capture".getBytes());
                }
           }
        });

        forwardImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {

                        NewAction newaction = new NewAction(NewAction.MOVE,"F","C",25,0);
                        movementStatusTextView.setText("forward");
                        String s = newaction.convertToSTMFormat(newaction);
                        BluetoothUtils.write(s.getBytes());

                }
            }
        });

        backwardImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {

                        NewAction newaction = new NewAction(NewAction.MOVE,"B","C",25,0);
                        movementStatusTextView.setText("forward");
                        String s = newaction.convertToSTMFormat(newaction);
                        BluetoothUtils.write(s.getBytes());
                }
            }
        });

        forwardRightImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                        NewAction newaction = new NewAction(NewAction.MOVE,"F","R",25,90);
                        movementStatusTextView.setText("forward right");
                        String s = newaction.convertToSTMFormat(newaction);
                    BluetoothUtils.write(s.getBytes());
                }
            }
        });

        forwardLeftImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                        NewAction newaction = new NewAction(NewAction.MOVE,"F","L",25,90);
                        movementStatusTextView.setText("forward left");
                        String s = newaction.convertToSTMFormat(newaction);
                        BluetoothUtils.write(s.getBytes());

                }
            }
        });

        backwardRightImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                        NewAction newaction = new NewAction(NewAction.MOVE,"B","R",25,90);
                        movementStatusTextView.setText("backward right");
                        String s = newaction.convertToSTMFormat(newaction);
                        BluetoothUtils.write(s.getBytes());

                }
            }
        });

        backwardLeftImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                        NewAction newaction = new NewAction(NewAction.MOVE,"B","L",25,90);
                        movementStatusTextView.setText("backward left");
                        String s = newaction.convertToSTMFormat(newaction);
                        BluetoothUtils.write(s.getBytes());

                }
            }
        });

        rightImageBtn.setOnClickListener(new View.OnClickListener() {//may remove in the future
            @Override
            public void onClick(View view) {
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                    try {
                        BluetoothUtils.writeNewActions(NewAction.skirtRight());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        leftImageBtn.setOnClickListener(new View.OnClickListener() {//may remove in the future
            @Override
            public void onClick(View view) {

                System.out.println(gridMap.getObstacles());
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
               BluetoothUtils.write("RESET".getBytes());
                }
            }
        });

        // Inflate the layout for this fragment
        return root;
    }



}
