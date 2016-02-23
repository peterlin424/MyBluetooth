package com.example.peter.mybluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    private EditText outEt;
    private Button sendBtn;
    private String connectedNameStr = null;
    private StringBuffer outSb;
    private BluetoothAdapter btAdapter = null;
    private MyService myService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!btAdapter.isEnabled()) {
            Toast.makeText(this, "«Îœ»ø™∆Ù¿∂—¿£°", Toast.LENGTH_LONG).show();
            finish();
        } else {
            if (myService == null)
                initChat();
        }
    }
    @Override
    public synchronized void onResume() {
        super.onResume();
        if (myService != null) {
            if (myService.getState() == MyService.STATE_NONE) {
                myService.start();
            }
        }
    }
    private void initChat() {
        outEt = (EditText) findViewById(R.id.edit_text_out);
        sendBtn = (Button) findViewById(R.id.button_send);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView view = (TextView) findViewById(R.id.edit_text_out);
                String message = view.getText().toString();
                sendMessage(message);
            }
        });
        myService = new MyService(this, mHandler);
        outSb = new StringBuffer("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myService != null) {
            myService.stop();
        }
    }

    private void sendMessage(String message) {
        if (myService.getState() != MyService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (message.length() > 0) {
            byte[] send = message.getBytes();
            myService.write(send);
            outSb.setLength(0);
            outEt.setText(outSb);
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Toast.makeText(Main2Activity.this,
                            connectedNameStr + ":  " + readMessage,
                            Toast.LENGTH_LONG).show();
                    break;
                case Constant.MSG_DEVICE_NAME:
                    connectedNameStr = msg.getData().getString(
                            Constant.DEVICE_NAME);
                    Toast.makeText(getApplicationContext(),
                            "已連接到 " + connectedNameStr, Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(
                            MyDeviceListActivity.EXTRA_DEVICE_ADDR);
                    BluetoothDevice device = btAdapter
                            .getRemoteDevice(address);
                    myService.connect(device);
                }
                break;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Intent serverIntent = new Intent(this, MyDeviceListActivity.class);
        startActivityForResult(serverIntent, 1);
        return true;
    }
}
