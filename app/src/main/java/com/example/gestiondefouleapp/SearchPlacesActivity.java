package com.example.gestiondefouleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchPlacesActivity extends Activity {
    EditText searchEt;
    ListView SeachedPlacesLV;
    Button goButton;
    private FirebaseAuth mAuth;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    DatabaseReference Searchreference,referenceFav;
    PlacesAdapter PlacesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_places);
        searchEt=findViewById(R.id.searchEt);
        goButton=findViewById(R.id.goButton);
        SeachedPlacesLV=findViewById(R.id.SeachedPlacesLV);
        //database
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("places");
        //Authentification:
        mAuth = FirebaseAuth.getInstance();

        final ArrayList<PlacesItem> arrayList = new ArrayList<PlacesItem>();
        PlacesAdapter = new PlacesAdapter(this, arrayList);
        SeachedPlacesLV.setAdapter(PlacesAdapter);

        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width=dm.widthPixels;
        int height=dm.heightPixels;

        getWindow().setLayout( (int)(width*1), (int)(height*.8));

        WindowManager.LayoutParams params=getWindow().getAttributes();
        params.gravity= Gravity.CENTER;
        params.x=0;
        params.y=-20;
        getWindow().setAttributes(params);

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Searchplace();
            }
        });
        SeachedPlacesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(SearchPlacesActivity.this,PlacesAdapter.getItem(position).getName()+"ajouté avec succès à votre liste de favoris",Toast.LENGTH_LONG).show();
                addNewFav(PlacesAdapter.getItem(position).getName());

            }
        });


    }
    private void Searchplace() {
        PlacesAdapter.clear();

        String searchedplace=searchEt.getText().toString();
        if (searchedplace.equals("")){
            Toast.makeText(SearchPlacesActivity.this,"veuillez entrer un nom de lieu",Toast.LENGTH_LONG).show();
            Log.d("Resultplace","veuillez entrer un nom de lieu");

        }else{
            Log.d("Searchedplace",searchedplace);
            Searchreference=reference.child("algeria").child("constantine").child(searchedplace);
            Searchreference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()){
                        Log.d("resvalue",""+snapshot.child("name").getValue());
                        String name=snapshot.child("name").getValue().toString();
                        int state=Integer.valueOf(snapshot.child("state").getValue().toString());
                        PlacesItem placeItem=new PlacesItem(name,state);
                        PlacesAdapter.add(placeItem);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    private void addNewFav(String Name) {
        referenceFav=rootNode.getReference("FavouritePlaces");
        referenceFav.child(mAuth.getCurrentUser().getUid()).child(Name).child("name").setValue(Name);
    }




}