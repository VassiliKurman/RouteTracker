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
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import vkurman.routetracker.R;
import vkurman.routetracker.model.Track;
import vkurman.routetracker.utils.RouteTrackerUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrackDetailsFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    private static final String TRACK = "track";
    /**
     * Reference to Track
     */
    private Track mTrack;
    // Google Map
    private GoogleMap mMap;
    // List of Locations

    /**
     * Binding Views
     */
    @BindView(R.id.text_track_name)
    TextView mTextTrackName;
    @BindView(R.id.text_track_id)
    TextView mTextTrackId;
    @BindView(R.id.text_track_owner)
    TextView mTextTrackOwner;
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

        // Load the saved state (the Parcelable step) if there is one
        if(savedInstanceState != null) {
            mTrack = savedInstanceState.getParcelable(TRACK);
            if(mTrack != null) {
                displayData();
            }
        }

        return rootView;
    }

    /**
     * Setting track.
     *
     * @param track - provided track
     */
    public void setTrack(Track track) {
        mTrack = track;
        displayData();
    }

    /**
     * Save the current state of this fragment
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle currentState) {
        currentState.putParcelable(TRACK, mTrack);
    }



    /**
     * Filling up and displaying data in the views.
     */
    private void displayData() {
        if(mTrack != null) {
            mTextTrackName.setText(TextUtils.isEmpty(mTrack.getName()) ? getString(R.string.text_unspecified) : mTrack.getName());
            mTextTrackId.setText(String.format("%d", mTrack.getId()));
            mTextTrackOwner.setText(mTrack.getOwner());
            mTextTrackTimestamp.setText(RouteTrackerUtils.convertMillisecondsToDateTimeFormat(mTrack.getId()));
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        // Check for location permission.
        mMap.setMyLocationEnabled(false);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getContext(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }
}