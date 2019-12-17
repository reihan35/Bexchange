package com.example.bexchange.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.bexchange.DashboardActivity2;
import com.example.bexchange.LoginActivity;
import com.example.bexchange.R;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private Button logOut;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("s","!!!!!!!!!!!!!!!!!!!!!JE RENTREEEEEEEEEEEEEEEE!!!!!!!!!!!!!!!!!!!!!!");

        View v = inflater.inflate(R.layout.fragment_share, container, false);
        mAuth = FirebaseAuth.getInstance();
        logOut = v.findViewById(R.id.logout);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Log.d("s","!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }


        //log_out = findViewById(R.id.nav_share);u

}
