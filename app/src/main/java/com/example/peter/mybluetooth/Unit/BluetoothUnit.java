package com.example.peter.mybluetooth.Unit;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;

import java.util.Set;

/**
 * Created by Peter on 16/2/19.
 */
public class BluetoothUnit {

    public interface BTActionLisener{
        void receiveDevice(BluetoothDevice device);
        void receiveFinished();
    }

    public static final int REQUEST_ENABLE_BT = 99;
    public static final int DISCOVERABLE_DURATION_TIME = 300;
    public static final String UUID_KEY = "00001101-0000-1000-8000-00805F9B34FB";
    public static final String SERVICE_MAC_ADDRESS = "78:24:AF:B9:04:BD";

    private Context context;
    private FragmentActivity activity;
    private BluetoothAdapter mBluetoothAdapter = null;

    private BTActionLisener lisener;

    public BluetoothUnit(Context c){
        this.context = c;
        this.activity = (FragmentActivity)c;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * 檢查是否支援藍芽
     * */
    public boolean isDeviceSupported(){
        if (mBluetoothAdapter == null) {
            return false;
        }
        return true;
    }

    /**
     * 檢查藍芽是否開啟。
     * */
    // 如果藍芽未開啟，則輔助跳出開啟藍芽視窗
    public boolean isOpenEnabled(){
        if (!mBluetoothAdapter.isEnabled()) {
            PLog.e(Pub.TAG, "--- BLUETOOTH DIALOG SHOW ---");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            return false;
        }
        return true;
    }
    // 藍芽視窗回傳結果確認
    public boolean isResultEnabled(int requestCode){
        if (requestCode != REQUEST_ENABLE_BT)
            return false;

        if (!mBluetoothAdapter.isEnabled()) {
            PLog.e(Pub.TAG, "--- BLUETOOTH NOT OPEN ---");
            return false;
        }
        PLog.e(Pub.TAG, "+++ DONE, BLUETOOTH OPENED AND GOT LOCAL BT ADAPTER +++");
        return true;
    }

    /**
     * 使本機藍芽處於可被搜尋狀態，開放可被搜尋 300秒
     * */
    public void ensureDiscoverable(){
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE){
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION_TIME);
            context.startActivity(discoverableIntent);
        }
    }

    /**
     * 搜尋已經被對過的裝置清單
     * */
    public BluetoothDevice[] searchPairedDeviceList(){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        BluetoothDevice[] result = pairedDevices.toArray(new BluetoothDevice[pairedDevices.size()]);

        PLog.e(Pub.TAG, "+++ PAIRED DEVICES COUNT : " + String.valueOf(result.length) + " +++");
        return result;
    }

    /**
     * 搜尋新裝置
     * */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // ����ҵ��豸
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 已經配對過的則跳過
                if (device.getBondState() != BluetoothDevice.BOND_BONDED){
                    // 儲存取得的裝置 device
                    PLog.e(Pub.TAG, "+++ SEARCH DEVICE : " + device.getName() + " +++");
                    lisener.receiveDevice(device);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // 搜尋結束
                PLog.e(Pub.TAG, "+++ SEARCH FINISHED +++");
                lisener.receiveFinished();
            }
        }
    };
    public void searchDeviceList(BTActionLisener lisener){
        this.lisener = lisener;

        // 註冊，當一個裝置被發現時調用 onReceive
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(mReceiver, filter);

        // 當搜尋結束後調用 onReceive
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        activity.registerReceiver(mReceiver, filter);
    }

    /**
     * 建立連結
     * */
//    private UUID MY_UUID = UUID.fromString(UUID_KEY);
//    private String NAME_INSECURE = "peter";
//    private class AcceptThread extends Thread {
//        private BluetoothServerSocket serverSocket;
//
//        public AcceptThread(boolean secure){
//            BluetoothServerSocket temp = null;
//            try {
//                temp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE, MY_UUID);
//            } catch (IOException e) {
//                e.printStackTrace();
//                PLog.e(Pub.TAG, "app listen() failed");
//            }
//            serverSocket = temp;
//        }
//
//        public void run(){
//            BluetoothSocket socket = null;
//            while (true){
//                try {
//                    socket = serverSocket.accept();
//                } catch (Exception e){
//                    e.printStackTrace();
//                    PLog.e(Pub.TAG, "app accept() failed");
//                    break;
//                }
//            }
//            if (socket != null){
//                // TODO 可新建一個數據交換線程，把此 socket 傳入
//            }
//        }
//
//        // 取消監聽
//        public void cancel(){
//            try {
//                serverSocket.close();
//            } catch (Exception e){
//                e.printStackTrace();
//                PLog.e(Pub.TAG, "app close() failed");
//            }
//        }
//    }

    /**
     * 交換數據
     * */
//    private UUID MY_UUID_SECURE = UUID.fromString(UUID_KEY);
//    private String NAME_SECURE = "peter";
//    private class ConnectThread extends Thread{
//        private BluetoothSocket socket;
//        private BluetoothDevice device;
//        public ConnectThread (BluetoothDevice device, boolean secure){
//            this.device = device;
//            BluetoothSocket temp = null;
//            try {
//                temp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_SECURE);
//            } catch (IOException e){
//                e.printStackTrace();
//                PLog.e(Pub.TAG, "app create() failed");
//            }
//        }
//
//        public void run(){
//            mBluetoothAdapter.cancelDiscovery(); // 取消設備查詢
//            try{
//                socket.connect();
//            } catch (IOException e){
//                try {
//                    socket.close();
//                } catch (IOException e1){
//                    e1.printStackTrace();
//                    PLog.e(Pub.TAG, "app unable to close() socket during connection failure");
//                }
//
//                connetionFailed();
//                return;
//            }
//        }
//
//        public void cancel(){
//            try{
//                socket.close();
//            } catch (IOException e){
//                e.printStackTrace();
//                PLog.e(Pub.TAG, "app close() of connect socket failed");
//            }
//        }
//    }

    /**
     * 建立數據通信線程
     * */
//    private class ConnectedThread extends Thread {
//
//        private BluetoothSocket socket;
//        private InputStream inputStream;
//        private OutputStream outputStream;
//
//        public ConnectedThread(BluetoothSocket socket) {
//            this.socket = socket;
//            try {
//                // 獲取 輸入/輸出 串流
//                inputStream = socket.getInputStream();
//                outputStream = socket.getOutputStream();
//            } catch (IOException e) {
//                e.printStackTrace();
//                PLog.e(Pub.TAG, "app temp sockets not created");
//            }
//        }
//
//        public void run() {
//            byte[] buff = new byte[1024];
//            int len = 0;
//            // 讀數據需不斷監聽, 寫不需要
//            while (true){
//                try {
//                    len = inputStream.read(buff);
//                    // 把讀取到的數據發送給 ui 進行顯示
//                    Message msg = handler.obtainMessage(BluetoothChat.MESSAGE_READ, len, -1, buff);
//                    msg.sendToTarget();
//                } catch (IOException e){
//                    e.printStackTrace();
//                    PLog.e(Pub.TAG, "app disconnected");
//                    connectionLost();
//                    start();
//                    break;
//                }
//            }
//        }
//
//        public void write(byte[] buffer){
//            try {
//                outputStream.write(buffer);
//                handler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
//            } catch (IOException e){
//                e.printStackTrace();
//                PLog.e(Pub.TAG, "app Exception during write");
//            }
//        }
//
//        public void cancel(){
//            try {
//                socket.close();
//            } catch (IOException e){
//                e.printStackTrace();
//                PLog.e(Pub.TAG, "app close() of connect socket failed");
//            }
//        }
//    }
}
