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
package vkurman.routetracker.ui;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import vkurman.routetracker.R;
import vkurman.routetracker.loader.TrackDetailsLoader;
import vkurman.routetracker.model.Track;
import vkurman.routetracker.model.Waypoint;
import vkurman.routetracker.provider.TrackerContract;
import vkurman.routetracker.utils.RouteTrackerConstants;

/**
 * TrackDetailsActivity
 *
 * Created by Vassili Kurman on 09/08/2018.
 * Version 1.0
 */
public class TrackDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Cursor>> {
    // Key for logging
    private static final String TAG = TrackDetailsActivity.class.getSimpleName();
    // Initial track id
    private static final long INITIAL_TRACK_ID = -1;
    // Keys for saved instance
    private static final String TRACK_ID = "trackId";
    private static final String TRACK = "track";
    private static final String WAYPOINTS = "waypoints";
    /**
     * Unique id for loader
     */
    private int mTracksLoaderId;
    /**
     * Reference to track id
     */
    private long mTrackId;
    /**
     * Reference to track
     */
    private Track mTrack;
    /**
     * Reference to waypoints
     */
    private Waypoint[] mWaypoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_details);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_details_activity);
        mTrackId = INITIAL_TRACK_ID;
        if(savedInstanceState != null) {
            mTrackId = savedInstanceState.getLong(TRACK_ID);
            mTrack = savedInstanceState.getParcelable(TRACK);
            mWaypoints = (Waypoint[]) savedInstanceState.getParcelableArray(WAYPOINTS);

            if(mTrack != null) {
                getSupportActionBar().setTitle(mTrack.getName());
            }
        }  else {
            mTracksLoaderId = TrackDetailsLoader.ID;
            // Retrieving track id
            mTrackId = getIntent().getLongExtra(RouteTrackerConstants.INTENT_NAME_FOR_TRACK_ID, INITIAL_TRACK_ID);
            if(mTrackId != INITIAL_TRACK_ID) {
                // Retrieving data
                retrieveData();
                // passing data to fragments
                // TODO display data in fragments
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(TRACK_ID, mTrackId);
        outState.putParcelable(TRACK, mTrack);
        outState.putParcelableArray(WAYPOINTS, mWaypoints);
    }

    /**
     * Calling Loader to retrieve data from database
     */
    private void retrieveData() {
        if(getSupportLoaderManager().getLoader(mTracksLoaderId) == null) {
            getSupportLoaderManager().initLoader(mTracksLoaderId, null, this).forceLoad();
        } else {
            getSupportLoaderManager().getLoader(mTracksLoaderId).forceLoad();
        }
    }

    @NonNull
    @Override
    public Loader<List<Cursor>> onCreateLoader(int id, @Nullable Bundle args) {
        return new TrackDetailsLoader(this, mTrackId);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Cursor>> loader, List<Cursor> data) {
        if(data == null || data.isEmpty()) {
            return;
        }
        // Resetting references
        mTrack = null;
        mWaypoints = null;

        // Getting track data from cursor
        Cursor cursor = data.get(RouteTrackerConstants.LOADER_TRACK_INDEX);
        if(cursor.getCount() > 0) {
            cursor.moveToNext();
            long id = cursor.getLong(cursor.getColumnIndex(TrackerContract.TracksEntry.COLUMN_TRACKS_ID));
            String name = cursor.getString(cursor.getColumnIndex(TrackerContract.TracksEntry.COLUMN_TRACKS_NAME));
            String owner = cursor.getString(cursor.getColumnIndex(TrackerContract.TracksEntry.COLUMN_TRACKS_OWNER));
            String image = cursor.getString(cursor.getColumnIndex(TrackerContract.TracksEntry.COLUMN_TRACKS_IMAGE));
            mTrack = new Track(id, name, owner, image);
        }
        // Getting waypoints data from cursor
        if(data.size() > RouteTrackerConstants.LOADER_WAYPOINTS_INDEX) {
            cursor = data.get(RouteTrackerConstants.LOADER_WAYPOINTS_INDEX);
            if (cursor.getCount() > 0) {
                mWaypoints = new Waypoint[cursor.getCount()];
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    long id = cursor.getLong(cursor.getColumnIndex(TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_ID));
                    long trackId = cursor.getLong(cursor.getColumnIndex(TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_TRACK_ID));
                    double latitude = cursor.getDouble(cursor.getColumnIndex(TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_LATITUDE));
                    double longitude = cursor.getDouble(cursor.getColumnIndex(TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_LONGITUDE));
                    float altitude = cursor.getFloat(cursor.getColumnIndex(TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_ALTITUDE));
                    long timeStamp = cursor.getLong(cursor.getColumnIndex(TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_TIMESTAMP));
                    mWaypoints[i] = new Waypoint(id, trackId, latitude, longitude, altitude, timeStamp);
                }
            }
        }

        // S
        getSupportActionBar().setTitle(TextUtils.isEmpty(mTrack.getName()) ? Long.toString(mTrack.getId()) : mTrack.getName());

        TrackDetailsFragment fragment = (TrackDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.track_container);
        if(fragment != null) {
            fragment.setTrackData(mTrack, mWaypoints);
        } else {
            Toast.makeText(this, "Error finding track fragment", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Cursor>> loader) {}
}