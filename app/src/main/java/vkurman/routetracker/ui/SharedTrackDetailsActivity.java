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

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vkurman.routetracker.R;
import vkurman.routetracker.firebase.FirebaseInterface;
import vkurman.routetracker.model.Track;
import vkurman.routetracker.model.Waypoint;
import vkurman.routetracker.utils.RouteTrackerConstants;
import vkurman.routetracker.utils.RouteTrackerUtils;

/**
 * SharedTrackDetailsActivity
 *
 * Created by Vassili Kurman on 22/08/2018.
 * Version 1.0
 */
public class SharedTrackDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Initial track id
    private static final long INITIAL_TRACK_ID = -1;
    // Keys for saved instance
    private static final String TRACK_ID = "trackId";
    private static final String TRACK = "track";
    private static final String WAYPOINTS = "waypoints";

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
    private List<Waypoint> mWaypoints;
    /**
     * Google Map
     */
    private GoogleMap mMap;
    /**
     * Binding Views
     */
    @BindView(R.id.text_track_id)
    TextView mTextTrackId;
    @BindView(R.id.text_track_owner)
    TextView mTextTrackOwner;
    @BindView(R.id.text_track_waypoints)
    TextView mTextTrackWaypoints;
    @BindView(R.id.text_track_timestamp)
    TextView mTextTrackTimestamp;

    DatabaseReference mTrackDatabaseReference;
    DatabaseReference mWaypointsDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_track_details);
        // Binding views
        ButterKnife.bind(this);
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show the Up button and title in the action bar.
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_details_activity);
        }
        // Initial track id
        mTrackId = INITIAL_TRACK_ID;

        if(savedInstanceState != null) {
            mTrackId = savedInstanceState.getLong(TRACK_ID);
            mTrack = savedInstanceState.getParcelable(TRACK);
            mWaypoints = savedInstanceState.getParcelableArrayList(WAYPOINTS);
            // Setting title for actionBar
            if(mTrack != null) {
                getSupportActionBar().setTitle(mTrack.getName());
                // Displaying track details
                displayData();
            }
        }  else {
            mWaypoints = new ArrayList<>();
            // Checking that intent has extra in it
            if(getIntent().hasExtra(RouteTrackerConstants.INTENT_EXTRA_NAME_FOR_TRACK_ID)) {
                // Retrieving track id
                mTrackId = getIntent().getLongExtra(RouteTrackerConstants.INTENT_EXTRA_NAME_FOR_TRACK_ID, INITIAL_TRACK_ID);
                if(mTrackId != INITIAL_TRACK_ID) {
                    // Retrieving data
                    retrieveData();
                }
            }
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_container);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // if track is not shared one, return relevant value
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
        outState.putParcelableArrayList(WAYPOINTS, (ArrayList<? extends Parcelable>) mWaypoints);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                // Displaying points on the map
                if(mWaypoints != null && mWaypoints.size() > 0) {
                    LatLngBounds.Builder builder = LatLngBounds.builder();

                    LatLng firstLatLng = new LatLng(mWaypoints.get(0).getLatitude(), mWaypoints.get(0).getLongitude());
                    LatLng lastLatLng = new LatLng(mWaypoints.get(mWaypoints.size() - 1).getLatitude(), mWaypoints.get(mWaypoints.size() - 1).getLongitude());
                    mMap.addMarker(new MarkerOptions().position(firstLatLng));
                    mMap.addMarker(new MarkerOptions().position(lastLatLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(firstLatLng));
                    mMap.moveCamera(CameraUpdateFactory.zoomIn());
                    PolylineOptions rectOptions = new PolylineOptions();
                    for(Waypoint point : mWaypoints) {
                        LatLng ll = new LatLng(point.getLatitude(), point.getLongitude());
                        builder.include(ll);
                        rectOptions.add(ll);
                    }
                    // Get back the mutable Polyline
                    mMap.addPolyline(rectOptions);

                    LatLngBounds bounds = builder.build();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
                }
            }
        });
    }

    /**
     * Calling Loader to retrieve data from database
     */
    private void retrieveData() {
        String trackIdString = Long.toString(mTrackId);
        mTrackDatabaseReference = FirebaseInterface.getInstance().getTrackDatabaseReference(trackIdString);
        mTrackDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mTrack = dataSnapshot.getValue(Track.class);
                displayData();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        mWaypointsDatabaseReference = FirebaseInterface.getInstance().getWaypointsDatabaseReference(trackIdString);
        mWaypointsDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Waypoint waypoint = dataSnapshot.getValue(Waypoint.class);
                mWaypoints.add(waypoint);
                mTextTrackWaypoints.setText(String.valueOf(mWaypoints.size()));
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void displayData() {
        mTextTrackId.setText(String.valueOf(mTrack.getId()));
        mTextTrackOwner.setText(TextUtils.isEmpty(mTrack.getOwnerName()) ? mTrack.getOwner() : mTrack.getOwnerName());
        mTextTrackWaypoints.setText(String.valueOf(mWaypoints.size()));
        mTextTrackTimestamp.setText(RouteTrackerUtils.convertMillisecondsToDateTimeFormat(mTrack.getId()));
    }
}