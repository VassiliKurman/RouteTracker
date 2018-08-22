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
package vkurman.routetracker.loader;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import vkurman.routetracker.firebase.FirebaseInterface;
import vkurman.routetracker.firebase.TrackWrapper;
import vkurman.routetracker.provider.TrackerContract;
import vkurman.routetracker.utils.RouteTrackerConstants;

/**
 * SharedTrackDetailsLoader is an AsyncTaskLoader that loads Track details in background thread.
 *
 * Created by Vassili Kurman on 22/08/2018.
 * Version 1.0
 */
public class SharedTrackDetailsLoader extends AsyncTaskLoader<TrackWrapper> {
    /**
     * TAG for logging
     */
    private static final String TAG = TracksLoader.class.getSimpleName();
    /**
     * TracksLoader unique id
     */
    public static final int ID = RouteTrackerConstants.LOADER_SHARED_TRACKS_ID;
    private long mTrackId;

    /**
     * Constructor for SharedTrackDetailsLoader.
     *
     * @param context
     * @param trackId
     */
    public SharedTrackDetailsLoader(@NonNull Context context, long trackId) {
        super(context);
        mTrackId = trackId;
    }

    @Override
    public TrackWrapper loadInBackground() {
        TrackWrapper data = new TrackWrapper();
        Log.d(TAG, "Adding shared track...");
        // Retrieving track data
        // TODO

        Log.d(TAG, "Adding shared waypoints...");
        // retrieving waypoints for track
        // TODO

        Log.d(TAG, "... shared data loaded!");
        return data;
    }
}