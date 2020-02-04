package com.example.bexchange;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
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

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DashboardActivity2 extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnMarkerClickListener{

    private AppBarConfiguration mAppBarConfiguration;
    private FrameLayout log_out;
    private FirebaseAuth mAuth;
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard2);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setTitle("Bexchange");
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
                scanBarCode(view);
                /*
                Intent intent = new Intent(DashboardActivity2.this, AddBookActivity.class);
                startActivity(intent);

                 */
               /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }

        });
        /*
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
*/
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
            LatLng us = new LatLng(getLocation().getLatitude(),getLocation().getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(us, 13F));
            // Set a preference for minimum and maximum zoom.

            //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(, 15F));

            final Double posotionLat = this.getLocation().getLatitude();
            final Double positionLog = this.getLocation().getLongitude();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d("a","AU MOIINNS");
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("a", document.getId() + " => " + document.getData());
                                    Double Lat = (Double) document.get("Lat");
                                    Double Long = (Double) document.get("Long");
                                    int nb_books = document.get("nb_books", Integer.class);
                                    Map<String, Object> data = document.getData();
                                    Log.d("233", data.toString());
                                    Log.d("2",document.get("books")+"");
                                    LatLng p1 = new LatLng(Lat,Long);
                                    if (nb_books > 0 && CalculationByDistance(p1,(new LatLng(posotionLat,positionLog))) < 200 && !document.getId().equals(mAuth.getCurrentUser().getEmail()) ) { //we need to change it to 0.05
                                        mMap.addMarker(new MarkerOptions().position(p1).title("" + document.get("name")).snippet(document.getId()));
                                        Log.d("a", "LA DISTANCE  PPPPPPPPPPPPPPPPPPPPPPPPP");
                                    }
                                    /*
                                    if (document.getId().equals(mAuth.getCurrentUser().getEmail())){
                                        mMap.addMarker(new MarkerOptions().position(p1).title(""+document.get("name")).snippet("My home"));
                                        //Log.d("a","LA DISTANCE  PPPPPPPPPPPPPPPPPPPPPPPPP");
                                    }*/

                                }
                            } else {
                                Log.w("what", "Error getting documents.", task.getException());
                            }
                        }
                    });

            googleMap.setOnMarkerClickListener(this);

        }

        //LatLng paris = new LatLng(48.864716, 2.349014);
        // mMap.addMarker(new MarkerOptions().position(paris).title("Marker in Paris"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(paris, 15F));
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Log.d("a","LA DISTANCE  AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Intent actIntent = new Intent(DashboardActivity2.this,ListOfBookActivity.class);
        actIntent.putExtra("user",marker.getSnippet());
        startActivity(actIntent);
        return true;
    }
    
    /*public ArrayList<LatLng> SearchUsersWhere(){
       // Log.d("a","MAIS WAAAATTTT THEEE FUUUUUCKKKK");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
            final ArrayList<LatLng>  allItems = new ArrayList<>();
        Log.d("a","!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!********************");
        db.collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d("a","AU MOIINNS");
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("a", document.getId() + " => " + document.getData());
                                    Double Lat = (Double) document.get("Lat");
                                    Double Long = (Double) document.get("Long");
                                    LatLng p1 = new LatLng(Lat,Long);
                                    if (CalculationByDistance(p1,(new LatLng(48.87,2.32))) < 4) { //we need to change it to 0.05
                                        Log.d("a","LA DISTANCE  PPPPPPPPPPPPPPPPPPPPPPPPP");
                                        allItems.add(p1);
                                        Log.d("a","ET BAH OUI "+allItems.get(0));
                                    }
                                }
                            } else {
                                Log.w("what", "Error getting documents.", task.getException());
                            }
                        }
                    });
        Log.d("a","ET BAH OUI 2222222222 "+allItems.get(0));
        return allItems;
        }*/

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }


    public void addMarkerFromLatLongList(ArrayList<LatLng> latlongs){
        for (int i=0; i<latlongs.size();i++){
            mMap.addMarker(new MarkerOptions().position(latlongs.get(i)));
        }
    }

    public void onMyLocationClick(@NonNull Location location) {
        location.getLatitude();
        location.getLongitude();
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

            case R.id.nav_logout:
                mAuth.signOut();
                Intent intent = new Intent(DashboardActivity2.this, LoginActivity.class);
                startActivity(intent);
                return true;

            case R.id.nav_my_books:
                Intent actIntent = new Intent(DashboardActivity2.this,ListOfMyBooksActivity.class);
                actIntent.putExtra("user", mAuth.getCurrentUser().getEmail());
                startActivity(actIntent);
                
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

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(this);
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
        actIntent.putExtra("user", mAuth.getCurrentUser().getEmail());
        startActivity(actIntent);
    }

    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private String isbn;



    public void scanBarCode(View v){
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
        else {
            Intent intent = new Intent(this, ScanBookActivity.class);
            startActivityForResult(intent, 0);
        }
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
            if(result == null){
                return;
            }
            Log.d("JSON", "i am here2");
            submitBook(result);
        }
    }

    public void submitBook(JSONObject book){
        Log.d("JSON", "i am here3");
        Intent intent = new Intent(DashboardActivity2.this,SubmitBookActivity.class);
        intent.putExtra("book", book.toString());
        intent.putExtra("isbn", isbn);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==0){
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if(data!=null){
                    final Barcode barcode = data.getParcelableExtra("barcode");
                    Log.d("JSON", "i am here");
                    isbn = barcode.displayValue;

                    new RequestBookInfo().execute(barcode);

                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
