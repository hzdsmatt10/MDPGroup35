package com.example.mdpgroup35.Fragments;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.fragment.app.Fragment;
import com.example.mdpgroup35.Grid.GridMap;
import com.example.mdpgroup35.MainActivity;
import com.example.mdpgroup35.State.State;
import com.example.mdpgroup35.Bluetooth.BluetoothUtils;
import com.example.mdpgroup35.R;

public class FragmentRecalibrate extends Fragment{

    private Button send_coord;
    public static ToggleButton startTimerBtn, exploreTypeBtn;
    public static Chronometer startTimer;
    GridMap gridMap;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recalibration, container, false);
        send_coord = root.findViewById(R.id.send_coord);
        startTimerBtn = root.findViewById(R.id.startTimerBtn);
        startTimer = root.findViewById(R.id.startTimer);
        exploreTypeBtn = root.findViewById(R.id.exploreTypeBtn);
        gridMap = MainActivity.getGridMap();

        initStartTimer();
        send_coord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(gridMap);
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
        // Set this based on your Bluetooth connection status
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
                        System.out.println(gridMap);


                        if (exploreTypeBtn.getText().equals("Image Exploration")) {

                            System.out.println("grid map is " + gridMap);
                            String[] obstacles = new String[gridMap.getObstacles().size()];
                            System.out.println("shag man");
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
                            System.out.println(sendingObstacleArray);

                        } else if (exploreTypeBtn.getText().equals("Fastest Path")) {
                            //For fastest path

                        }

                        startTimer.setBase(SystemClock.elapsedRealtime());
                        startTimer.start();


                    } else if (startTimerBtn.getText().equals("START")) {
                        if (exploreTypeBtn.getText().equals("Image Exploration")) {
                            //for image exploration

                        } else if (exploreTypeBtn.getText().equals("Fastest Path")) {
//For fastest path
                        }
                        startTimer.stop();
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