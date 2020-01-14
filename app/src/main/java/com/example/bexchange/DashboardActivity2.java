package com.example.bexchange;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity2 extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    private FrameLayout log_out;
    private FirebaseAuth mAuth;
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard2);
        Toolbar toolbar = findViewById(R.id.toolbar);

        defaultLocation.setLatitude(48.864716);
        defaultLocation.setLongitude(2.349014);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        mAuth = FirebaseAuth.getInstance();
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
        }
        getLocation();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity2.this, AddBookActivity.class);
                startActivity(intent);
               /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }

        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        /*private Button logOut = findViewById(R.id.logout);

        //log_out = findViewById(R.id.nav_share);
        logOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                mAuth.signOut();
                Intent intent = new Intent(DashboardActivity2.this, LoginActivity.class);
                startActivity(intent);
            }
        });*/


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
            // Set a preference for minimum and maximum zoom.

            //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(, 15F));

        }

        //LatLng paris = new LatLng(48.864716, 2.349014);
       // mMap.addMarker(new MarkerOptions().position(paris).title("Marker in Paris"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(paris, 15F));
    }

    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    public boolean onMyLocationButtonClick() {
        Location loc = getLocation();
        LatLng userLoc = new LatLng(loc.getLatitude(), loc.getLongitude());
        mMap.addMarker(new MarkerOptions().position(userLoc).title("Marker user"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 15F));
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard_activity2, menu);

               /* View v = findViewById(R.id.nav_logout);
                v.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        mAuth.signOut();
                        Intent intent = new Intent(DashboardActivity2.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.nav_logout:
                mAuth.signOut();
                Intent intent = new Intent(DashboardActivity2.this, LoginActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        ImageView profilephoto = findViewById(R.id.imageView);
        //Uri photoUrl = user.getPhotoUrl();
        //System.out.println("JE SUIS LA PHOTO DE PROFIL DU USER " + user.getPhotoUrl());
        profilephoto.setImageResource(R.drawable.woman); //change it with user's profile photo
       // profilephoto.setImageURI(photoUrl);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "your message", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //Get location
    private static Location defaultLocation = new Location("");
    public Location getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            return defaultLocation;
        }
        Location myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (myLocation == null)
        {
            myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        }
        return myLocation;
    }

    public void listBookUser(View view) {
        Intent actIntent = new Intent(DashboardActivity2.this,ListOfBookActivity.class);
        actIntent.putExtra("user", "matthieueyraud47@gmail.com");
        startActivity(actIntent);
    }
}
