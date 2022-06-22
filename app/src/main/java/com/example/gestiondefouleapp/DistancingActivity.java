package com.example.gestiondefouleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DistancingActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ImageView poweronImg,poweroffImg,ConnectedGadgetImg;
    TextView GadgetstateTV,connectedGadgetTV;
    String gadgetkey;
    EditText gadgetkeyET;
    //database
    private FirebaseAuth mAuth;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distancing);

        poweronImg= findViewById(R.id.poweronImg);
        poweroffImg=findViewById(R.id.poweroffImg);
        GadgetstateTV= findViewById(R.id.GadgetstateTV);
        ConnectedGadgetImg=findViewById(R.id.ConnectedGadgetImg);
        connectedGadgetTV=findViewById(R.id.connectedGadgetTV);
        gadgetkeyET=findViewById(R.id.gadgetkeyET);

        //firebase datbase:
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("usersHistory");
        mAuth = FirebaseAuth.getInstance();
        // get data from share SharePreference
        SharedPreferences sp = getSharedPreferences("FILE_NAME", MODE_PRIVATE);
        String result = sp.getString("key", "");

        if (!(result.equals(""))){
            poweronImg.setVisibility(View.INVISIBLE);
            poweroffImg.setVisibility(View.VISIBLE);
            GadgetstateTV.setText("Connecté");
            connectToGadget(result);
        }

        //botoom nav
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_Distancing);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()){

                    case R.id.nav_Distancing:
                        break;

                    case R.id.nav_home:
                        intent=new Intent(DistancingActivity.this,MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_Map:
                        intent=new Intent(DistancingActivity.this,MapsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_history:
                        intent=new Intent(DistancingActivity.this,HistoryActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_profile:
                        intent=new Intent(DistancingActivity.this,ProfileActivity.class);
                        startActivity(intent);
                        break;
                }



                return true;
            }
        });

        //start the gadget
        poweronImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poweronImg.setVisibility(View.INVISIBLE);
                poweroffImg.setVisibility(View.VISIBLE);
                GadgetstateTV.setText("Connecté");
                gadgetkey=gadgetkeyET.getText().toString();
                connectToGadget(gadgetkey);


            }
        });
        //stop the gadget
        poweroffImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poweroffImg.setVisibility(View.INVISIBLE);
                poweronImg.setVisibility(View.VISIBLE);
                GadgetstateTV.setText("Non connecté");

            }
        });



    }

    private void connectToGadget(String gadgetkey) {


        reference=rootNode.getReference("usersHistory").child(gadgetkey);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    gadgetkeyET.setText("Connecté à ESP8266:"+gadgetkey);
                    connectedGadgetTV.setText("Connecté à ESP8266");
                    connectedGadgetTV.setTextColor(Color.parseColor("#189C56"));
                    ConnectedGadgetImg.setImageResource(R.drawable.ic_connected_gadget);
                    // save data into share SharePreference
                    SharedPreferences sp = getSharedPreferences("FILE_NAME", MODE_PRIVATE);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString("key",gadgetkey);
                    edit.apply();
/*
                    // get data from share SharePreference
                    SharedPreferences sp = getSharedPreferences("FILE_NAME", MODE_PRIVATE);
                    int result = sp.getString("key", -1);*/

                }else{
                    poweroffImg.setVisibility(View.INVISIBLE);
                    poweronImg.setVisibility(View.VISIBLE);
                    GadgetstateTV.setText("Non connecté");
                    Toast.makeText(DistancingActivity.this, "veuillez entrer une clé valide", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}