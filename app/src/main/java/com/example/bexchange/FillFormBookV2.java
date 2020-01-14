package com.example.bexchange;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.bexchange.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FillFormBookV2 extends AppCompatActivity implements SurfaceHolder.Callback, Detector.Processor {

    private SurfaceView cameraView;
    private TextView txtView;
    private CameraSource cameraSource;
    private String txt = "";
    private Lock l = new ReentrantLock();
    private boolean touched = true;

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    cameraSource.start(cameraView.getHolder());
                } catch (Exception e) {

                }
            }
        }
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("Main Activity", "dqs depen");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_form_book_v3);
        cameraView = findViewById(R.id.surface_view);
        txtView = findViewById(R.id.txtview);
        TextRecognizer txtRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!txtRecognizer.isOperational()) {
            Log.e("Main Activity", "Detector dependencies are not yet available");
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), txtRecognizer)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            cameraView.getHolder().addCallback(this);
            txtRecognizer.setProcessor(this);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},1);
                return;
            }
            cameraSource.start(cameraView.getHolder());
        } catch (Exception e) {
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

    @Override
    public void release() {
    }

    @Override
    public void receiveDetections(Detector.Detections detections) {
        if(touched) {
            touched = false;
            SparseArray items = detections.getDetectedItems();
            Log.d("taille item", "" + items.size());
            final StringBuilder strBuilder = new StringBuilder();
            for (int i = 0; i < items.size(); i++) {
                TextBlock item = (TextBlock) items.valueAt(i);
                strBuilder.append(item.getValue());
                strBuilder.append("/");
                // The following Process is used to show how to use lines & elements as well
                /*
                for (int j = 0; j < items.size(); j++) {
                    TextBlock textBlock = (TextBlock) items.valueAt(j);
                    strBuilder.append(textBlock.getValue());
                    strBuilder.append("/");
                    for (Text line : textBlock.getComponents()) {
                        //extract scanned text lines here
                        Log.v("lines", line.getValue());
                        strBuilder.append(line.getValue());
                        strBuilder.append("/");
                        for (Text element : line.getComponents()) {
                            //extract scanned text words here
                            Log.v("element", element.getValue());
                            strBuilder.append(element.getValue());
                        }
                    }
                }*/
            }
            Log.v("strBuilder.toString()", strBuilder.toString());
            txt = strBuilder.toString();

            txtView.post(new Runnable() {
                @Override
                public void run() {
                    txtView.setText(strBuilder.toString());
                }
            });
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent evt) {
        if (evt.getAction() == MotionEvent.ACTION_DOWN) {
            touched = true;
        }
        return true;
    }

    public void resumeOk(View v){
        Intent data = getIntent();
        data.setData(Uri.parse(txt));
        setResult(RESULT_OK, data);
        //---close the activity---
        finish();
    }
}