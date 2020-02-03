package com.example.bexchange;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddBookActivity extends AppCompatActivity {

    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private String isbn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
    }





    public void scanBarCode(View v){
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
        Intent intent = new Intent(this,ScanBookActivity.class);
        startActivityForResult(intent,0);
    }


    private class RequestBookInfo extends AsyncTask<Barcode, Integer, JSONObject> {
        protected JSONObject doInBackground(Barcode... barcodes) {
            Log.d("JSON", "i am here1");
            Barcode barcode = barcodes[0];
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

                return books;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            Log.d("JSON", "i am here2");
            submitBook(result);
        }
    }

    public void submitBook(JSONObject book){
        Log.d("JSON", "i am here3");
        Intent intent = new Intent(AddBookActivity.this,SubmitBookActivity.class);
        intent.putExtra("book", book.toString());
        intent.putExtra("isbn", isbn);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==0){
            if (resultCode == CommonStatusCodes.SUCCESS) {
                TextView tvId = (TextView) findViewById(R.id.barcode_result);

                if(data!=null){
                    final Barcode barcode = data.getParcelableExtra("barcode");
                    Log.d("JSON", "i am here");
                    tvId.setText("Barcode Value : " + barcode.displayValue);
                    isbn = barcode.displayValue;

                    new RequestBookInfo().execute(barcode);

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
