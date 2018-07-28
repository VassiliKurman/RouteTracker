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

/**
 * RouteManager is a class to manage start, update end end for route tracking.
 * Created by Vassili Kurman on 28/07/2018.
 * Version 1.0
 */
public class RouteManager {
    private static final String TAG = "RouteManager";
    public static final String ACTION_LOCATION = "vkurman.routetracker.ACTION_LOCATION";
    private static RouteManager sRouteManager;
    private Context mContext;
    private LocationManager mLocationManager;

    // Private constructor used in singleton pattern
    private RouteManager(Context context) {
        mContext = context;
        mLocationManager = (LocationManager) mContext
                .getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Returns an instance of <code>RouteManager</code>
     * @param context
     * @return RouteManager
     */
    public static RouteManager getInstance(Context context) {
        if (sRouteManager == null) {
            // Use the application context to avoid leaking activities
            sRouteManager = new RouteManager(context.getApplicationContext());
        }
        return sRouteManager;
    }

    /**
     * Getting PendingIntent for location update
     * @param shouldCreate
     * @return PendingIntent
     */
    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent intent = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mContext, 0, intent, flags);
    }

    /**
     * Starting to update route locations
     */
    public void startLocationUpdates() {
        String locationProvider = LocationManager.GPS_PROVIDER;
        // Get the last known location and broadcast it if there is one
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastKnownLocation = mLocationManager.getLastKnownLocation(locationProvider);
        if (lastKnownLocation != null) {
            // Reset the time to now
            lastKnownLocation.setTime(System.currentTimeMillis());
            broadcastLocation(lastKnownLocation);
        }
        // Start updates from the location manager
        PendingIntent pendingIntent = getLocationPendingIntent(true);
        mLocationManager.requestLocationUpdates(locationProvider, 0, 0, pendingIntent);
    }

    /**
     * Proadcasting current location
     * @param location
     */
    private void broadcastLocation(Location location) {
        Intent intent = new Intent(ACTION_LOCATION);
        intent.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        mContext.sendBroadcast(intent);
    }

    /**
     * Stopping location tracking updates
     */
    public void stopLocationUpdates() {
        PendingIntent pendingIntent = getLocationPendingIntent(false);
        if (pendingIntent != null) {
            mLocationManager.removeUpdates(pendingIntent);
            pendingIntent.cancel();
        }
    }

    /**
     * Checking if location tracking is running
     * @return boolean
     */
    public boolean isTracking() {
        return getLocationPendingIntent(false) != null;
    }
}