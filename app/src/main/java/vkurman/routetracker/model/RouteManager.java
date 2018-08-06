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
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;

/**
 * RouteManager is a class to manage start, update end end for route tracking.
 * Created by Vassili Kurman on 28/07/2018.
 * Version 1.0
 */
public class RouteManager {
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

    /**
     * Starting to update route locations
     *
     * @param context
     */
    public void startLocationUpdates(Context context) {
        // Checking if permissions are granted
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
            mLocationManager.requestLocationUpdates(locationProvider, 0, 0, pendingIntent);
        }
    }

    /**
     * Broadcasting current location
     *
     * @param context
     * @param location
     */
    private void broadcastLocation(Context context, Location location) {
        Intent intent = new Intent(ACTION_LOCATION);
        intent.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    /**
     * Stopping location tracking updates
     *
     * @param context
     */
    public void stopLocationUpdates(Context context) {
        PendingIntent pendingIntent = getLocationPendingIntent(context, false);
        if (pendingIntent != null) {
            if(mLocationManager != null) {
                mLocationManager.removeUpdates(pendingIntent);
            }
            pendingIntent.cancel();
        }
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