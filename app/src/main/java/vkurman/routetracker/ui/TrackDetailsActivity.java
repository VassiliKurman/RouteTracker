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

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import vkurman.routetracker.R;
import vkurman.routetracker.model.Track;
import vkurman.routetracker.model.Waypoint;
import vkurman.routetracker.utils.RouteTrackerConstants;

/**
 * TrackDetailsActivity
 *
 * Created by Vassili Kurman on 09/08/2018.
 * Version 1.0
 */
public class TrackDetailsActivity extends AppCompatActivity {
    // Key for logging
    private static final String TAG = TrackDetailsActivity.class.getSimpleName();
    // Keys for saved instance
    private static final String TRACK = "track";
    private static final String WAYPOINTS = "waypoints";

    /**
     * Reference to track
     */
    private Track mTrack;
    private Waypoint[] mWaypoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_details);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle(R.string.title_details_activity);
        if(savedInstanceState != null) {
            mTrack = savedInstanceState.getParcelable(TRACK);
            mWaypoints = (Waypoint[]) savedInstanceState.getParcelableArray(WAYPOINTS);

            if(mTrack != null) {
                getSupportActionBar().setTitle(mTrack.getName());
            }
        }  else {
            // Retrieving track id
            int trackId = getIntent().getParcelableExtra(RouteTrackerConstants.INTENT_NAME_FOR_TRACK_ID);
            // TODO retrieve track and waypoint from database
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
        outState.putParcelable(TRACK, mTrack);
        outState.putParcelableArray(WAYPOINTS, mWaypoints);
    }
}
