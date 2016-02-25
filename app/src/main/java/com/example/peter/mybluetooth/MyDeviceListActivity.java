package com.example.peter.mybluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

public class MyDeviceListActivity extends AppCompatActivity {

    public static String EXTRA_DEVICE_ADDR = "device_address";

    private BluetoothAdapter myBtAdapter;
    private ArrayAdapter<String> myAdapterPaired;
    private ArrayAdapter<String> myAdapterNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_my_device_list);

        setResult(Activity.RESULT_CANCELED);

        Button scanBtn = (Button) findViewById(R.id.button_scan);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);// ʹ��ť���ɼ�
            }
        });

        myAdapterPaired = new ArrayAdapter<String>(this,
                R.layout.device_name);// ����Ե�
        myAdapterNew = new ArrayAdapter<String>(this,
                R.layout.device_name);// �·��ֵ�

        ListView lvPaired = (ListView) findViewById(R.id.paired_devices);
        lvPaired.setAdapter(myAdapterPaired);
        lvPaired.setOnItemClickListener(mDeviceClickListener);

        ListView lvNewDevices = (ListView) findViewById(R.id.new_devices);
        lvNewDevices.setAdapter(myAdapterNew);
        lvNewDevices.setOnItemClickListener(mDeviceClickListener);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        myBtAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = myBtAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                myAdapterPaired.add(device.getName() + "\n"
                        + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired)
                    .toString();
            myAdapterPaired.add(noDevices);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myBtAdapter != null) {// ȷ�����������豸
            myBtAdapter.cancelDiscovery();
        }
        // ȡ��㲥������
        this.unregisterReceiver(mReceiver);
    }

    // ʹ�����������������豸�ķ���
    private void doDiscovery() {
        // �ڱ�������ʾ���������ı�־
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);
        // ��ʾ�����������豸�ĸ�����
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
        if (myBtAdapter.isDiscovering()) {// �������������ȡ�������
            myBtAdapter.cancelDiscovery();
        }
        myBtAdapter.startDiscovery();// ��ʼ����
    }

    // �б����豸����ʱ�ļ�����
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            myBtAdapter.cancelDiscovery();// ȡ������
            // ��ȡ�豸��MAC��ַ
            String msg = ((TextView) v).getText().toString();
//            String address = msg.substring(msg.length() - 17);
            String address = msg.substring(msg.length());
            // ��������MAC��ַ��Intent
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDR, address);
            // �豸����˳�Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    // �������������豸��BroadcastReceiver
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // ����ҵ��豸
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // ��Intent�л�ȡBluetoothDevice����
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // ���û����ԣ����豸�������豸�б�
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    myAdapterNew.add(device.getName() + "\n"
                            + device.getAddress());
                }
                // ��������ɺ󣬸ı�Activity�ı���
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (myAdapterNew.getCount() == 0) {
                    String noDevices = getResources().getText(
                            R.string.none_found).toString();
                    myAdapterNew.add(noDevices);
                }
            }
        }
    };
}
