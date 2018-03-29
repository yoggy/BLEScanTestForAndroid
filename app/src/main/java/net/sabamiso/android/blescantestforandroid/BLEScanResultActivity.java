package net.sabamiso.android.blescantestforandroid;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class BLEScanResultActivity extends AppCompatActivity implements ScanResultListener {
    private final String TAG = getClass().getSimpleName();

    void log(String msg) {
        Log.d(TAG, msg);
    }

    Handler handler = new Handler();

    BLEScanService ble_scan_service;
    public static final int BLE_SCAN_REQUEST_CODE = 123;

    Button buttonStart;
    Button buttonStop;
    TextView textViewMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blescan_result);

        textViewMessage = (TextView)findViewById(R.id.textViewMessage);

        buttonStart = (Button)findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ble_scan_service.startScan();
            }
        });

        buttonStop = (Button)findViewById(R.id.buttonStop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ble_scan_service.stopScan();
            }
        });
    }

    @Override
    public void onStart() {
        message("onStart()");
        super.onStart();

        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, BLE_SCAN_REQUEST_CODE);
        }
        else {
            bindBLEScanService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == BLE_SCAN_REQUEST_CODE) {
            if (verifyPermissions(grantResults) == true) {
                bindBLEScanService();
            }
            else {
                Toast.makeText(this, "ACCESS_COARSE_LOCATIONを許可してください", Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean verifyPermissions(int [] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    void bindBLEScanService() {
        if (ble_scan_service == null) {
            Intent bankmemory_intent = new Intent(getApplicationContext(), BLEScanService.class);
            bindService(bankmemory_intent, service_connection, BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onResume() {
        message("onResume()");
        super.onResume();
    }

    @Override
    protected void onPause() {
        message("onPause()");
        super.onPause();
    }

    protected void onDestroy() {
        log("onDestroy()");
        super.onDestroy();

        if (ble_scan_service != null) {
            unbindService(service_connection);
            ble_scan_service = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    final ServiceConnection service_connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            message("mBLEScanServiceConnection.onServiceConnected()");
            ble_scan_service = ((BLEScanService.BLEScanServiceBinder) service).getService();
            ble_scan_service.setScanResultListener(BLEScanResultActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            message("mBLEScanServiceConnection.onServiceDisconnected()");
            ble_scan_service = null;
        }
    };

    ////////////////////////////////////////////////////////////////////////////

    @Override
    public void onScanResult(ScanResult result) {
        BluetoothDevice device = result.getDevice();
        ScanRecord scan_record = result.getScanRecord();

        // ibeaconの場合、名前が入っていないことがあるので注意
        String name = device.getName();
        String msg = "";

        // AD type=0xff, ManufactureID=0x1234
        byte [] manufacture_data = scan_record.getManufacturerSpecificData(0x1234);
        if (manufacture_data != null) {
            for (int i = 0; i < manufacture_data.length; ++i) {
                msg += String.format("%02X", manufacture_data[i]);
                if (i < manufacture_data.length - 1) msg += ",";
            }
        }

        message(name + " : manufacture_data=" + msg);
    }

    ////////////////////////////////////////////////////////////////////////////
    LinkedList<String> messages = new LinkedList<String>();

    void message(final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                messageInner(msg);
            }
        });
    }

    void messageInner(final String msg) {
        log(msg);

        messages.addLast(msg);
        if (messages.size() > 14) {
            messages.removeFirst();
        }

        String str = "";
        for (int i = 0; i < messages.size(); ++i) {
            str += messages.get(i);

            if (i < messages.size() - 1) {
                str += "\n";
            }
        }
        textViewMessage.setText(str);
    }
}
