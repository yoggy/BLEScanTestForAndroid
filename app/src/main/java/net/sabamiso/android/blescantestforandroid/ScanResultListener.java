package net.sabamiso.android.blescantestforandroid;

import android.bluetooth.le.ScanResult;

public interface ScanResultListener {
    public void onScanResult(ScanResult result);
}
