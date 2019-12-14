package com.example.bexchange;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button registerBtn, loginBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        initializeViews();


        //HttpGet httpget= new HttpGet("https://www.googleapis.com/books/v1/volumes?q=isbn:9782253150978");
        //HttpGet httpget= new HttpGet("www.google.com");
        mAuth = FirebaseAuth.getInstance();
       /* if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, DashboardActivity2.class));
            finish();
        }*/

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);

        /*registerBtn.setOnClickListener(new View.OnClickListener() {
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
        });*/
    }



    private void initializeViews() {
        registerBtn = findViewById(R.id.register);
        loginBtn = findViewById(R.id.login);
    }
}