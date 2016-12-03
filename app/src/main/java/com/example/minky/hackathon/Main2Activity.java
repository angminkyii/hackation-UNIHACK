package com.example.minky.hackathon;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    Button addSys;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initialise();
        listeners();
    }

    public void initialise(){
        listView = (ListView)findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,retrieveSystem());
        listView.setAdapter(arrayAdapter);
        addSys = (Button)findViewById(R.id.button);
    }

    public void listeners(){
        addSys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),BluetoothActivity.class);
                startActivity(intent);
            }
        });
    }

    public List<String> retrieveSystem(){
        //database to store system
        List<String> listOfSystem = new ArrayList<>();

        //Do something with it, Firebase or SQL
        return listOfSystem;
    }
}
