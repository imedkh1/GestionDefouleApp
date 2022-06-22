package com.example.gestiondefouleapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.gestiondefouleapp.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    BottomNavigationView bottomNavigationView;

    private ClusterManager <MyItem> clusterManager;
    private List<MyItem> items=new ArrayList<>();
    List<LatLng> latLngs  =new ArrayList<>();
    ArrayList lstlocationsList;
    double[] longArray;
    double[] latArray;
    int s;

    //my current location
    public Double mylocationLatM;
    public Double mylocationLongM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //botoom nav
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_Map);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()){

                    case R.id.nav_Distancing:
                        intent=new Intent(MapsActivity.this,DistancingActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_home:
                        intent=new Intent(MapsActivity.this,MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_Map:

                        break;
                    case R.id.nav_history:
                        intent=new Intent(MapsActivity.this,HistoryActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_profile:
                        intent=new Intent(MapsActivity.this,ProfileActivity.class);
                        startActivity(intent);
                        break;
                }



                return true;
            }
        });
        Bundle b = MainActivity.b;
        s=b.getInt("size");
        longArray=new double[s];
        latArray=new double[s];
        longArray=b.getDoubleArray("longArray");
        latArray=b.getDoubleArray("latArray");
        Log.d("longitudzzze",":"+longArray[0]);
        Log.d("latitude",":"+latArray[0]);
        mylocationLatM= MainActivity.mylocationLat;
        mylocationLongM=MainActivity.mylocationLong;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        clusterManager = new ClusterManager<MyItem>(this, mMap);

        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);

        // Add a marker in my location and move the camera
        LatLng mylocation = new LatLng(mylocationLatM, mylocationLongM);
        clusterManager.addItem(new MyItem(mylocation.latitude, mylocation.longitude, "Mylocation ", "My location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mylocationLatM,mylocationLongM), 12.0f));
        clusterManager.cluster();
        for (int i=0;i<s;i++){

            Log.d("longitude",i+":"+longArray[i]);
            Log.d("latitude",i+":"+latArray[i]);
            LatLng User = new LatLng(latArray[i], longArray[i]);
            latLngs.add(User);
            items.add(new MyItem(User.latitude, User.longitude, "User " + i, "User " + i));

            // Create a heat map tile provider, passing it the latlngs of the police stations.



        }
        HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                .radius(45)
                .data(latLngs)
                .build();

        // Add a tile overlay to the map, using the heat map tile provider.
        TileOverlay overlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
    }
}