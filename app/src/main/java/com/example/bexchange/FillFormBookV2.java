package com.example.bexchange;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
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
import androidx.constraintlayout.solver.widgets.Rectangle;
import androidx.core.app.ActivityCompat;
import com.example.bexchange.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FillFormBookV2 extends AppCompatActivity implements SurfaceHolder.Callback, Detector.Processor {

    private SurfaceView cameraView;
    private TextView txtView;
    private CameraSource cameraSource;
    private String txt = "";
    private Lock l = new ReentrantLock();
    private boolean touched = false;
    private SurfaceHolder holder;
    private SurfaceHolder holderTransparent;

    private class Coordinates{
        float x=0, y=0;
    }
    private Coordinates beginCoordinate = new Coordinates();

    private Coordinates endCoordinate = new Coordinates();
    private Rect selectedZone = new Rect();
    private boolean drawRectangle;

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


    View.OnTouchListener onTouchListner = new View.OnTouchListener() {

        public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d("test" , "action_down");
                    drawRectangle = true; // Start drawing the rectangle
                    beginCoordinate.x = event.getX();
                    beginCoordinate.y = event.getY();
                    endCoordinate.x = event.getX();
                    endCoordinate.y = event.getY();
                    v.invalidate(); // Tell View that the canvas needs to be redrawn
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d("test" , "action_move");
                    endCoordinate.x = event.getX();
                    endCoordinate.y = event.getY();
                    DrawFocusRect(beginCoordinate.x, beginCoordinate.y, endCoordinate.x, endCoordinate.y, Color.BLUE);
                    v.invalidate(); // Tell View that the canvas needs to be redrawn
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d("test" , "action_up");
                    // Do something with the beginCoordinate and endCoordinate, like creating the 'final' object
                    drawRectangle = false; // Stop drawing the rectangle
                    DrawFocusRect(beginCoordinate.x, beginCoordinate.y, endCoordinate.x, endCoordinate.y, Color.BLUE);

                    //a gross approximation
                    selectedZone.left = (int)beginCoordinate.x;
                    selectedZone.top = (int) beginCoordinate.y ;
                    selectedZone.right = (int) endCoordinate.x ;
                    selectedZone.bottom = (int) endCoordinate.y ;
                    //DrawRect(selectedZone);
                    touched = true;
                    v.invalidate(); // Tell View that the canvas needs to be redrawn
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("Main Activity", "dqs depen");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_form_book_v3);
        cameraView = findViewById(R.id.CameraView);
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

        cameraView.setOnTouchListener(onTouchListner);

        holder = cameraView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // Create second surface with another holder (holderTransparent)
        SurfaceView transparentView = (SurfaceView)findViewById(R.id.TransparentView);

        holderTransparent = transparentView.getHolder();
        holderTransparent.setFormat(PixelFormat.TRANSPARENT);
        //holderTransparent.addCallback(callBack);
        holderTransparent.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
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
                Rect box = item.getBoundingBox();
                DrawRect(box);
                Log.d("test", item.getValue());

                Log.d("test", "box " + box.toString());
                Log.d("test", "selected zone " + selectedZone.contains(box));
                if(selectedZone.contains(box) || box.contains(selectedZone) || selectedZone.intersect(box) || box.intersect(selectedZone)) {
                    Log.d("test", "adding " + item.getValue());
                    strBuilder.append(item.getValue());
                }
                //strBuilder.append(item.getValue());
                //strBuilder.append("/");
            }
            Log.v("strBuilder.toString()", strBuilder.toString());
            txt = strBuilder.toString();
            resumeOk(null);
            txtView.post(new Runnable() {
                @Override
                public void run() {
                    txtView.setText(strBuilder.toString());
                }
            });
        }
    }

    public void resumeOk(View v){
        Intent data = getIntent();
        data.setData(Uri.parse(txt));
        setResult(RESULT_OK, data);
        //---close the activity---
        finish();
    }




    
    private void DrawFocusRect(float RectLeft, float RectTop, float RectRight, float RectBottom, int color)
    {
        Log.d("test", "drawing rectangle " + RectLeft + " " + RectTop + " " + RectRight + " " + RectBottom);

        Canvas canvas = holderTransparent.lockCanvas();
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        //border's properties
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(4);
        canvas.drawRect(RectLeft, RectTop, RectRight, RectBottom, paint);

        holderTransparent.unlockCanvasAndPost(canvas);
    }

    private void DrawRect(Rect r){
        DrawFocusRect(r.left, r.top, r.right, r.bottom, Color.BLUE);
    }

    protected void onDraw(Canvas canvas) {
        if(drawRectangle) {
            DrawFocusRect(beginCoordinate.x, beginCoordinate.y, endCoordinate.x, endCoordinate.y, Color.BLUE);
        }
    }

}