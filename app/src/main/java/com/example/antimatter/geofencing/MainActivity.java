package com.example.antimatter.geofencing;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, ResultCallback<Status>, LocationListener{

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static final String TAG = "MainActivity";
    private ArrayList<Geofence> mGeofenceList;
    private Button mAddGeofencesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAddGeofencesButton = (Button)findViewById(R.id.add_geofences_button);
        mGeofenceList = new ArrayList<Geofence>();
        populateGeofenceList();
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                            .addOnConnectionFailedListener(this)
                            .addConnectionCallbacks(this)
                            .addApi(LocationServices.API)
                            .build();
    }

    public void populateGeofenceList(){
        for(Map.Entry<String, LatLng> entry: Constants.AREA_LANDMARKS.entrySet()){
            mGeofenceList.add(new Geofence.Builder()
                            .setRequestId(entry.getKey())
                            .setCircularRegion(entry.getValue().latitude, entry.getValue().longitude, Constants.GEOFENCE_RADIUS_IN_METERS)
                            .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build());
        }
    }

    private GeofencingRequest getGeofencingRequest(){
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent(){
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(@NonNull Status status) {
        if(status.isSuccess()){
            Toast.makeText(this, "Geofences added", Toast.LENGTH_SHORT).show();
        } else {
            String errorMessage = GeofenceErrorMessages.getErrorString(this, status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }

    public void addGeofencesButtonHandler(View view){
        if(!mGoogleApiClient.isConnected()){
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try{
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()).setResultCallback(this);
        } catch (SecurityException securityException){
            Log.e(TAG, "SecurityExceptionOccurred");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!mGoogleApiClient.isConnecting() || !mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if(mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting())
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged");
    }
}
