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

import vkurman.routetracker.provider.TrackerContract;
import vkurman.routetracker.utils.RouteTrackerConstants;

/**
 * TracksLoader is an AsyncTaskLoader that loads Track's in background thread.
 *
 * Created by Vassili Kurman on 06/08/2018.
 * Version 1.0
 */
public class TracksLoader extends AsyncTaskLoader<Cursor> {

    /**
     * TAG for logging
     */
    private static final String TAG = TracksLoader.class.getSimpleName();
    /**
     * TracksLoader unique id
     */
    public static final int ID = RouteTrackerConstants.LOADER_TRACKS_ID;

    public TracksLoader(@NonNull Context context) {
        super(context);
    }

    @Override
    public Cursor loadInBackground() {
        return getContext().getContentResolver().query(TrackerContract.TracksEntry.CONTENT_URI_TRACKS,
                null,
                null,
                null,
                null);
    }
}