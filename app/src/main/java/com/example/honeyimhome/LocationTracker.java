package com.example.honeyimhome;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


import java.util.concurrent.Executor;

class LocationTracker {

    private static final String TAG = "LocationTracker";
    final private static String EXCEPTION = "exception: ";
    public static String INTENT_ACTION = "lala";
    public static String INTENT_LOCATION = "vovo";
    public boolean bool = true;

    public Context context;
    public FusedLocationProviderClient fusedLocationProviderClient;
    public LocationCallback locationCallback;
    public LocationInfo locationInfo;

    public LocationTracker(Context context){
        this.context = context;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        locationCallback = trackLocation();
    }

    void startTracking(){
        bool = true;
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(context);
        Executor executor = ContextCompat.getMainExecutor(context);
        client.checkLocationSettings(builder.build()).addOnSuccessListener(executor, new OnSuccessListener<LocationSettingsResponse>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                        locationCallback,
                        Looper.getMainLooper());
            }
        });

    }

    void stopTracking() {
        bool = false;

        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(context);
        Executor executor = ContextCompat.getMainExecutor(context);
        client.checkLocationSettings(builder.build()).addOnSuccessListener(executor, new OnSuccessListener<LocationSettingsResponse>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                        stopTrackingLocation(),
                        Looper.getMainLooper());
            }
        });


    }
    public LocationCallback stopTrackingLocation() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Intent intent = new Intent();
                intent.setAction(INTENT_ACTION);
                intent.putExtra(INTENT_LOCATION, bool);
                context.sendBroadcast(intent);
            }
        };
    }


    public LocationCallback trackLocation(){
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Intent intent = new Intent();
                intent.setAction(INTENT_ACTION);
                if (locationResult == null) {
                    intent.putExtra(INTENT_LOCATION, bool);
                    context.sendBroadcast(intent);
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    locationInfo = new LocationInfo(location.getAccuracy(), location.getLatitude(), location.getLongitude());
                    intent.putExtra(INTENT_LOCATION, bool);
                }
                context.sendBroadcast(intent);
            }
        };
    }

}