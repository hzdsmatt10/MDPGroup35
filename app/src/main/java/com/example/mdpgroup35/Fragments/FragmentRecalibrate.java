package com.example.mdpgroup35.Fragments;

import static com.example.mdpgroup35.MainActivity.gridMap;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import com.example.mdpgroup35.State.State;
import com.example.mdpgroup35.Bluetooth.BluetoothUtils;
import com.example.mdpgroup35.R;

public class FragmentRecalibrate extends Fragment {
    private Button send_coord;
    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recalibration, container, false);
        send_coord = root.findViewById(R.id.send_coord);
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

}
