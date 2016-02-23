package com.example.peter.mybluetooth;

import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class MyDeviceListActivity extends AppCompatActivity {

    public static String EXTRA_DEVICE_ADDR = "device_address";

    private BluetoothAdapter myBtAdapter;
    private ArrayAdapter<String> myAdapterPaired;
    private ArrayAdapter<String> myAdapterNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_device_list);
    }
}
