package com.example.peter.mybluetooth.View;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.example.peter.mybluetooth.Unit.DeviceAdapter;
import com.example.peter.mybluetooth.R;

/**
 * Created by linweijie on 2/24/16.
 */
public class DeviceListDialog {

    public interface clickListener{
      void scan();
    }

    private Activity activity;
    private Dialog dialog;

    public DeviceListDialog(Activity activity){
        this.activity = activity;
    }

    public void init(BluetoothDevice[] paired, BluetoothDevice[] newDevices, final clickListener listener){

        dialog = new Dialog(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View layout = inflater.inflate(R.layout.dialog_devicelist, null);

        ListView pairedList = (ListView)layout.findViewById(R.id.paired_devices);
        ListView newDeviceList = (ListView)layout.findViewById(R.id.new_devices);
        Button buttonScan = (Button)layout.findViewById(R.id.button_scan);

        pairedList.setAdapter(new DeviceAdapter(activity, paired, new DeviceAdapter.itemClickListener() {
            @Override
            public void itemClick(BluetoothDevice device) {
                // TODO 建立連結
            }
        }));
        newDeviceList.setAdapter(new DeviceAdapter(activity, newDevices, new DeviceAdapter.itemClickListener() {
            @Override
            public void itemClick(BluetoothDevice device) {
                // TODO 進行配對後，建立連結
            }
        }));

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.scan();
            }
        });

        dialog.setContentView(layout);
    }

    public void show(){
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }
}
