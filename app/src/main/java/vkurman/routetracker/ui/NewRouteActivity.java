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

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import vkurman.routetracker.R;
import vkurman.routetracker.model.RouteManager;
import vkurman.routetracker.utils.RouteTrackerConstants;

/**
 * NewRouteActivity is an Activity for creating new Track.
 *
 * Created by Vassili Kurman on 05/08/2018.
 * Version 1.0
 */
public class NewRouteActivity extends AppCompatActivity implements
        NewRouteFragment.OnFragmentInteractionListener,
        TrackNameDialogFragment.TrackNameDialogListener {

    /**
     * Default result code
     */
    private int mResultCode = RouteTrackerConstants.TRACK_DETAILS_ACTIVITY_RESULT_CODE_UNCHANGED;
    /**
     * Reference to child fragment
     */
    private NewRouteFragment mNewRouteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_route);
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show the Up button and title in the action bar.
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_new_track_activity);
        }

        // Track fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        mNewRouteFragment = new NewRouteFragment();
        fragmentManager.beginTransaction()
                .add(R.id.track_container, mNewRouteFragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (RouteManager.getInstance().isTracking(this)) {
                    Toast.makeText(this, "Switching track recording in background!", Toast.LENGTH_LONG).show();
                    // TODO save track in background
                }

                Intent intent = new Intent();
                intent.putExtra(
                        RouteTrackerConstants.INTENT_EXTRA_FOR_RESULT_CODE,
                        mResultCode);
                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFragmentInteraction(int code) {
        mResultCode = code;
    }

    @Override
    public void onTrackNameRequest() {
        // Create an instance of the dialog fragment and show it
        TrackNameDialogFragment dialog = new TrackNameDialogFragment();
        dialog.show(getSupportFragmentManager(), "TrackNameDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog){
        if(dialog instanceof TrackNameDialogFragment) {
            TrackNameDialogFragment d = (TrackNameDialogFragment) dialog;
            String trackName = d.getTrackName();
            if(!TextUtils.isEmpty(trackName)) {
                // Change Activity title
                if(getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(trackName);
                }
                mNewRouteFragment.startLocationTracking(trackName);
            } else {
                Toast.makeText(this, "Track name not specified", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog){
        // Do nothing as track name is not set
    }
}
