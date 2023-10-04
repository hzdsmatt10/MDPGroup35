package com.example.mdpgroup35.Fragments;

import static com.example.mdpgroup35.MainActivity.MESSAGE_READ;
import static com.example.mdpgroup35.MainActivity.MESSAGE_STATE_CHANGED;
import static com.example.mdpgroup35.MainActivity.MESSAGE_WRITE;
import static com.example.mdpgroup35.MainActivity.gridMap;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;

import com.example.mdpgroup35.Grid.GridMap;
import com.example.mdpgroup35.MainActivity;
import com.example.mdpgroup35.RpiHelper.Response;
import com.example.mdpgroup35.State.State;
import com.example.mdpgroup35.Bluetooth.BluetoothUtils;
import com.example.mdpgroup35.R;

public class FragmentRecalibrate extends Fragment{
    private Button send_coord;
    private ToggleButton startTimerBtn, exploreTypeBtn;
    private Chronometer startTimer;
    private TextView xAxisObstacleTextView;
    private TextView yAxisObstacleTextView;
    private boolean running;
    private String connectedDevice;
    GridMap gridMap;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recalibration, container, false);
        send_coord = root.findViewById(R.id.send_coord);
        startTimerBtn = root.findViewById(R.id.startTimerBtn);
        startTimer = root.findViewById(R.id.startTimer);
        exploreTypeBtn = root.findViewById(R.id.exploreTypeBtn);



        initStartTimer();



        send_coord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] obstacles = new String[gridMap.getObstacles().size()];
                int index = 0;
                for (State i : gridMap.getObstacles()) {
                    obstacles[index] = i.sendCoord2(index + 1); // Getting the coordinates in the specified format and add them to the obstacle list
                    index++;
                }
                String sendingObstacleArray = "["; // Open the array
                for (String x : obstacles) {
                    sendingObstacleArray = sendingObstacleArray + "[" + x + "],"; // Add obstacles to the array
                }
                if (obstacles.length != 0) {
                    sendingObstacleArray = sendingObstacleArray.substring(0, sendingObstacleArray.length() - 1) + "]"; // Remove the last comma and close the array
                } else {
                    sendingObstacleArray = sendingObstacleArray + "]"; // If size is 0, just close the array
                }
                BluetoothUtils.write(sendingObstacleArray.getBytes());
            }
        });

        // Set initial state of the timer and Bluetooth connection
        running = false;
        boolean isConnectedToBluetooth = false; // Set this based on your Bluetooth connection status

        // Set click listener for the startTimerBtn

        return root;
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void initStartTimer() { ////////////////////dealing with the timer
        showLog("Entering initStartTimer");
        startTimer.setFormat("%s");
        startTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {////////////////////////////////////////////THE LOCATION TO RUN THE 2 TASKS
                if (BluetoothUtils.getState() == BluetoothUtils.STATE_CONNECTED) {
                    if (startTimerBtn.getText().equals("STOP")) {
                        if (exploreTypeBtn.getText().equals("Image Exploration")) {
//to delete once task done////////////////////////////////////
                            int index=0;
                            for(State i:gridMap.getObstacles())
                            {
                                BluetoothUtils.write(i.sendCoord4times(index+1).getBytes()); //getting the coordinates in the specified format and add them to obstacle list
                                index++;

                            }
                            /////////////////////////////////////////////////////
                        } else if (exploreTypeBtn.getText().equals("Fastest Path")) {

                        }
                        startTimer.setBase(SystemClock.elapsedRealtime());
                        startTimer.start();
                        running = true;
                    } else if (startTimerBtn.getText().equals("START")) {
                        if (exploreTypeBtn.getText().equals("Image Exploration")) {
                          //  gridMap.updateRobot(Response.getReset());
                        } else if (exploreTypeBtn.getText().equals("Fastest Path")) {
                            //gridMap.updateRobot(Response.getReset());
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







        private void showLog (String message){
            Log.d("YourTag", message); // Use your desired tag, e.g., "YourTag"
        }
}