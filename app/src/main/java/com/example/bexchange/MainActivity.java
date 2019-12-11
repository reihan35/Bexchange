package com.example.bexchange;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button registerBtn, loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();


        //HttpGet httpget= new HttpGet("https://www.googleapis.com/books/v1/volumes?q=isbn:9782253150978");
        //HttpGet httpget= new HttpGet("www.google.com");
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    HttpURLConnection urlConnection = null;
                    URL url = new URL("https://www.googleapis.com/books/v1/volumes?q=isbn:9782253150978");
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

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }



    private void initializeViews() {
        registerBtn = findViewById(R.id.register);
        loginBtn = findViewById(R.id.login);
    }
}