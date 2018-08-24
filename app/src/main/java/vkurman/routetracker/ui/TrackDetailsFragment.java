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

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import vkurman.routetracker.R;
import vkurman.routetracker.model.Track;
import vkurman.routetracker.model.Waypoint;
import vkurman.routetracker.utils.RouteTrackerUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrackDetailsFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    private static final String TRACK = "track";
    private static final String WAYPOINTS = "waypoints";

    /**
     * Reference to Track
     */
    private Track mTrack;
    /**
     * List of locations
     */
    private Waypoint[] mWaypoints;
    /**
     * Google Map
     */
    private GoogleMap mMap;

    private boolean isMapReady;
    private boolean areWaypointsReady;

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

    public TrackDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_track_details, container, false);
        // Binding views
        ButterKnife.bind(this, rootView);
        isMapReady = false;
        areWaypointsReady = false;

        // Load the saved state (the Parcelable step) if there is one
        if (savedInstanceState != null) {
            mTrack = savedInstanceState.getParcelable(TRACK);
            mWaypoints = (Waypoint[]) savedInstanceState.getParcelableArray(WAYPOINTS);
            areWaypointsReady = true;
            if (mTrack != null) {
                displayData();
                if (isMapReady) {
                    displayWaypointsOnMap();
                }
            }
        }

        return rootView;
    }

    /**
     * Setting track.
     *
     * @param track - provided track
     */
    public void setTrackData(Track track, Waypoint[] waypoints) {
        mTrack = track;
        mWaypoints = waypoints;
        // Displaying data
        displayData();
        if (isMapReady) {
            displayWaypointsOnMap();
        }
    }

    /**
     * Save the current state of this fragment
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle currentState) {
        currentState.putParcelable(TRACK, mTrack);
        currentState.putParcelableArray(WAYPOINTS, mWaypoints);
    }

    /**
     * Filling up and displaying data in the views.
     */
    private void displayData() {
        if (mTrack != null) {
            mTextTrackId.setText(String.format(Locale.getDefault(), "%d", mTrack.getId()));
            mTextTrackOwner.setText(TextUtils.isEmpty(mTrack.getOwnerName()) ? mTrack.getOwner() : mTrack.getOwnerName());
            mTextTrackWaypoints.setText(mWaypoints == null ? "null" : String.valueOf(mWaypoints.length));
            mTextTrackTimestamp.setText(RouteTrackerUtils.convertMillisecondsToDateTimeFormat(mTrack.getId()));
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                isMapReady = true;
                if (areWaypointsReady) {
                    displayWaypointsOnMap();
                }
            }
        });
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    /**
     * Displaying waypoints on the map. it should be called when bot map is ready and wypoints are loaded.
     */
    private void displayWaypointsOnMap() {
        // Displaying points on the map
        if (mWaypoints != null && mWaypoints.length > 0 && mMap != null) {
            LatLng firstLatLng = new LatLng(mWaypoints[0].getLatitude(), mWaypoints[0].getLongitude());
            mMap.addMarker(new MarkerOptions().position(firstLatLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(firstLatLng));
            mMap.moveCamera(CameraUpdateFactory.zoomIn());
            if(mWaypoints.length > 1) {
                LatLng lastLatLng = new LatLng(mWaypoints[mWaypoints.length - 1].getLatitude(), mWaypoints[mWaypoints.length - 1].getLongitude());
                mMap.addMarker(new MarkerOptions().position(lastLatLng));
                PolylineOptions rectOptions = new PolylineOptions();
                for (Waypoint point : mWaypoints) {
                    rectOptions.add(new LatLng(point.getLatitude(), point.getLongitude()));
                }
                // Get back the mutable Polyline
                mMap.addPolyline(rectOptions);
            }
        }
    }
}