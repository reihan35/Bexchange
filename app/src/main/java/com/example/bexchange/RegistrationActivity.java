package com.example.bexchange;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {

    private EditText emailTV, passwordTV;
    private Button regBtn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        initializeUI();

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });
    }

    private void registerNewUser() {
        progressBar.setVisibility(View.VISIBLE);

        String email, password,name;
        email = emailTV.getText().toString();
        password = passwordTV.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }


        Log.d("2","!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + password + " " + email);
    /*
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Please enter your name!", Toast.LENGTH_LONG).show();
            return;
        }
    */

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);

                            Intent intent = new Intent(RegistrationActivity.this, CompleteRegist.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
                            Log.d("test", task.getException().getMessage());
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(Uri.parse("https://www.google.com/url?sa=i&source=images&cd=&ved=2ahUKEwiQ_o3LobDmAhUJ-hQKHdbRBzEQjRx6BAgBEAQ&url=http%3A%2F%2Fwww.kjosbigmouth.com%2F2019%2F09%2Fbest-hd-my-profile-icon-vector-library%2F&psig=AOvVaw2c58W7tCGU2Agi6LAmWKhL&ust=1576245152765478"))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete( Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("1", "User profile updated.");
                        }
                    }
                });

        */

    }

    private void initializeUI() {
        emailTV = findViewById(R.id.email2);
        passwordTV = findViewById(R.id.password);
        regBtn = findViewById(R.id.register);
        progressBar = findViewById(R.id.progressBar);
    }
}
