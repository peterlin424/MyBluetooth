package com.example.peter.mybluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BluetoothUnit.BTActionLisener{

    private BluetoothUnit bluetooth = new BluetoothUnit(this, this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PLog.e(Pub.TAG, "+++ ON CREATE +++");

        initView();

        if (!bluetooth.isDeviceSupported())
            Toast.makeText(this, "Bluetooth is not available.", Toast.LENGTH_LONG).show();
    }

    private void initView() {
        Button bt_open = (Button)findViewById(R.id.bt_open);
        Button bt_search = (Button)findViewById(R.id.bt_search);
        Button bt_send = (Button)findViewById(R.id.bt_send);
        Button bt_close = (Button)findViewById(R.id.bt_close);

        bt_open.setOnClickListener(this);
        bt_search.setOnClickListener(this);
        bt_send.setOnClickListener(this);
        bt_close.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        PLog.e(Pub.TAG, "++ ON START ++");
    }

    @Override
    protected void onResume() {
        super.onResume();
        PLog.e(Pub.TAG, "+ ON RESUME +");
    }

    @Override
    public void onPause() {
        super.onPause();
        PLog.e(Pub.TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        PLog.e(Pub.TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PLog.e(Pub.TAG, "--- ON DESTROY ---");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (!bluetooth.isResultEnabled(requestCode))
            Toast.makeText(this, "Please enable your BT and re-run this program.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.bt_open:
                if (bluetooth.isOpenEnabled())
                    Toast.makeText(this, "Your BT is already enabled.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.bt_search:
                bluetooth.ensureDiscoverable();
                BluetoothDevice[] pairedDevices = bluetooth.searchPairedDeviceList();

                // TODO show and select device
                bluetooth.searchDeviceList();
                break;

            case R.id.bt_send:
                break;

            case R.id.bt_close:
                break;
        }
    }

    @Override
    public void receiveDevice(BluetoothDevice device) {

    }

    @Override
    public void receiveFinished() {

    }
}
