package com.example.antimatter.geofencing;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by antimatter on 15/1/17.
 */

public final class Constants {
    private Constants(){}


    public static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";
    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";
    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 1; // 1 mile, 1.6 km

    public static final HashMap<String, LatLng> AREA_LANDMARKS = new HashMap<String, LatLng>();
    static {
            AREA_LANDMARKS.put("SFO", new LatLng(37.621313, -122.378955));
            AREA_LANDMARKS.put("GOOGLE", new LatLng(37.422611,-122.0840577));
            AREA_LANDMARKS.put("Udacity Studio", new LatLng(37.3999497,-122.1084776));
    }
}
