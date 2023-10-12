package com.example.mdpgroup35.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
//BluetoothserverSocket is a class which allows you to work with Bluetooth devices and comm, server side endpoint-> used to listen
//for incoming Bluetooth connection request from devices
//like a listening socket that waits for client devices to connect to it
import android.bluetooth.BluetoothSocket;
//client side endpoint for BLuetooth connection, used when your Android device wants to connect
//to another bluetooth device, create a bluetoothsocket to establish connection to a remote BLuetooth
//server, once connection establish, can used BLuetoothSocket to send and receive data between the client and server

//Bluetoothsocket is for sending and receiving within an establush connection
//BluetoothServerSocket is for receiving incoming connectio requests, whjich can lead to
//creation of BLuetoothSocket instances for data exchange




import android.content.Context;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.example.mdpgroup35.MainActivity;
import com.example.mdpgroup35.Actionables.NewAction;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BluetoothUtils {

    private static final String TAG = "BluetoothUtils";

    private Context context;
    private final Handler handler;
    private BluetoothAdapter bluetoothAdapter;
    private ConnectThread connectThread; // A thread used for initiating a connection to a remote Bluetooth device.
    private AcceptThread acceptThread; //A thread used for listening and accepting incoming Bluetooth connections.
    private static ConnectedThread connectedThread; //A thread used for handling data communication once a Bluetooth connection is established.

    private final UUID APP_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private final String APP_NAME = "MDPGroup35";

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    private String receivedMessage; // Add a variable to store the received message


    private static int state;

/*
    BluetoothSocket is primarily for sending and receiving data in an established Bluetooth connection. It's used when your device wants to actively communicate with another device. Think of it like making a phone call where both parties can talk and listen.

    BluetoothServerSocket is for receiving incoming connection requests. It's like waiting for someone to call you. When another device wants to communicate with your device, it initiates the connection, and your BluetoothServerSocket can accept it. Once accepted, you get a BluetoothSocket to send and receive data with the connecting device.



 */



    public BluetoothUtils(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;

        state = STATE_NONE;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static int getState() {
        return state;
    } //getter for the state

    public synchronized void setState(int state) { //setter for the State
        this.state = state;
        handler.obtainMessage(MainActivity.MESSAGE_STATE_CHANGED, state, -1).sendToTarget();
    }

    private synchronized void start() {
        //this function resets all the threads and sets the state to state_listen
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

    public synchronized void stop() {//resets all the threads and set the state to state_none
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

    public void connect(BluetoothDevice device) {//connect is done by delete the current connect thread and connected
        //thread before creating a new connectthread and then set the state to connecting
        if (state == STATE_CONNECTING) {
            connectThread.cancel();
            connectThread = null;
        } //delete the connect thread before creating a new one and s

        connectThread = new ConnectThread(device); //whenever you initialie a thread u have to start the thread
        connectThread.start();

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        setState(STATE_CONNECTING);
    }



    private class AcceptThread extends Thread {//initialises a Bluetooth server socket that will used for listening to incoming
        //Bluetooth connection requests. It creates the socket using a specifified UUID and service name
        //and handles any exceptions that may occur in the process
        private BluetoothServerSocket serverSocket; //store the Bluetooth server socket used for accepting incoming connections4
        public AcceptThread() { //constructor for accept thread
            BluetoothServerSocket tmp = null;
            //line intializes a temporary Bluetooth server socket variable tmp
            //with a null value
            //variable used to store the Bluetooth server socket that will be created
            try {
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(APP_NAME, APP_UUID); //this method creates a bluetoothserver socket that listens for incoming bluetooth connections on a specific UUID and service name
                //attempts to create a Bluetooth server socket(tmp) for listening to incoming connection using a specific UUID and
                //service name(APP_NAME). The UUID and service name should
                //match the ones used by the remote device initiating a connection

                //UUID is a 128-bit identifier that is universally unique and standardised

            } catch (IOException e) {
                showLog("Accept->Constructor: " + e.toString());
            }

            serverSocket = tmp;
            //after attmepting to create Bluetooth server socket, this line assigns the tmp server
            //socket to the serverSocket instance variable of the AcceptThread. This
            //stores the server socket that will be used to listen to incoming Bluetooth connections
        }

        public void run() { // a method to constantly listen for incoming bluetooth connection requests
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = serverSocket.accept(); // accept incoming connection requests. When a connection request is received, it returns a BluetoothSocket representing the established connection.
                } catch (IOException e) {
                    showLog("Accept->Run: " + e.toString());
                    break;

                }

                if (socket != null) {
                    switch (state) {
                        case STATE_LISTEN:
                        case STATE_CONNECTING:
                            connected(socket, socket.getRemoteDevice()); //If the state is STATE_LISTEN or STATE_CONNECTING, it means the device is in a state where it's ready to accept connections or in the process of connecting. In this case, it calls the connected method to handle the connection.
                            break;
                        case STATE_NONE:
                        case STATE_CONNECTED: //If the state is STATE_NONE or STATE_CONNECTED, it means the device should not be accepting more connections, so it closes the serverSocket.
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

        public void cancel() {//This method is responsible for closing the serverSocket when it's no longer needed.
            //It uses serverSocket.close() to close the Bluetooth server socket.
            try {
                serverSocket.close();
            } catch (IOException e) {
                showLog("Accept->CloseServer: " + e.toString());
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket socket; //socket stores the Bluetoothsocket used for the connection
        private final BluetoothDevice device; //store the Bluetooth device to which you want to connect.

        public ConnectThread(BluetoothDevice device) { //constructor
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            this.device = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(APP_UUID);//This line attempts to create a Bluetooth socket (tmp) for communication with the target device using a specific UUID
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            socket = tmp;
        }


/*
attempts to establish a Bluetooth connection with a remote device using the socket, cancels Bluetooth device discovery to improve connection stability, and handles any exceptions that may occur during the connection process. If the connection is successful, it calls the connected method. The cancel method is used to close the socket when it's no longer required.
 */
        public void run() {

            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    socket.close(); // it attempts to close the socket to release any resources associated with it.
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
        private final BluetoothSocket socket; //stores  bluetoothsocket used for communication with the connected bluetooth device, socket which data is sent and received
        private final InputStream inputStream; //The InputStream is used to receive data from the connected Bluetooth device. It allows you to read data sent by the remote device.
        private final OutputStream outputStream; //The OutputStream is used to send data to the connected Bluetooth device. It allows you to write data that will be transmitted to the remote device.

        public ConnectedThread(BluetoothSocket socket) { //constructor
            this.socket = socket;

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();//: This line attempts to get the input stream from the BluetoothSocket. The input stream is used for receiving data from the connected device.
                tmpOut = socket.getOutputStream(); // This line attempts to get the output stream from the BluetoothSocket. The output stream is used for sending data to the connected device.
            } catch (IOException e) {
            }

            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024]; //This buffer is used for reading data from the input stream.
            int bytes; //This variable is used to keep track of the number of bytes read from the input stream.
            while (true) {
                try {
                    bytes = inputStream.read(buffer); // line reads data from the input stream into the buffer and returns the number of bytes read, which is stored in the bytes variable.
                    String inputBuffer = new String(buffer, 0, bytes); //: It converts the read bytes into a String named inputBuffer. The 0 and bytes arguments specify the range of bytes to convert.

                    if (inputBuffer.startsWith("AN")) { //It checks if the received data starts with the characters "AN." If it does, it sends a message to the handler with the type MainActivity.MESSAGE_READ_STATUS. This indicates a special status message.
                        handler.obtainMessage(MainActivity.MESSAGE_READ_STATUS, -1, -1, inputBuffer).sendToTarget(); //This indicates a special status message.
                    }
                    else {
                        handler.obtainMessage(MainActivity.MESSAGE_READ, -1, -1, inputBuffer).sendToTarget(); // This indicates a regular data message.
                    }
                } catch (IOException e) {
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer) { //This method is used to send data to the connected Bluetooth device.
            try {
                outputStream.write(buffer); //outputStream.write(buffer);: It writes the provided buffer of bytes to the output stream, effectively
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

        public synchronized String getReceivedMessage() {
            return receivedMessage;
        }

        // Setter method for setting the received message
        private synchronized void setReceivedMessage(String message) {
            receivedMessage = message;
        }
    }


    public static void writeNewActions(NewAction newaction) throws InterruptedException {
        System.out.println(newaction);
        for(NewAction na : newaction.data)
        {
            TimeUnit.MILLISECONDS.sleep(100);
            BluetoothUtils.write(NewAction.convertToSTMFormat(na).getBytes());

        }
    }
    public static void write(byte[] out){
        ConnectedThread tmp;
        tmp = connectedThread;
        tmp.write(out);
    }

    private void connectionLost() {
        //This method is called when a Bluetooth connection is lost.
        Message message = handler.obtainMessage(MainActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.TOAST, "Connection Lost");
        message.setData(bundle);
        handler.sendMessage(message);

        BluetoothUtils.this.start(); //It then calls BluetoothUtils.this.start(), which seems to be intended to restart the Bluetooth connection.
    }

    private synchronized void connectionFailed() { //This method is called when a Bluetooth connection attempt fails.
        Message message = handler.obtainMessage(MainActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.TOAST, "Cant connect to the device");
        message.setData(bundle);
        handler.sendMessage(message);

        BluetoothUtils.this.start(); //It also attempts to restart the Bluetooth connection by calling BluetoothUtils.this.start()
    }

    private synchronized void connected(BluetoothSocket socket, BluetoothDevice device) { //This method is called when a Bluetooth connection is successfully established.
        if (connectThread != null) {//It cancels any existing connectThread and connectedThread if they exist, effectively cleaning up previous connection attempts.
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {//It cancels any existing connectThread and connectedThread if they exist, effectively cleaning up previous connection attempts.
            connectedThread.cancel();
            connectedThread = null;
        }

        connectedThread = new ConnectedThread(socket);//It initializes a new ConnectedThread with the established BluetoothSocket to handle data communication
        connectedThread.start(); //Calling start() on the Thread transitions it to the RUNNABLE state, indicating that it is ready to execute.

        Message message = handler.obtainMessage(MainActivity.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.DEVICE_NAME, device.getName());
        message.setData(bundle);
        handler.sendMessage(message); //It sends a message to a handler indicating the name of the connected Bluetooth device.

        setState(STATE_CONNECTED); //Finally, it sets the state to STATE_CONNECTED.
    }

    private static void showLog(String message) {
        Log.d(TAG, message);
    }
}
