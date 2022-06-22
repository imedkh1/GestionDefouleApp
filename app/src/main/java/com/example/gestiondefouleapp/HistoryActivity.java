package com.example.gestiondefouleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    DatabaseReference Historyreference;
    String path;
    HistoryAdapter historyAdapter;
    TextView nohistoryTV;
    ProgressBar ProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        nohistoryTV=findViewById(R.id.nohistoryTV);
        ProgressBar=findViewById(R.id.ProgressBar);

        // create a arraylist of the type NumbersView
        final ArrayList<HistoryItem> arrayList = new ArrayList<HistoryItem>();

        // Now create the instance of the NumebrsViewAdapter and pass
        // the context and arrayList created above
        historyAdapter = new HistoryAdapter(this, arrayList);

        // create the instance of the ListView to set the numbersViewAdapter
        ListView HistoryListView = findViewById(R.id.HistoriqueListV);

        // set the numbersViewAdapter for ListView
        HistoryListView.setAdapter(historyAdapter);


        //firebase datbase:
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("usersHistory");
        mAuth = FirebaseAuth.getInstance();



        //bottom nav
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_history);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()){

                    case R.id.nav_Distancing:
                        intent=new Intent(HistoryActivity.this,DistancingActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_home:
                        intent=new Intent(HistoryActivity.this,MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_Map:
                        intent=new Intent(HistoryActivity.this,MapsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_history:

                        break;

                    case R.id.nav_profile:
                       intent=new Intent(HistoryActivity.this,ProfileActivity.class);
                        startActivity(intent);
                        break;
                }



                return true;
            }
        });



        }


    public void getuserHistory(){
        ProgressBar.setVisibility(View.VISIBLE);
        Historyreference=reference.child(path);
        System.out.println("refrence :::"+Historyreference.toString());
        Historyreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    System.out.println("snapshottt :::"+snapshot.toString());

                    // for(DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        Map<String, Object> mapTime = (Map<String, Object>) snapshot.child("Time").getValue();
                        Map<String, Object> mapDate = (Map<String, Object>) snapshot.child("Date").getValue();
                        String[] timeArray=new String[mapTime.size()];
                        String[] dateArray=new String[mapDate.size()];

                        //arrayhistoryList
                        Iterator<Map.Entry< String, Object >> iterator = mapTime.entrySet().iterator();
                        int i=0;
                        while (iterator.hasNext()) {
                            Map.Entry<String,Object > entry = iterator.next();
                            System.out.println(entry.getValue());
                            timeArray[i]=entry.getValue().toString();
                            i++;
                        }
                        int j=0;
                        Iterator<Map.Entry< String, Object >> iterator2 = mapDate.entrySet().iterator();
                        while (iterator2.hasNext()) {
                            Map.Entry<String,Object > entry2 = iterator2.next();
                            System.out.println("Date"+entry2.getValue());
                            dateArray[j]=entry2.getValue().toString();
                            j++;
                        }

                        for(int s=0;s<timeArray.length-1;s++)
                        {
                            for(int m=s + 1;m<timeArray.length;m++)
                            {
                                try {
                                    String substr=timeArray[s].substring(0,5);
                                    String substrnxt=timeArray[m].substring(0,5);
                                    if (dateArray[s].equals(dateArray[m])){
                                        if(timeArray[s] != null && substr.equals(substrnxt))
                                        {
                                            // array = ArrayUtils.removeElement(array, array[s]); --m;??
                                            timeArray[m] = null; // Mark for deletion later on
                                            dateArray[m] = null;
                                        }
                                    }

                                } catch (Exception e) {
                                    System.out.println(e);
                                }

                            }
                        }
                        int h=0;
                        for(int k=0;k<timeArray.length;k++){


                            if (timeArray[k] != null){
                                 h =h+1;

                                Log.d("Distancing Notification", "onDataChange");
                                HistoryItem historyItem=new HistoryItem(R.drawable.socialdistancered,timeArray[k],dateArray[k]);
                                historyAdapter.add(historyItem);

                                ProgressBar.setVisibility(View.GONE);

                            }









                        }

                        //get ddata from sharedpref

                        SharedPreferences sp = getSharedPreferences("FILE_NAME", MODE_PRIVATE);
                        int historynumber = sp.getInt("historynumber", 0);
                        if (h!=0 && historynumber!=0){

                            if (h>historynumber){
                                notifyThis();
                            }

                        }
                        // save data into share SharePreference
                        SharedPreferences spHistory = getSharedPreferences("FILE_NAME", MODE_PRIVATE);
                        SharedPreferences.Editor edit = spHistory.edit();
                        edit.putInt("historynumber",h);
                        edit.apply();


                    } catch (Exception e) {
                        System.out.println("exception: "+e);

                    }





                    //}
                    historyAdapter.notifyDataSetChanged();


                }else{
                    ProgressBar.setVisibility(View.GONE);
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
        // get data from share SharePreference
        SharedPreferences sp = getSharedPreferences("FILE_NAME", MODE_PRIVATE);
        String result = sp.getString("key", "");
        path=result;
        if (!(result.equals(""))){
            nohistoryTV.setVisibility(View.GONE);
            getuserHistory();
        }

    }
    public void notifyThis() {

        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.getApplicationContext(), "notify_001");
        Intent ii = new Intent(this.getApplicationContext(), SearchPlacesActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Attention");
        bigText.setBigContentTitle("Attention,quelqu'un était trés proche");
        bigText.setSummaryText("Attention");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("Attention");
        mBuilder.setContentText("Attention,quelqu'un était trés proche2");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());
        Intent intent=new Intent(HistoryActivity.this,HistoryActivity.class);
        startActivity(intent);
    }

}