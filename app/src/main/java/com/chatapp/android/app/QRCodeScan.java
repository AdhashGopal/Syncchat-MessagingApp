/*
package com.chatapp.android.app;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.chatapp.android.R;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;
import com.chatapp.android.core.CoreActivity;


*/
/**
 * created by  Adhash Team on 11/11/2016.
 *//*


public class QRCodeScan extends CoreActivity implements QRCodeReaderView.OnQRCodeReadListener {

    private QRCodeReaderView qrCodeReaderView;
    private AvnNextLTProRegTextView tv_header;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode_scan);
        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.scanner);
        tv_header = (AvnNextLTProRegTextView) findViewById(R.id.texthead);
        qrCodeReaderView.setOnQRCodeReadListener(this);
        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);
        setTitle("Scan code");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
//        initScanListener();
        tv_header.setText("");
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        qrCodeReaderView.stopCamera();
        Log.d("QRScanner", text);
        Intent intent = new Intent();
        intent.putExtra("QRData", text);
        setResult(RESULT_OK, intent);
        finish();
    }


//    public void initScanListener() {
//
//        scanner.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener() {
//            @Override
//            public void onScannerStarted(ScannerLiveView scanner) {
//            }
//
//            @Override
//            public void onScannerStopped(ScannerLiveView scanner) {
//            }
//
//            @Override
//            public void onScannerError(Throwable err) {
//                Toast.makeText(QRCodeScan.this, "Error occurred", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCodeScanned(String data) {
//                scanner.stopScanner();
//                Log.d("QRScanner",data);
//                Intent intent = new Intent();
//                intent.putExtra("QRData", data);
//                setResult(RESULT_OK, intent);
//                finish();
//            }
//        });
//
//    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }

    public void onDestroy() {
        super.onDestroy();
        qrCodeReaderView.stopCamera();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}*/
