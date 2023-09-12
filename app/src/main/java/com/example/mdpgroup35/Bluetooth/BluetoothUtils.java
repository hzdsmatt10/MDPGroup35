package com.example.mdpgroup35.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.mdpgroup35.MainActivity;
import com.example.mdpgroup35.RpiHelper.Action;
import com.example.mdpgroup35.RpiHelper.Response;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BluetoothUtils {
    private static BluetoothUtils instance;
    private static final String TAG = "BluetoothUtils";

    private Context context;
    private final Handler handler;
    private BluetoothAdapter bluetoothAdapter;
    private ConnectThread connectThread;
    private AcceptThread acceptThread;
    private static ConnectedThread connectedThread;

    private final UUID APP_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private final String APP_NAME = "AndroidMDPApp";

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    private static int state;

    public BluetoothUtils(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;

        state = STATE_NONE;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static int getState() {
        return state;
    }

    public synchronized void setState(int state) {
        this.state = state;
        handler.obtainMessage(MainActivity.MESSAGE_STATE_CHANGED, state, -1).sendToTarget();
    }

    private synchronized void start() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (acceptThread == null) {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        setState(STATE_LISTEN);
    }

    public synchronized void stop() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        setState(STATE_NONE);
    }

    public void connect(BluetoothDevice device) {
        if (state == STATE_CONNECTING) {
            connectThread.cancel();
            connectThread = null;
        }

        connectThread = new ConnectThread(device);
        connectThread.start();

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        setState(STATE_CONNECTING);
    }

//    public void write(byte[] buffer) {
//        ConnectedThread connThread;
//        synchronized (this) {
//            if (state != STATE_CONNECTED) {
//                return;
//            }
//
//            connThread = connectedThread;
//        }
//
//        connThread.write(buffer);
//    }

    private class AcceptThread extends Thread {
        private BluetoothServerSocket serverSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(APP_NAME, APP_UUID);
            } catch (IOException e) {
                showLog("Accept->Constructor: " + e.toString());
            }

            serverSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    showLog("Accept->Run: " + e.toString());
                    break;
//                    try {
//                        serverSocket.close();
//                    } catch (IOException e1) {
//                        showLog("Accept->Close: " + e.toString());
//                    }
                }

                if (socket != null) {
                    switch (state) {
                        case STATE_LISTEN:
                        case STATE_CONNECTING:
                            connected(socket, socket.getRemoteDevice());
                            break;
                        case STATE_NONE:
                        case STATE_CONNECTED:
                            try {
                                serverSocket.close();
                            } catch (IOException e) {
                                showLog("Accept->CloseSocket: " + e.toString());
                            }
                            break;
                    }
                }
            }
        }

        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) {
                showLog("Accept->CloseServer: " + e.toString());
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;
        private final BluetoothDevice device;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            this.device = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(APP_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            socket = tmp;
        }



        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    socket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            synchronized (BluetoothUtils.this) {
                connectThread = null;
            }

            connected(socket, device);
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                showLog("Connect->Cancel: " + e.toString());
            }
        }
    }

    public class ConnectedThread extends Thread {
        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket socket) {
            this.socket = socket;

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    String inputBuffer = new String(buffer, 0, bytes);

                    if (inputBuffer.startsWith("AN")) {
                        handler.obtainMessage(MainActivity.MESSAGE_READ_STATUS, -1, -1, inputBuffer).sendToTarget();
                    }
                    else {
                        handler.obtainMessage(MainActivity.MESSAGE_READ, -1, -1, inputBuffer).sendToTarget();
                    }
                } catch (IOException e) {
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                outputStream.write(buffer);
                handler.obtainMessage(MainActivity.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
                showLog( "write: Write is called." );
            } catch (IOException e) {
                showLog("Error writing to output stream: " + e.getMessage());
            }
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                showLog( "Could not close the connect socket" );
            }
        }
    }

    public static void writeActions(Action action) {
        try {
            String headerFrame = action.toJSON(false);
            BluetoothUtils.write(headerFrame.getBytes());
            TimeUnit.MILLISECONDS.sleep(50);
            // Send the rest of the frames
            for (Action a : action.data) {
                // 50 ms buffer time to fill the serial line
                TimeUnit.MILLISECONDS.sleep(100);
                BluetoothUtils.write(a.toJSON().getBytes());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // remove this if we can use context from the fragmentMessage
    public static void write(byte[] out){
        ConnectedThread tmp;
        tmp = connectedThread;
        tmp.write(out);
    }

    private void connectionLost() {
        Message message = handler.obtainMessage(MainActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.TOAST, "Connection Lost");
        message.setData(bundle);
        handler.sendMessage(message);

        BluetoothUtils.this.start();
    }

    private synchronized void connectionFailed() {
        Message message = handler.obtainMessage(MainActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.TOAST, "Cant connect to the device");
        message.setData(bundle);
        handler.sendMessage(message);

        BluetoothUtils.this.start();
    }

    private synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        connectedThread = new ConnectedThread(socket);
        connectedThread.start();

        Message message = handler.obtainMessage(MainActivity.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.DEVICE_NAME, device.getName());
        message.setData(bundle);
        handler.sendMessage(message);

        setState(STATE_CONNECTED);
    }

    private static void showLog(String message) {
        Log.d(TAG, message);
    }
}
