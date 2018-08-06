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

/**
 * LocationReceiver is a BroadcastReceiver to receive Location updates.
 * Created by Vassili Kurman on 05/08/2018.
 * Version 1.0
 */
public class LocationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Successfully got location
        Location location = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
        if (location != null) {
            locationChanged(location);
            return;
        }
        // Something went wrong
        if (intent.hasExtra(LocationManager.KEY_PROVIDER_ENABLED)) {
            boolean enabled = intent
                    .getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED, false);
            providerStateChanged(enabled);
        }
    }

    /**
     * New location received. Needs overriding in Activity or Fragment.
     *
     * @param location
     */
    protected void locationChanged(Location location) {}

    /**
     * Provider state changed. Needs overriding in Activity or Fragment.
     *
     * @param enabled
     */
    protected void providerStateChanged(boolean enabled) {}
}