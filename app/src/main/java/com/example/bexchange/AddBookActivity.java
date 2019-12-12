package com.example.bexchange;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.bexchange.R;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class AddBookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
    }

    public void scanBarCode(View v){
        Intent intent = new Intent(this,ScanBookActivity.class);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==0){
            if (resultCode == CommonStatusCodes.SUCCESS) {
                TextView tvId = (TextView) findViewById(R.id.barcode_result);

                if(data!=null){
                    final Barcode barcode = data.getParcelableExtra("barcode");
                    tvId.setText("Barcode Value : " + barcode.displayValue);

                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try  {
                                HttpURLConnection urlConnection = null;
                                java.net.URL url = new URL("https://www.googleapis.com/books/v1/volumes?q=isbn:" + barcode.displayValue);
                                urlConnection = (HttpURLConnection) url.openConnection();
                                urlConnection.setRequestMethod("GET");
                                urlConnection.setReadTimeout(10000 /* milliseconds */ );
                                urlConnection.setConnectTimeout(15000 /* milliseconds */ );
                                urlConnection.setDoOutput(true);
                                urlConnection.connect();

                                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                                StringBuilder sb = new StringBuilder();

                                String line;
                                while ((line = br.readLine()) != null) {
                                    sb.append(line + "\n");
                                }
                                br.close();

                                String jsonString = sb.toString();
                                System.out.println("JSON: " + jsonString);
                                JSONObject books = new JSONObject(jsonString);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();

                }
                else{
                    tvId.setText("No barcode FOUND");
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
