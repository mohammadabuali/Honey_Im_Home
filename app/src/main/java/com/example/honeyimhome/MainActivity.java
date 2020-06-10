package com.example.honeyimhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public LocationTracker locationTracker;
    public BroadcastReceiver broadcastReceiver;
    public TextView longitudeTv, accuracyTv, latitudeTv, homeTv;
    public Button trackBtn, homeBtn, clearHomeBtn;
    public final int ACCESS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        longitudeTv = findViewById(R.id.longtv);
        accuracyTv = findViewById(R.id.acctv);
        latitudeTv = findViewById(R.id.lattv);
        homeTv = findViewById(R.id.hometv);
        trackBtn = findViewById(R.id.trackbtn);
        homeBtn = findViewById(R.id.homebtn);
        clearHomeBtn = findViewById(R.id.clearHomebtn);
        locationTracker = new LocationTracker(this);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if( ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
        if(sp.getBoolean("track", false)){
            locationTracker.startTracking();
        }

        }
        else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }

        trackBtn.setOnClickListener(trackViewListener());
        String homeCheck = sp.getString("home", "");
        if(homeCheck.equals("")){
            homeTv.setText("");
            clearHomeBtn.setVisibility(View.INVISIBLE);
            homeBtn.setVisibility(View.INVISIBLE);
        }else{
            homeTv.setText(homeCheck);
            clearHomeBtn.setVisibility(View.VISIBLE);
            clearHomeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    homeTv.setText("");
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    sp.edit().putString("home", "").apply();
                    clearHomeBtn.setVisibility(View.INVISIBLE);
                }
            });
        }

        broadcastReceiver = createLocationReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter(LocationTracker.INTENT_ACTION));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        sp.edit().putString("home", "").putBoolean("track",false).apply();
    }

    public View.OnClickListener trackViewListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationTracker.startTracking();
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                boolean track = sp.getBoolean("track", false);
                if(track){
                    sp.edit().putBoolean("track", false).apply();
                    locationTracker.stopTracking();
                    trackBtn.setText("START TRACKING");
                    homeBtn.setVisibility(View.INVISIBLE);
                }else{
                    if( ActivityCompat.checkSelfPermission(MainActivity.this,
                      Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        locationTracker.startTracking();
                        sp.edit().putBoolean("track", true).apply();
                        trackBtn.setText("STOP TRACKING");
                        homeBtn.setVisibility(View.VISIBLE);
                    }
                    else{
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},111);
                    }

                }
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 111) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                sp.edit().putBoolean("track", true).apply();
                locationTracker.startTracking();

            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(this, "The app is location based. By denying " +
                            "permission, the app won't work.    ", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }
    public BroadcastReceiver createLocationReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getBooleanExtra(LocationTracker.INTENT_LOCATION, false)){
                    latitudeTv.setText(""+locationTracker.locationInfo.latitude);
                    longitudeTv.setText(""+locationTracker.locationInfo.longitude);
                    accuracyTv.setText(""+locationTracker.locationInfo.accuracy);
                    if (locationTracker.locationInfo.accuracy < 50){

                        homeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String s = "Your home location is defined as <";
                                s += locationTracker.locationInfo.latitude + ", ";
                                s += locationTracker.locationInfo.longitude + ">";
                                homeTv.setText(s);
                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                                sp.edit().putString("home", s).apply();
                                clearHomeBtn.setVisibility(View.VISIBLE);
                                clearHomeBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        homeTv.setText("");
                                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                                        sp.edit().putString("home", "").apply();
                                        clearHomeBtn.setVisibility(View.INVISIBLE);
                                    }
                                });
                                if(sp.getString("home","").equals("")){
                                    clearHomeBtn.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }
                    else{
                        homeBtn.setVisibility(View.INVISIBLE);
                        clearHomeBtn.setVisibility(View.INVISIBLE);
                    }
                }
            }

        };
    }


}