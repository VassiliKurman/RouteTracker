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

import vkurman.routetracker.provider.TrackerContract;
import vkurman.routetracker.utils.RouteTrackerConstants;

/**
 * TrackDetailsLoader is an AsyncTaskLoader that loads Track details in background thread.
 *
 * Created by Vassili Kurman on 10/08/2018.
 * Version 1.0
 */
public class TrackDetailsLoader extends AsyncTaskLoader<List<Cursor>> {
    /**
     * TAG for logging
     */
    private static final String TAG = TracksLoader.class.getSimpleName();
    /**
     * TracksLoader unique id
     */
    public static final int ID = 1002;
    private long mTrackId;

    /**
     * Constructor for TrackDetailsLoader.
     *
     * @param context
     * @param trackId
     */
    public TrackDetailsLoader(@NonNull Context context, long trackId) {
        super(context);
        mTrackId = trackId;
    }

    @Override
    public List<Cursor> loadInBackground() {
        List<Cursor> data = new ArrayList<>(2);
        Log.d(TAG, "Adding track...");
        // Retrieving track data
        data.add(RouteTrackerConstants.LOADER_TRACK_INDEX, getContext().getContentResolver().query(
                TrackerContract.TracksEntry.CONTENT_URI_TRACKS,
                null,
                TrackerContract.TracksEntry.COLUMN_TRACKS_ID + "=?",
                new String[]{Long.toString(mTrackId)},
                null));

        Log.d(TAG, "Adding waypoints...");
        // retrieving waypoints for track
        data.add(RouteTrackerConstants.LOADER_WAYPOINTS_INDEX, getContext().getContentResolver().query(TrackerContract.WaypointsEntry.CONTENT_URI_WAYPOINTS,
                null,
                TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_TRACK_ID + "=?",
                new String[]{Long.toString(mTrackId)},
                TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_TIMESTAMP));

        Log.d(TAG, "Data loaded!");
        return data;
    }
}