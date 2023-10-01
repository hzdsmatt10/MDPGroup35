package com.example.mdpgroup35.Fragments;
import android.database.DataSetObserver;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import com.example.mdpgroup35.Bluetooth.BluetoothUtils;
import com.example.mdpgroup35.R;
import com.example.mdpgroup35.RpiHelper.Action;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
public class FragmentMessage extends Fragment {

    private ListView listViewSentMessages;
    private ListView listViewReceivedMessages;
    private EditText edCreateMessage;
    private Button btnSendMessage;
    private ImageButton deleteSentMessages;
    private ImageButton deleteReceivedMessages;
    private static ArrayAdapter<String> adapterSentMessages;
    private static ArrayAdapter<String> adapterReceivedMessages;
    private static final String TAG = "FragmentMessage";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_message, container, false);

        listViewSentMessages = root.findViewById(R.id.list_sent_messages);
        listViewSentMessages.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL); //autoscrolling text

        listViewReceivedMessages = root.findViewById(R.id.list_received_messages);
        listViewReceivedMessages.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        edCreateMessage = root.findViewById(R.id.edit_enter_message);
        btnSendMessage = root.findViewById(R.id.btn_send_msg);
        deleteSentMessages = root.findViewById(R.id.deleteSentMessages);
        deleteReceivedMessages = root.findViewById(R.id.deleteReceivedMessages);

        adapterSentMessages = new ArrayAdapter<String>(getActivity(), R.layout.message_layout);
        listViewSentMessages.setAdapter(adapterSentMessages);

        adapterReceivedMessages = new ArrayAdapter<String>(getActivity(), R.layout.message_layout);
        listViewReceivedMessages.setAdapter(adapterReceivedMessages);

        adapterSentMessages.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listViewSentMessages.setSelection(adapterSentMessages.getCount() - 1);
            }
        });

        adapterReceivedMessages.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listViewReceivedMessages.setSelection(adapterReceivedMessages.getCount() - 1);
            }
        });

        // set values for movement
        edCreateMessage.setText(Action.getActionValues());
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = edCreateMessage.getText().toString();
                try {
                    BluetoothUtils.write(message.getBytes());
                    Action.setActionValues(message); //for calibration as well
                } catch (Exception e) {
                }
            }
        });


        deleteSentMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterSentMessages.clear();
                adapterSentMessages.notifyDataSetChanged();
            }
        });

        deleteReceivedMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterReceivedMessages.clear();
                adapterReceivedMessages.notifyDataSetChanged();
            }
        });
        return root;
    }
    public static void addToAdapterSentMessages(String owner, String message) {
        adapterSentMessages.add(getCurrentTime() + " : " + message);
        adapterSentMessages.notifyDataSetChanged();
    }
    public static void addToAdapterReceivedMessages(String owner, String message) {
        String[] parsed;
        String header;
        try {
            parsed = message.split(" ");
            header = parsed[0];

        } catch (Exception e) {
            System.out.println("catch");
            header = message;
        }

        if(header == "ROBOT")
        {

            adapterReceivedMessages.add(getCurrentTime() + " : " + message);
            adapterReceivedMessages.notifyDataSetChanged();
        }
        /* //All these headers need to change/delete
        else if(header == "TARGET")
        {
            System.out.println("case 2");
            //handleTargetMessage(message);
            adapterReceivedMessages.add(getCurrentTime() + " : " + message);
            adapterReceivedMessages.notifyDataSetChanged();
        }
        else if (header =="STM")
        {
            System.out.println("case 3");
            adapterReceivedMessages.add(getCurrentTime() + " : " + message);
            adapterReceivedMessages.notifyDataSetChanged();
        }
        else if(header == "AN")
        {
            System.out.println("case 4");
            adapterReceivedMessages.add(getCurrentTime() + " : " + message);
            adapterReceivedMessages.notifyDataSetChanged();
        }
        */

        else {
           // System.out.println("case5");
            adapterReceivedMessages.add(getCurrentTime() + " : " + message);
            adapterReceivedMessages.notifyDataSetChanged();
        }
    }
    public static String getCurrentTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm:ss");
        date.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return date.format(currentLocalTime);
    }
    }

