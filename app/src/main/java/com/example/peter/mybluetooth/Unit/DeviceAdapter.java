package com.example.peter.mybluetooth.Unit;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.peter.mybluetooth.R;

/**
 * Created by linweijie on 2/24/16.
 */
public class DeviceAdapter extends BaseAdapter {

    public interface itemClickListener{
        void itemClick(BluetoothDevice device);
    }

    private Context context;
    private LayoutInflater inflater;
    private BluetoothDevice[] list;
    private itemClickListener listener;

    public DeviceAdapter(Context contexts, BluetoothDevice[] list, itemClickListener listener){
        this.context = contexts;
        this.list = list;
        this.inflater = LayoutInflater.from(contexts);
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int position) {
        return list[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null){
            convertView = inflater.inflate(R.layout.item_device, null);

            holder = new ViewHolder();
            holder.body = (LinearLayout)convertView.findViewById(R.id.ll_body);
            holder.name = (TextView)convertView.findViewById(R.id.tv_name);
            holder.mac = (TextView)convertView.findViewById(R.id.tv_mac);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.itemClick(list[position]);
            }
        });
        holder.name.setText(list[position].getName());
        holder.mac.setText(list[position].getAddress());

        return convertView;
    }

    class ViewHolder{
        public LinearLayout body;
        public TextView name;
        public TextView mac;
    }
}
