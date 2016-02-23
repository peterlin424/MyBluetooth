package com.example.peter.mybluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by linweijie on 2/23/16.
 */
public class MyService {

    private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    private final BluetoothAdapter btAdapter = null;
    private final Handler myHandler;
    private AcceptThread myAcceptThread;
    private ConnectThread myConnectThread;
    private ConnectedThread myConnectedThread;

    private int myState;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    public MyService(Context context, Handler handler) {
        this.myHandler = handler;
        myState = STATE_NONE;
    }

    private synchronized void setState(int state) {
        myState = state;
    }

    public synchronized int getState() {
        return myState;
    }

    public synchronized void start() {
        if (myConnectThread != null) {myConnectThread.cancel(); myConnectThread = null;}
        if (myConnectedThread != null) {myConnectedThread.cancel(); myConnectedThread = null;}
        if (myAcceptThread == null) {
            myAcceptThread = new AcceptThread();
            myAcceptThread.start();
        }
        setState(STATE_LISTEN);
    }

    public synchronized void connect(BluetoothDevice device) {
        if (myState == STATE_CONNECTING) {
            if (myConnectThread != null) {myConnectThread.cancel(); myConnectThread = null;}
        }
        if (myConnectedThread != null) {myConnectedThread.cancel(); myConnectedThread = null;}
        myConnectThread = new ConnectThread(device);
        myConnectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (myConnectThread != null) {myConnectThread.cancel(); myConnectThread = null;}
        if (myConnectedThread != null) {myConnectedThread.cancel(); myConnectedThread = null;}
        if (myAcceptThread != null) {myAcceptThread.cancel(); myAcceptThread = null;}
        myConnectedThread = new ConnectedThread(socket);
        myConnectedThread.start();
        Message msg = myHandler.obtainMessage(Constant.MSG_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        myHandler.sendMessage(msg);
        setState(STATE_CONNECTED);
    }

    public synchronized void stop() {
        if (myConnectThread != null) {myConnectThread.cancel(); myConnectThread = null;}
        if (myConnectedThread != null) {myConnectedThread.cancel(); myConnectedThread = null;}
        if (myAcceptThread != null) {myAcceptThread.cancel(); myAcceptThread = null;}
        setState(STATE_NONE);
    }
    public void write(byte[] out) {
        ConnectedThread tmpCt;
        synchronized (this) {
            if (myState != STATE_CONNECTED) return;
            tmpCt = myConnectedThread;
        }
        tmpCt.write(out);
    }



    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;
        public AcceptThread() {
            BluetoothServerSocket tmpSS = null;
            try {
                tmpSS = btAdapter.listenUsingRfcommWithServiceRecord("BluetoothChat", MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmServerSocket = tmpSS;
        }
        public void run() {
            setName("AcceptThread");
            BluetoothSocket socket = null;
            while (myState != STATE_CONNECTED) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                if (socket != null) {
                    synchronized (MyService.this) {
                        switch (myState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                }
            }
        }
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket myBtSocket;
        private final BluetoothDevice mmDevice;
        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            myBtSocket = tmp;
        }
        public void run() {
            setName("ConnectThread");
            btAdapter.cancelDiscovery();
            try {
                myBtSocket.connect();
            } catch (IOException e) {
                setState(STATE_LISTEN);
                try {// �ر�socket
                    myBtSocket.close();
                } catch (IOException e2) {
                    e.printStackTrace();
                }
                MyService.this.start();
                return;
            }
            synchronized (MyService.this) {
                myConnectThread = null;
            }
            connected(myBtSocket, mmDevice);
        }
        public void cancel() {
            try {
                myBtSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket myBtSocket;
        private final InputStream mmInStream;
        private final OutputStream myOs;
        public ConnectedThread(BluetoothSocket socket) {
            myBtSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmInStream = tmpIn;
            myOs = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    myHandler.obtainMessage(Constant.MSG_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    setState(STATE_LISTEN);
                    break;
                }
            }
        }
        public void write(byte[] buffer) {
            try {
                myOs.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void cancel() {
            try {
                myBtSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
