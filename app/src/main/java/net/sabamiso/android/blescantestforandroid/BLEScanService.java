package net.sabamiso.android.blescantestforandroid;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BLEScanService extends Service {

    BluetoothManager mBluetoothManager;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothLeScanner mBluetoothScanner;

    private final String TAG = getClass().getSimpleName();

    void log(String msg) {
        Log.d(TAG, msg);
    }
    void err(String msg) {
        Log.e(TAG, msg);
    }

    ScanResultListener listener;

    ///////////////////////////////////////////////////////////////////////
    //
    // binder
    //
    private final IBinder mBinder = new BLEScanServiceBinder();

    public class BLEScanServiceBinder extends Binder {
        public BLEScanService getService() {
            return BLEScanService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    ///////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate() {
        log("onCreate");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothScanner = mBluetoothAdapter.getBluetoothLeScanner();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log("onStartCommand() : flags=" + flags + ", startId=" + startId + ", intent=" + intent);

        if (intent != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Toast.makeText(getApplicationContext(), "BLEScanService.onStartCommand() : ACTION_BOOT_COMPLETED", Toast.LENGTH_LONG).show();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        log("onDestroy");
    }

    ///////////////////////////////////////////////////////////////////////

    void startScan() {
        mBluetoothScanner.startScan(mScanCallback);
    }

    void stopScan() {
        log("stop() : stopLeScan()");
        mBluetoothScanner.stopScan(mScanCallback);
    }

    void setScanResultListener(ScanResultListener listener) {
        this.listener = listener;
    }

    final ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (listener != null) {
                listener.onScanResult(result);
            }
        }
    };
}
