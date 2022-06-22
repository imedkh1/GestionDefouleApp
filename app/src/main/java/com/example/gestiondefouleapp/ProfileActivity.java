package com.example.gestiondefouleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    TextView userfullnameTV,userEmailTV;
    CardView EditeuserinfoLayout;
    RelativeLayout PlusLayout3,PlusLayout1,PlusLayout2;
    private FirebaseAuth mAuth;
    FirebaseDatabase rootNode;
    DatabaseReference reference,referenceHistory;
    String UserFullname,UserEmail,UserPhoneNumber;
    User userprofile=new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userfullnameTV = findViewById(R.id.userfullnameTV);
        userEmailTV = findViewById(R.id.userEmailTV);
        EditeuserinfoLayout=findViewById(R.id.EditeuserinfoLayout);
        PlusLayout1=findViewById(R.id.PlusLayout1);
        PlusLayout3=findViewById(R.id.PlusLayout3);
        PlusLayout2=findViewById(R.id.PlusLayout2);

        // get data from share SharePreference
        SharedPreferences sp = getSharedPreferences("FILE_NAME", MODE_PRIVATE);
        String result = sp.getString("key", "");


        //firebase datbase:
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");
        mAuth = FirebaseAuth.getInstance();
        getuserinfo();


        //bottom nav bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()){

                    case R.id.nav_home:
                        intent=new Intent(ProfileActivity.this,MainActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_Map:
                        intent=new Intent(ProfileActivity.this,MapsActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_Distancing:
                        intent=new Intent(ProfileActivity.this,DistancingActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_history:
                        intent=new Intent(ProfileActivity.this,HistoryActivity.class);
                        startActivity(intent);

                        break;

                    case R.id.nav_profile:



                        break;
                }



                return true;
            }
        });


        //edit profile account info
        EditeuserinfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ProfileActivityEdit","open new page");

                Intent i = new Intent(ProfileActivity.this, EditProfileActivity.class);
                i.putExtra("UserFullname",UserFullname);
                i.putExtra("UserEmail",UserEmail);
                i.putExtra("UserPhoneNumber",UserPhoneNumber);
                startActivity(i);
            }
        });

        //signout
        PlusLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Se déconnecter")
                        .setMessage("Voulez-vous vraiment déconnecter?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                finish();
                                startActivity(intent);
                                Toast.makeText(ProfileActivity.this, "déconnecté", Toast.LENGTH_SHORT).show();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();


            }
        });
        //clear all user data(history and gadget key)
        PlusLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Vider le cache")
                        .setMessage("Voulez-vous vraiment effacer votre historique?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (!(result.equals(""))){
                                    referenceHistory=rootNode.getReference("usersHistory").child(result);
                                    referenceHistory.removeValue();
                                }
                                // delete data from share SharePreference
                                SharedPreferences sp = getSharedPreferences("FILE_NAME", MODE_PRIVATE);
                                SharedPreferences.Editor edit = sp.edit();
                                edit.putString("key","");
                                edit.apply();
                                Toast.makeText(ProfileActivity.this, "Historique supprimé avec succès", Toast.LENGTH_SHORT).show();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();


            }

        });
        //open help and support page
        PlusLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent
                        = new Intent(ProfileActivity.this,
                        HelpandSupportActivity.class);
                startActivity(intent);
            }
        });





    }

    private void getuserinfo() {
        Log.d("TAG",""+mAuth.getCurrentUser().getUid());
        //get all user data
        reference = rootNode.getReference("users").child(mAuth.getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userprofile=snapshot.getValue(User.class);

                    UserFullname=userprofile.getName();
                    UserEmail=userprofile.getAddress();
                    UserPhoneNumber=userprofile.getPhone();
                    userfullnameTV.setText(userprofile.getName());
                    userEmailTV.setText(userprofile.getAddress());



                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}