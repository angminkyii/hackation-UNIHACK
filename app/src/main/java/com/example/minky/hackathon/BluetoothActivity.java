package com.example.minky.hackathon;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {

    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    public ArrayList<BluetoothDevice> bluetoothDevices;
    ListView lv;
    Button scan;
    Button unpairAll;
    ArrayAdapter<String> arrayAdapter;
    BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name and the MAC address of the object to the arrayAdapter
                Log.d("Receiver","Broadcasting");
                if(device!=null) {
                    if (avoidMatching(device)) {
                        Log.d("Receiver","Adding " + device.getName());
                        if(device.getName()!= null) {
                            arrayAdapter.add(device.getName());
                            bluetoothDevices.add(device);
                        }else{
                            arrayAdapter.add("null");
                            bluetoothDevices.add(device);
                        }
                    }
                }
            }
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //Do something if connected
                Toast.makeText(getApplicationContext(), "BT Connected", Toast.LENGTH_SHORT).show();
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Do something if disconnected
                Toast.makeText(getApplicationContext(), "BT Disconnected", Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        initalise();
        checkBTState();
        find();
        listener();
    }

    public void initalise(){
        bluetoothDevices = new ArrayList<>();
        BA = BluetoothAdapter.getDefaultAdapter();
        lv = (ListView)findViewById(R.id.listView2);
        scan = (Button)findViewById(R.id.button2);
        unpairAll = (Button)findViewById(R.id.button3);
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, 0);
        lv.setAdapter(arrayAdapter);
        pairedDevices = BA.getBondedDevices();
        for(BluetoothDevice bt : pairedDevices) {
            arrayAdapter.add(bt.getName());
            bluetoothDevices.add(bt);
        }
        Toast.makeText(getApplicationContext(),"Showing Paired Devices",Toast.LENGTH_SHORT).show();
    }

    public void find() {
        if (BA.isDiscovering()) {
            // the button is pressed when it discovers, so cancel the discovery
            BA.cancelDiscovery();
            //unregisterReceiver(bReceiver);
        }
        else {
            BA.startDiscovery();
            registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
            IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
            IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            this.registerReceiver(bReceiver, filter1);
            this.registerReceiver(bReceiver, filter2);
            this.registerReceiver(bReceiver, filter3);
        }
    }

    public void listener(){
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name  =bluetoothDevices.get(position).getName();
                String add = bluetoothDevices.get(position).getAddress();
                Toast.makeText(getApplicationContext(),name+"\n"+add,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("MACADDRESS",add);
                startActivity(intent);
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find();
            }
        });

        unpairAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unpairAll();
            }
        });
    }

    public boolean avoidMatching(BluetoothDevice bluetoothDevice){
        int count = 0;

        for(int i=0;i<bluetoothDevices.size();i++){
            printError("Comparing "+bluetoothDevice.getName()+" with "+bluetoothDevices.get(i).getName());
            if(bluetoothDevices.get(i).getName().equals(bluetoothDevice.getName()) && bluetoothDevices.get(i).getAddress().equals(bluetoothDevice.getAddress())){
                count = count + 1;
            }
        }
        if(count == 0) {
            return true;
        }
        return false;
    }
    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(BA==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (BA.isEnabled()) {
                Log.d("BT", "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        if(BA.isDiscovering()) {
            BA.cancelDiscovery();
        }
        //unregisterReceiver(bReceiver);
        super.onDestroy();
    }

    public void printError(String message){
        Log.d("ADD BT",message);
    }

    public void unpairAll() {
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                try {
                    Method m = device.getClass()
                            .getMethod("removeBond", (Class[]) null);
                    m.invoke(device, (Object[]) null);
                    //Toast.makeText(getApplicationContext(),"Unpairing "+device.getName(),Toast.LENGTH_SHORT).show();
                    pairedDevices.remove(device);
                } catch (Exception e) {
                }
            }
        }
        if(pairedDevices.size()==0) {
            Toast.makeText(getApplicationContext(), "Unpairing successful", Toast.LENGTH_LONG).show();
        }
    }
}
