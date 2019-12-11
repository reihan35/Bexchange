package com.example.bexchange;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ScanBookActivity extends Activity {

    SurfaceView cameraPreview;
    protected void onCreate(Bundle savedInsBundle){
        super.onCreate(savedInsBundle);
        setContentView(R.layout.scan_book);
        cameraPreview = (SurfaceView) findViewById(R.id.camera_preview);
        createCameraSource();
    }

    private void createCameraSource(){
        Log.d("a","JE RENTRE ICI 1 !!!!!!!!!!!!!!!!!");
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).build();
        final CameraSource cameraSource = new CameraSource.Builder(this,barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600,1024)
                .build();

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //if(ActivityCompat.checkSelfPermission(ScanBookActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                try {
                    Log.d("a","JE RENTRE ICI 2 !!!!!!!!!!!!!!!!!");
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                Log.d("a","JE RENTRE ICI 3 !!!!!!!!!!!!!!!!!");

                if(barcodes.size()>0){
                    Intent intent = new Intent();
                    Log.d("b","JE COMPRREEEEENNNNNDSSSS" + barcodes.valueAt(0).displayValue);
                    intent.putExtra("barcode",barcodes.valueAt(0));
                    setResult(CommonStatusCodes.SUCCESS,intent);
                    finish();
                }
            }
        });
    }
}
