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
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Objects;
import java.util.UUID;

import vkurman.routetracker.R;
import vkurman.routetracker.loader.TracksLoader;
import vkurman.routetracker.utils.RouteTrackerConstants;

/**
 * RoutesActivity
 * Created by Vassili Kurman on 27/02/2018.
 * Version 1.0
 */
public class RoutesActivity extends AppCompatActivity implements RoutesFragment.OnItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = RoutesActivity.class.getSimpleName();
    /**
     * Unique id for loader
     */
    private int mTracksLoaderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        defaultSetup();

        mTracksLoaderId = TracksLoader.ID;
        retrieveData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new TracksLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if(data == null) {
            return;
        }
        RoutesFragment fragment = (RoutesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_master_list);
        if(fragment != null) {
            Log.d(TAG, "Passing new data to fragment");
            fragment.updateData(data);
        } else {
            Toast.makeText(this, "Error finding fragment with list of tracks", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {}

    @Override
    public void onItemSelected(long trackId) {
        Intent intent = new Intent(this, TrackDetailsActivity.class);
        intent.putExtra(RouteTrackerConstants.INTENT_NAME_FOR_TRACK_ID, trackId);
        startActivityForResult(intent, RouteTrackerConstants.ROUTES_ACTIVITY_REQUEST_CODE_FOR_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RouteTrackerConstants.ROUTES_ACTIVITY_REQUEST_CODE_FOR_RESULT) {
            if(resultCode == Activity.RESULT_OK) {
                long fragment_response_code = data.getLongExtra(
                        RouteTrackerConstants.INTENT_EXTRA_FOR_RESULT_CODE,
                        RouteTrackerConstants.TRACK_DETAILS_ACTIVITY_RESULT_CODE_UNCHANGED);
                if(fragment_response_code == RouteTrackerConstants.TRACK_DETAILS_ACTIVITY_RESULT_CODE_CREATED) {
                    Log.d(TAG, "Track created");
                    retrieveData();
                } else if(fragment_response_code == RouteTrackerConstants.TRACK_DETAILS_ACTIVITY_RESULT_CODE_UPDATED) {
                    Log.d(TAG, "Track updated");
                    retrieveData();
                } else if(fragment_response_code == RouteTrackerConstants.TRACK_DETAILS_ACTIVITY_RESULT_CODE_DELETED) {
                    Log.d(TAG, "Track deleted");
                    retrieveData();
                }
            }
        }
    }

    @Override
    public void onRefresh() {
        retrieveData();
    }

    /**
     * Calling Loader to retrieve data from database
     */
    private void retrieveData() {
        if(getSupportLoaderManager().getLoader(mTracksLoaderId) == null) {
            getSupportLoaderManager().initLoader(mTracksLoaderId, null, this).forceLoad();
        } else {
            Objects.requireNonNull(getSupportLoaderManager().getLoader(mTracksLoaderId)).forceLoad();
        }
    }

    /**
     * App default setup.
     */
    private void defaultSetup() {
        // Setting default values for the preferences when app started first time.
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String defaultUserId = getString(R.string.pref_value_default_user_id);
        String userId = sharedPreferences.getString(
                getString(R.string.pref_key_default_user_id),
                defaultUserId);
        if(userId.equals(defaultUserId)) {
            // Setting up user id
            userId = UUID.randomUUID().toString();
            sharedPreferences.edit().putString(getString(R.string.pref_key_default_user_id), userId).apply();
        }
    }
}
