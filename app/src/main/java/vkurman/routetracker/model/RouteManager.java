/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vkurman.routetracker.model;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import vkurman.routetracker.R;
import vkurman.routetracker.provider.TrackerDbUtils;

/**
 * RouteManager is a class to manage start, update end end for route tracking.
 *
 * Created by Vassili Kurman on 28/07/2018.
 * Version 1.0
 */
public class RouteManager extends LocationCallback {
    /**
     * Tag for logging
     */
    private static final String TAG = RouteManager.class.getSimpleName();
    /**
     * Action for BroadcastReceiver
     */
    public static final String ACTION_LOCATION = "vkurman.routetracker.ACTION_LOCATION";
    /**
     * Instance of current class used in Singleton pattern.
     */
    private static RouteManager sRouteManager;
    /**
     * Reference to LocationManager
     */
    private LocationManager mLocationManager;
    /**
     * Current running track.
     */
    private Track mTrack;

    /**
     * Private constructor used in singleton pattern
     */
    private RouteManager() {}

    /**
     * Returns an instance of <code>RouteManager</code>.
     *
     * @return RouteManager
     */
    public static RouteManager getInstance() {
        if (sRouteManager == null) {
            sRouteManager = new RouteManager();
        }
        return sRouteManager;
    }

    /**
     * Getting PendingIntent for location update
     *
     * @param context
     * @param shouldCreate
     * @return PendingIntent
     */
    private PendingIntent getLocationPendingIntent(Context context, boolean shouldCreate) {
        Intent intent = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(context, 0, intent, flags);
    }

    public void startTracking(Context context, long trackId, String trackName, String userId, String imageUri) {
        Log.d(TAG, "Entered startTracking()...");
        if(permissionsGranted(context)) {
            mTrack = new Track(trackId,
                    (trackName == null || trackName.isEmpty()) ? Long.toString(trackId) : trackName,
                    userId,
                    imageUri);
            // Adding Track to Database
            TrackerDbUtils.addTrack(context, mTrack);

            startLocationUpdates(context);
        }
        Log.d(TAG, "...exiting startTracking()");
    }

    public void stopTracking(Context context) {
        Log.d(TAG, "Entered stopTracking()...");
        mTrack = null;
        stopLocationUpdates(context);
        Log.d(TAG, "...exiting stopTracking()");
    }

    /**
     * Checking if necessary permitions are granted.
     *
     * @param context - app context
     * @return boolean
     */
    private boolean permissionsGranted(Context context) {
        return
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Starting to update route locations
     *
     * @param context - app context
     */
    @SuppressLint("MissingPermission")
    private void startLocationUpdates(Context context) {
        Log.d(TAG, "Entered startLocationUpdates()...");
        // Checking if permissions are granted
        if (permissionsGranted(context)) {
            // Checking if have reference to LocationManager
            if (mLocationManager == null) {
                mLocationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            }
            String locationProvider = LocationManager.GPS_PROVIDER;
            // Get the last known location and broadcast it if there is one
            Location lastKnownLocation = mLocationManager.getLastKnownLocation(locationProvider);
            if (lastKnownLocation != null) {
                // Resetting the time
                lastKnownLocation.setTime(System.currentTimeMillis());
                broadcastLocation(context, lastKnownLocation);
            }
            // Start updates from the location manager
            PendingIntent pendingIntent = getLocationPendingIntent(context,true);
            // Getting preferences for tracking
//            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//
//            long defTimeValue = 5000;
//            float defDistanceValue = 5.0f;
//            try {
//                defTimeValue = Long.valueOf(context.getString(R.string.pref_value_default_tracking_minimum_time));
//                defDistanceValue = Float.valueOf(context.getString(R.string.pref_value_default_tracking_minimum_distance));
//            } catch (NumberFormatException e) {
//                Log.e(TAG, "Error formatting numbers");
//            }
//
//            long minTime = sharedPreferences.getLong(
//                    context.getString(R.string.pref_key_default_tracking_minimum_time), defTimeValue);
//            float minDistance = sharedPreferences.getFloat(
//                    context.getString(R.string.pref_key_default_tracking_minimum_distance), defDistanceValue);

//            mLocationManager.requestLocationUpdates(locationProvider, minTime, minDistance, pendingIntent);
            mLocationManager.requestLocationUpdates(locationProvider, 0, 0, pendingIntent);
        } else {
            Log.e(TAG, "Not all permissions are granted");
        }
        Log.d(TAG, "...exiting startLocationUpdates()");
    }

    /**
     * Broadcasting current location
     *
     * @param context
     * @param location
     */
    private void broadcastLocation(Context context, Location location) {
        Log.d(TAG, "Entered broadcastLocation()...");
        Intent intent = new Intent(ACTION_LOCATION);
        intent.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);

        Log.d(TAG, "Sending broadcast...");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        Log.d(TAG, "...broadcast sent");

        Log.d(TAG, "...exiting broadcastLocation()");
    }

    /**
     * Stopping location tracking updates
     *
     * @param context
     */
    private void stopLocationUpdates(Context context) {
        Log.d(TAG, "Entered stopLocationUpdates() ...");
        Log.d(TAG, "Retrieving pending intent...");
        PendingIntent pendingIntent = getLocationPendingIntent(context, false);
        if (pendingIntent != null) {
            Log.d(TAG, "... pending intent retrieved!");
            if(mLocationManager != null) {
                Log.d(TAG, "Removing updates from LocationManager ...");
                mLocationManager.removeUpdates(pendingIntent);
                Log.d(TAG, "... removed updates from LocationManager");
            }
            pendingIntent.cancel();
        } else {
            Log.e(TAG, "... pending intent not retrieved");
        }
        Log.d(TAG, "... exiting stopLocationUpdates()");
    }

    /**
     * Checking if location tracking is running
     *
     * @param context
     * @return boolean
     */
    public boolean isTracking(Context context) {
        return getLocationPendingIntent(context,false) != null;
    }
}