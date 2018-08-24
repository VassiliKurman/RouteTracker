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
package vkurman.routetracker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import vkurman.routetracker.model.RouteManager;

/**
 * LocationReceiver is a BroadcastReceiver to receive Location updates.
 * Created by Vassili Kurman on 05/08/2018.
 * Version 1.0
 */
public class LocationReceiver extends BroadcastReceiver {
    /**
     * Tag for logging
     */
    public static final String TAG = LocationReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Entered onReceive()");
        // Successfully got location
        Location location = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
        if (location != null) {
            Log.d(TAG, "Sending location data...");
            locationChanged(context, location);
            Log.d(TAG, "... location data sent");
            return;
        }
        // Something went wrong
        if (intent.hasExtra(LocationManager.KEY_PROVIDER_ENABLED)) {
            boolean enabled = intent
                    .getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED, false);
            Log.d(TAG, "Sending provider state change...");
            providerStateChanged(enabled);
            Log.d(TAG, "...provider state change sent");
        }
        Log.d(TAG, "... exiting onReceive()");
    }

    /**
     * New location received. Needs overriding in Activity or Fragment.
     *
     * @param location
     */
    protected void locationChanged(Context context, Location location) {
        Intent intent = new Intent(RouteManager.ACTION_LOCATION);
        intent.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);

        Log.d(TAG, "Sending broadcast from locationChanged()...");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        Log.d(TAG, "...broadcast sent from locationChanged()");
    }

    /**
     * Provider state changed. Needs overriding in Activity or Fragment.
     *
     * @param enabled
     */
    protected void providerStateChanged(boolean enabled) {}
}