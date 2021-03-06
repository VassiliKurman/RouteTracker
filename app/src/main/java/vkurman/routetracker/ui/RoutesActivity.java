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

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Objects;
import java.util.UUID;

import vkurman.routetracker.R;
import vkurman.routetracker.firebase.FirebaseInterface;
import vkurman.routetracker.loader.TracksLoader;
import vkurman.routetracker.model.Track;
import vkurman.routetracker.utils.RouteTrackerConstants;

/**
 * RoutesActivity
 * Created by Vassili Kurman on 27/02/2018.
 * Version 1.0
 */
public class RoutesActivity extends AppCompatActivity implements RoutesFragment.OnItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener,
        FirebaseInterface.FirebaseInterfaceListener {

    /**
     * Tag for logging
     */
    private static final String TAG = RoutesActivity.class.getSimpleName();
    /**
     * Unique id for local tracks loader
     */
    private int mTracksLoaderId;
    /**
     * Current menu item selected
     */
    private int mCurrentMenuItem = R.id.nav_my_tracks;
    /**
     * Layout for navigation drawer
     */
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

            // Setup toolbar
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                ActionBar actionbar = getSupportActionBar();
                actionbar.setDisplayHomeAsUpEnabled(true);
                actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
            }

            mDrawerLayout = findViewById(R.id.drawer_layout);

            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                            // Detaching listener if menu item is different than shared tracks
                            if (mCurrentMenuItem == R.id.nav_shared_tracks && mCurrentMenuItem != menuItem.getItemId()) {
                                FirebaseInterface.getInstance().detachListener();
                            }

                            mCurrentMenuItem = menuItem.getItemId();
                            // set item as selected to persist highlight
                            menuItem.setChecked(true);
                            // change title in action bar
                            switch (menuItem.getItemId()) {
                                case R.id.nav_my_tracks: {
                                    if (getSupportActionBar() != null) {
                                        getSupportActionBar().setTitle(menuItem.getTitle());
                                    }
                                    retrieveData();
                                    break;
                                }
                                case R.id.nav_shared_tracks: {
                                    if (getSupportActionBar() != null) {
                                        getSupportActionBar().setTitle(menuItem.getTitle());
                                    }
                                    retrieveSharedData();
                                    break;
                                }
                                case R.id.nav_credits: {
                                    Intent intent = new Intent(RoutesActivity.this, CreditsActivity.class);
                                    startActivity(intent);
                                    break;
                                }
                                case R.id.nav_about: {
                                    Intent intent = new Intent(RoutesActivity.this, AboutActivity.class);
                                    startActivity(intent);
                                    break;
                                }
                            }
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                            return true;
                        }
                    });

            defaultSetup();
            mTracksLoaderId = TracksLoader.ID;
            if(savedInstanceState == null) {
                retrieveData();
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
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
            Log.d(TAG, "Passing new data to fragment from onLoadFinished() for local tracks");
            fragment.updateData(data);
        } else {
            Toast.makeText(this, R.string.text_error_finding_fragment, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {}

    @Override
    public void onItemSelected(long trackId) {
        Intent intent = new Intent(this, TrackDetailsActivity.class);
        intent.putExtra(RouteTrackerConstants.INTENT_EXTRA_NAME_FOR_TRACK_ID, trackId);
        intent.putExtra(RouteTrackerConstants.INTENT_EXTRA_IS_TRACK_SHARED, false);
        startActivity(intent);
    }

    @Override
    public void onSharedItemSelected(long trackId) {
        Intent intent = new Intent(this, SharedTrackDetailsActivity.class);
        intent.putExtra(RouteTrackerConstants.INTENT_EXTRA_NAME_FOR_TRACK_ID, trackId);
        intent.putExtra(RouteTrackerConstants.INTENT_EXTRA_IS_TRACK_SHARED, true);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Entering onActivityResult() method...");
        if(requestCode == RouteTrackerConstants.ROUTES_ACTIVITY_REQUEST_CODE_FOR_RESULT) {
            Log.d(TAG, "********************* Result code: " + resultCode + " *******************");
            if(resultCode == RouteTrackerConstants.TRACK_DETAILS_ACTIVITY_RESULT_CODE_CREATED) {
                Log.d(TAG, "Track created");
                retrieveData();
            }
        }
    }

    @Override
    public void onRefresh() {
        switch (mCurrentMenuItem) {
            case R.id.nav_my_tracks: {
                retrieveData();
                break;
            }
            case R.id.nav_shared_tracks: {
                retrieveSharedData();
                break;
            }
        }
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
     * Calling Loader to retrieve shared data from Firebase Database
     */
    private void retrieveSharedData() {
        RoutesFragment fragment = (RoutesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_master_list);
        if(fragment != null) {
            Log.d(TAG, "Setting data to null before update");
            fragment.updateData(null);
        } else {
            Toast.makeText(this, R.string.text_error_finding_fragment, Toast.LENGTH_LONG).show();
        }

        FirebaseInterface.getInstance().attachListener(this);
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

    @Override
    public void onTrackAdded(Track track) {
        if(track == null) {
            return;
        }
        RoutesFragment fragment = (RoutesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_master_list);
        if(fragment != null) {
            Log.d(TAG, "Passing new data to fragment from onTrackAdded() for shared tracks");
            fragment.addTrack(track);
        } else {
            Toast.makeText(this, R.string.text_error_finding_fragment, Toast.LENGTH_LONG).show();
        }
    }
}
