package com.example.gestiondefouleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    ImageView Addstorebutton,AddstoreSmallbutton,refrechButton;
    ListView FavouritePlacesLV;
    BottomNavigationView bottomNavigationView;
    ProgressBar ProgressBar;
    private LocationManager locationManager;
    WifiManager wifi ;

    //location request items
    private static final int REQUES_CODE_LOCATION_PERMISSION = 1;
    private static final int HANDLER_DELAY = 1000 * 20 *1  ;
    private static final int START_HANDLER_DELAY = 0;
//my location
    public static double mylocationLat=0.0;
    public static double mylocationLong=0.0;

    //other users locations
    ArrayList locationsList;
    public static Bundle b;
    //

    FirebaseDatabase rootNode;
    DatabaseReference reference,referenceFav,referenceLocation;;
    DatabaseReference Searchreference;
    PlacesAdapter favPlacesAdapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //database
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("places");
        FavouritePlacesLV=findViewById(R.id.FavouritePlacesLV);
        Addstorebutton=findViewById(R.id.Addstorebutton);
        AddstoreSmallbutton=findViewById(R.id.AddstoreSmallbutton);
        refrechButton=findViewById(R.id.refrechButton);
        ProgressBar=findViewById(R.id.ProgressBar);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        WifiManager wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //Authentification:
        mAuth = FirebaseAuth.getInstance();

        //get location every 20seconds
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUES_CODE_LOCATION_PERMISSION
                    );
                } else {
                    getcurrentLocaiton();
                }
                handler.postDelayed(this, HANDLER_DELAY);
            }
        }, START_HANDLER_DELAY);

        //
        final ArrayList<PlacesItem> arrayList = new ArrayList<PlacesItem>();
        favPlacesAdapter = new PlacesAdapter(this, arrayList);
        FavouritePlacesLV.setAdapter(favPlacesAdapter);

        Addstorebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent
                        = new Intent(MainActivity.this,
                        SearchPlacesActivity.class);
                startActivity(intent);
            }
        });
        AddstoreSmallbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent
                        = new Intent(MainActivity.this,
                        SearchPlacesActivity.class);
                startActivity(intent);
            }
        });

        //bottom nav bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()){

                    case R.id.nav_home:

                        break;

                    case R.id.nav_Map:
                        intent=new Intent(MainActivity.this,MapsActivity.class);
                        startActivity(intent);

                        break;

                    case R.id.nav_Distancing:
                        intent=new Intent(MainActivity.this,DistancingActivity.class);
                        startActivity(intent);

                        break;

                    case R.id.nav_history:
                        intent=new Intent(MainActivity.this,HistoryActivity.class);
                        startActivity(intent);

                        break;

                    case R.id.nav_profile:
                        intent=new Intent(MainActivity.this,ProfileActivity.class);
                        startActivity(intent);


                        break;
                }



                return true;
            }
        });
        //delete place from fav list
        FavouritePlacesLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("supprimer un lieu")
                        .setMessage("voulez-vous vraiment supprimer "+favPlacesAdapter.getItem(position).getName()+" du favori?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(MainActivity.this,"élément supprimé avec succès",Toast.LENGTH_LONG).show();
                                referenceFav=rootNode.getReference("FavouritePlaces").child(mAuth.getCurrentUser().getUid()).child(favPlacesAdapter.getItem(position).getName());
                                referenceFav.removeValue();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                return false;

            }
        });

        //Refresh fav list
       refrechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

       final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        // convert the string to a char array
        char[] alphabetArr = alphabet.toCharArray();
        double[] ExmpllongArray=new double[19];
        double[] ExmpllatArray=new double[19];
        //36.3649, 6.6383
       // 36.3650, 6.6382
        //36.3650, 6.6383
       //36.3649, 6.6382

        ExmpllatArray= new double[] {36.36491, 36.36492,36.36493,36.36494,36.36495,36.36496,36.36497,36.36498,36.36499,36.365,36.3651,36.36511
                ,36.36512,36.36513,36.36514,36.36515,36.36516,36.36517,36.36518,36.36519,36.3652,36.36521,36.36522,36.36523,36.36524,36.36525,36.36526};
        ExmpllongArray= new double[]{6.6383, 6.6383,6.6383,6.6383,6.6383,6.6383,6.6383,6.6383,6.6383,6.6383,6.6383,6.6383
                ,6.6383,6.6383,6.6383,6.6383,6.6383,6.6383,6.6383,6.6383,6.6383,6.6383,6.6383,6.6383,6.6383,6.6383,6.6383};
        for (int i=0 ;i<19;i++){
            long usersid = (long) (Math.random() * 100000000000000L);
            // create an instance of random.
            Random random = new Random();
            //set the max number for the int to be the length of the string.
            int randomInt = random.nextInt(alphabet.length());
            char R1=alphabetArr[randomInt];
            char R2=alphabetArr[randomInt];
            char R3=alphabetArr[randomInt];

            rootNode.getReference("usersLocation").child(R3+"ii"+R1+usersid+"m"+i+R2).child("Longitude").setValue(ExmpllongArray[i]);
            rootNode.getReference("usersLocation").child(R3+"ii"+R1+usersid+"m"+i+R2).child("Latitude").setValue(ExmpllatArray[i]);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUES_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getcurrentLocaiton();
            } else {
                Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }

    private void getcurrentLocaiton() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                .removeLocationUpdates(this);
                        if (locationResult!=null &&locationResult.getLocations().size()>0){
                            int LatestLocationIndex=locationResult.getLocations().size()-1;
                            double latitude =locationResult.getLocations().get(LatestLocationIndex).getLatitude();
                            double longitude =locationResult.getLocations().get(LatestLocationIndex).getLongitude();
                            reference = rootNode.getReference("usersLocation");
                            reference.child(mAuth.getCurrentUser().getUid()).child("Longitude").setValue(longitude);
                            reference.child(mAuth.getCurrentUser().getUid()).child("Latitude").setValue(latitude);

                            mylocationLat=latitude;
                            mylocationLong=longitude;
                            Log.d("My location",""+latitude+"."+","+longitude);
                        }
                    }
                }, Looper.getMainLooper());

    }

    public void getotherlocations(){

        //list that contains the locations
        ArrayList<String>list=new ArrayList<>();
        // Attach a listener to read the data at our posts reference
        referenceLocation = rootNode.getReference("usersLocation");

        referenceLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    locationsList=new ArrayList<OtherLocations>();
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        Double lat = ds.child("Latitude").getValue(Double.class);
                        Double longitude = ds.child("Longitude").getValue(Double.class);
                        Log.d("TAG", "gotlocations");
                        //create array of locations
                        OtherLocations loc=new OtherLocations(longitude,lat);
                        locationsList.add(loc);

                    }

                    double[] longArray=new double[locationsList.size()];
                    double[] latArray=new double[locationsList.size()];


                    int j=0;
                    for(Object loc:locationsList){
                        OtherLocations lloc=new OtherLocations();
                        lloc= (OtherLocations) loc;

                        Log.d("TAG", "otherusers latitude::"+lloc.getLatitude());



                        if ((lloc.getLatitude()==null)||(lloc.getLongitude()==null)){

                            if ((lloc.getLatitude()==null)&&(lloc.getLongitude()!=null)){
                                longArray[j]=lloc.getLongitude();
                                latArray[j]=0;
                            }else if((lloc.getLongitude()==null) && (lloc.getLatitude()!=null)){
                                longArray[j]=0;
                                latArray[j]=lloc.getLatitude();
                            }
                        }else{
                            longArray[j]=lloc.getLongitude();
                            latArray[j]=lloc.getLatitude();
                        }


                        j=j+1;
                    }
                    b=new Bundle();
                    b.putInt("size",locationsList.size());
                    b.putDoubleArray("longArray",longArray);
                    b.putDoubleArray("latArray",latArray);











                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());

            }
        });


    }

    private void getfav(){
        ProgressBar.setVisibility(View.VISIBLE);

        favPlacesAdapter.clear();
        Log.d("resvalue","hello");

        referenceFav=rootNode.getReference("FavouritePlaces").child(mAuth.getCurrentUser().getUid());
        referenceFav.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    ProgressBar.setVisibility(View.GONE);

                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    Set keys = map.keySet();

                    for (Iterator i = keys.iterator(); i.hasNext(); ) {
                        String key = (String) i.next();
                        String value = (String) map.get(key).toString();
                        Log.d("fav places",""+key.toLowerCase(Locale.ROOT));

                        Searchreference=reference.child("algeria").child("constantine").child(key.toLowerCase(Locale.ROOT));
                         Log.d("places Link",""+Searchreference);
                        Searchreference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()){

                                    String name=snapshot.child("name").getValue().toString();
                                    int state=Integer.valueOf(snapshot.child("state").getValue().toString());
                                    PlacesItem placeItem=new PlacesItem(name,state);
                                    Log.d("my favourite place",""+placeItem.getName());

                                    favPlacesAdapter.add(placeItem);
                                    Addstorebutton.setVisibility(View.GONE);
                                    AddstoreSmallbutton.setVisibility(View.VISIBLE);

                                }
                                favPlacesAdapter.notifyDataSetChanged();


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        buttonCheckGPS_Status();
        buttonCheckWIFI_Status();



        getfav();
        getotherlocations();







    }
    public boolean buttonCheckGPS_Status(){
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Log.d("GPS STATUE","GPS is ON");
            return true;
        }
        else {
            showGPSSettingAlert();
        }
        return false;
    }
    public boolean buttonCheckWIFI_Status(){
        WifiManager wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled()){
            Log.d("WIFI STATUE","WIFI is ON");
            return true;
        }else{
            showwifiSettingAlert();
        }
        return false;

    }
    public boolean checkmobiledata_status(){
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean)method.invoke(cm);
        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }
        if (mobileDataEnabled){
            Log.d("3g STATUE","3g is ON");
            return true;
        }else{
            showMobiledataSettingAlert();

        }
        return false;
    }
    public void showGPSSettingAlert()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("GPS setting!");
        alertDialog.setMessage("GPS is not enabled, Do you want to go to settings menu? ");
        alertDialog.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                MainActivity.this.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }
    public void showwifiSettingAlert()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("WIFI setting!");
        alertDialog.setMessage("WIFI is not enabled, Do you want to go to settings menu? ");
        alertDialog.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                MainActivity.this.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }
    public void showMobiledataSettingAlert()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Mobiledata setting!");
        alertDialog.setMessage("Mobiledata is not enabled, Do you want to go to settings menu? ");
        alertDialog.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
                MainActivity.this.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }




}