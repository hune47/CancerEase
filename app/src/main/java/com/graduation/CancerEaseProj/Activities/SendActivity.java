package com.graduation.CancerEaseProj.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.graduation.CancerEaseProj.R;
import com.graduation.CancerEaseProj.Utilities.DataTransfer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Set;

public class SendActivity extends AppCompatActivity {
    private static final String TAG = "CancerEaseLog: sendB";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int STATE_LISTENING = 1;
    private static final int STATE_CONNECTING = 2;
    private static final int STATE_CONNECTED = 3;
    private static final int STATE_CONNECTION_FAILED = 4;
    private static final int STATE_MESSAGE_RECEIVED = 5;

    private Uri uri;
    private ArrayList<String> arrayList;
    private ProgressDialog progressDialog;
    private ArrayList<String> devicesNameList;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice device;
    private BluetoothDevice[] btArray;
    private ListView listView;
    private Button discoverDevicesBtn, sendBtn;
    private  ArrayAdapter<String> arrayAdapter;
    private TextView msgTxt;
    private EditText msgEdt;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        //uri = getIntent().getData();
        initializeItems();
        CheckBluetooth();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeItems(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.title_please_waite));
        progressDialog.setMessage(getString(R.string.search_for_devices));
        progressDialog.setCancelable(false);

        arrayList = new ArrayList<>();
        devicesNameList = new ArrayList<>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listView = findViewById(R.id.listView);
        sendBtn =findViewById(R.id.send_btn);
        msgTxt=findViewById(R.id.msg_txt);
        msgEdt=findViewById(R.id.msg_edt);
        discoverDevicesBtn = findViewById(R.id.button_discover_devices);
        discoverDevicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothAdapter.startDiscovery();
                progressDialog.show();
                //populateListView();
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void populateListView() {
        arrayList.clear();
        for (BluetoothDevice device : bluetoothAdapter.getBondedDevices())
            arrayList.add(device.getName() + System.lineSeparator() + device.getAddress());

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = listView.getItemAtPosition(position);
                String[] deviceInfo = listItem.toString().split(System.lineSeparator());
                if (deviceInfo.length == 2) {
                    device = bluetoothAdapter.getRemoteDevice(deviceInfo[1]);
                    Log.i(TAG,"device: " + device);
                   /* try {
                        new DataTransfer(getContentResolver().openInputStream(uri),
                                device, getApplicationContext()).execute();
                    } catch (FileNotFoundException e) { }*/
                }
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void searchBluetoothDevices(){
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(healthDataReceiver,intentFilter);
        //arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
        /*listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        progressDialog.dismiss();*/
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void CheckBluetooth(){
        if (bluetoothAdapter == null){
            Toast.makeText(this, "جهاز البلوتوث غير مدعوم!", Toast.LENGTH_SHORT).show();
        }else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            //populateListView();
            searchBluetoothDevices();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    BroadcastReceiver healthDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int index = 0;
            Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
            String[] strings = new String[bt.size()];
            if (bt.size()>0){
                if (BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //arrayList.add(device.getName());
                    //arrayAdapter.notifyDataSetChanged();
                    btArray[index] = device;
                    strings[index] = device.getName();
                    index++;
                }
                arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, strings);
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                });
            }
            progressDialog.dismiss();
        }
    };
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
