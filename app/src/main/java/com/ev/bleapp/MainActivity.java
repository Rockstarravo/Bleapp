package com.ev.bleapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ev.bleapp.R;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PERMISSIONS = 2;

    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<String> deviceList = new ArrayList<>();
    private ArrayAdapter<String> deviceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate called");

        ListView listView = findViewById(R.id.deviceList);
        TextView emptyTextView = findViewById(R.id.emptyTextView);
        listView.setEmptyView(emptyTextView);

        deviceListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceList);
        listView.setAdapter(deviceListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedDevice = deviceList.get(position);
                Log.d(TAG, "Selected device: " + selectedDevice);

                // Create an Intent to start the new activity
                Intent intent = new Intent(MainActivity.this, FieldsActivity.class);
                // Pass the selected device information to the new activity
                intent.putExtra("DEVICE_INFO", selectedDevice);
                startActivity(intent);
            }
        });

        Button refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(v -> {
            Log.d(TAG, "Refresh button clicked");
            checkBluetoothAndPermissions();
        });

        initializeBluetooth();
    }

    private void initializeBluetooth() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            Log.e(TAG, "Device doesn't support Bluetooth");
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Device doesn't support Bluetooth");
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        else {
            Toast.makeText(this, "Bluetooth is supported on this device", Toast.LENGTH_LONG).show();
        }


        checkBluetoothAndPermissions();
    }

    private void checkBluetoothAndPermissions() {
        if (!bluetoothAdapter.isEnabled()) {
            Log.d(TAG, "Bluetooth is not enabled, requesting to enable");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Log.d(TAG, "Bluetooth is enabled");
            checkBluetoothState();
            checkPermissionsAndStartDiscovery();
        }
    }

    private void checkBluetoothState() {
        Log.d(TAG, "Checking Bluetooth state");
        int state = bluetoothAdapter.getState();
        switch (state) {
            case BluetoothAdapter.STATE_OFF:
                Log.d(TAG, "Bluetooth is off");
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                Log.d(TAG, "Bluetooth is turning on");
                break;
            case BluetoothAdapter.STATE_ON:
                Log.d(TAG, "Bluetooth is on");
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                Log.d(TAG, "Bluetooth is turning off");
                break;
        }
    }

    private void checkPermissionsAndStartDiscovery() {
        Log.d(TAG, "Checking permissions");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Requesting BLUETOOTH_SCAN and BLUETOOTH_CONNECT permissions");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT},
                        REQUEST_PERMISSIONS);
                return;
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Requesting ACCESS_FINE_LOCATION permission");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSIONS);
                return;
            }
        }
        Log.d(TAG, "All required permissions granted");
        getPairedDevices();
        new Handler().postDelayed(this::startBluetoothDiscovery, 500); // Delay for 1 second
    }

    private void getPairedDevices() {
        Log.d(TAG, "Getting paired devices");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "BLUETOOTH_CONNECT permission not granted");
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d(TAG, "Paired device: " + deviceName + " - " + deviceHardwareAddress);
            }
        } else {
            Log.d(TAG, "No paired devices found");
        }
    }

    private void startBluetoothDiscovery() {
        Log.d(TAG, "Starting Bluetooth discovery");
        if (bluetoothAdapter.isDiscovering()) {
            Log.d(TAG, "Cancelling ongoing discovery");
            bluetoothAdapter.cancelDiscovery();
        }
        deviceList.clear();
        deviceListAdapter.notifyDataSetChanged();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        Log.d(TAG, "BroadcastReceiver registered");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "BLUETOOTH_SCAN permission not granted");
            Toast.makeText(this, "BLUETOOTH_SCAN permission not granted", Toast.LENGTH_LONG).show();
            return;
        }

        boolean discoveryStarted = bluetoothAdapter.startDiscovery();
        Log.d(TAG, "Discovery started: " + discoveryStarted);
        if (!discoveryStarted) {
            Log.e(TAG, "Failed to start discovery");
            Toast.makeText(this, "Failed to start Bluetooth discovery", Toast.LENGTH_LONG).show();
            // Additional error handling
            if (!bluetoothAdapter.isEnabled()) {
                Log.e(TAG, "Bluetooth is not enabled");
                Toast.makeText(this, "Bluetooth is not enabled", Toast.LENGTH_LONG).show();
            } else {
                Log.e(TAG, "Unknown error starting discovery");
                Toast.makeText(this, "Unknown error starting Bluetooth discovery", Toast.LENGTH_LONG).show();
            }
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "BroadcastReceiver onReceive: " + action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d(TAG, "Device found: " + deviceName + " - " + deviceHardwareAddress);
                String deviceInfo = (deviceName != null ? deviceName : "Unknown") + " - " + deviceHardwareAddress;
                if (!deviceList.contains(deviceInfo)) {
                    deviceList.add(deviceInfo);
                    deviceListAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Device added to list: " + deviceInfo);
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
        unregisterReceiver(receiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: " + requestCode);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permissions granted");
                startBluetoothDiscovery();
            } else {
                Log.e(TAG, "Permissions denied");
                Toast.makeText(this, "Permissions are required to scan for Bluetooth devices", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + requestCode + ", " + resultCode);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Bluetooth enabled successfully");
                checkPermissionsAndStartDiscovery();
            } else {
                Log.e(TAG, "Bluetooth not enabled");
                Toast.makeText(this, "Bluetooth is required for this app", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}