package com.example.mdpgroup35.Fragments;

import static com.example.mdpgroup35.MainActivity.gridMap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.mdpgroup35.Algo.State;
import com.example.mdpgroup35.Bluetooth.BluetoothUtils;
import com.example.mdpgroup35.MainActivity;
import com.example.mdpgroup35.R;
import com.example.mdpgroup35.RpiHelper.Action;

public class FragmentRecalibrate extends Fragment {

    private EditText calibrateForward;
    private EditText calibrateBackward;
    private EditText calibrateForwardLeft;
    private EditText calibrateForwardRight;
    private EditText calibrateBackwardLeft;
    private EditText calibrateBackwardRight;
    private Button btn_calibrate;

    private Button send_coord;

    private final static String TAG = "FragmentRecalibrate";

    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recalibration, container, false);
        btn_calibrate = root.findViewById(R.id.btn_calibrate);
        calibrateForward = root.findViewById(R.id.calib_F);
        calibrateBackward = root.findViewById(R.id.calib_B);
        calibrateForwardLeft = root.findViewById(R.id.calib_FL);
        calibrateForwardRight = root.findViewById(R.id.calib_FR);
        calibrateBackwardLeft= root.findViewById(R.id.calib_BL);
        calibrateBackwardRight = root.findViewById(R.id.calib_BR);
        send_coord = root.findViewById(R.id.send_coord);

        btn_calibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLog("Calibrate Button Clicked");
                String calibrateForwardString = calibrateForward.getText().toString();
                String calibrateBackwardString = calibrateBackward.getText().toString();
                String calibrateForwardLeftString = calibrateForwardLeft.getText().toString();
                String calibrateForwardRightString = calibrateForwardRight.getText().toString();
                String calibrateBackwardLeftString = calibrateBackwardLeft.getText().toString();
                String calibrateBackwardRightString = calibrateBackwardRight.getText().toString();
                String message = String.format("F %s,B %s,FR %s,BR %s,FL %s,BL %s", calibrateForwardString, calibrateBackwardString, calibrateForwardRightString, calibrateBackwardRightString, calibrateForwardLeftString, calibrateBackwardLeftString);

                try {
                    Action.setActionValues(message);
                    showToast("Successfully Calibrated!\n"+message);
                } catch (Exception e) {
                    showToast("Error Calibrating");
                }
            }

        });
        send_coord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] obstacles = new String[gridMap.getObstacles().size()];

                int index=0;
                for(State i:gridMap.getObstacles())
                {


                    obstacles[index] = i.sendCoord2(index+1); //getting the coordinates in the specified format and add them to obstacle list
                    index++;




                }
                String sendingObstacleArray ="[";//open the array
                for (String x: obstacles)
                {

                     sendingObstacleArray = sendingObstacleArray + "[" + x + "],";//add obstacles to the array
                }
                if(obstacles.length !=0)
                {
                    sendingObstacleArray =sendingObstacleArray.substring(0,sendingObstacleArray.length()-1) + "]";//remove the last comma and close the array
                }
                else
                {
                    sendingObstacleArray =sendingObstacleArray + "]"; //if size 0 just close the array
                }






                BluetoothUtils.write(sendingObstacleArray.getBytes());
            }
        });

        return root;
    }
    private static void showLog(String message) {
        Log.d(TAG, message);
    }
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
