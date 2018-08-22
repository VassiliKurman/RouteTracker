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

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import vkurman.routetracker.R;
import vkurman.routetracker.model.RouteManager;
import vkurman.routetracker.model.Track;
import vkurman.routetracker.model.Waypoint;
import vkurman.routetracker.provider.TrackerDbUtils;
import vkurman.routetracker.receiver.LocationReceiver;
import vkurman.routetracker.utils.RouteTrackerConstants;
import vkurman.routetracker.utils.RouteTrackerUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewRouteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 *
 * Created by Vassili Kurman on 05/08/2018.
 * Version 1.0
 */
public class NewRouteFragment extends Fragment implements View.OnClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    /**
     * Tag for logging
     */
    public static final String TAG = NewRouteFragment.class.getSimpleName();
    /**
     * Parent activity listens for this fragments interactions
     */
    private OnFragmentInteractionListener mListener;
    /**
     * Toast
     */
    private Toast mToast;
    /**
     * Google Map
     */
    private GoogleMap mMap;
    /**
     * Starting location
     */
    private Location mStartLocation;
    /**
     * Current track id
     */
    private long mCurrentTrackId;

    @BindView(R.id.text_timestamp)
    TextView mTextTimestamp;
    @BindView(R.id.text_latitude)
    TextView mTextLatitude;
    @BindView(R.id.text_longitude)
    TextView mTextLongitude;
    @BindView(R.id.text_altitude)
    TextView mTextAltitude;
    @BindView(R.id.text_duration)
    TextView mTextDuration;
    @BindView(R.id.btn_start)
    Button mStartButton;
    @BindView(R.id.btn_stop)
    Button mStopButton;

    private LocationReceiver mLocationReceiver = new LocationReceiver(){
        @Override
        protected void locationChanged(Context context, Location location) {
            Log.d(TAG, "Entered LocationReceiver.locationChanged()");
            if(location != null) {
                if(mStartLocation == null) {
                    mStartLocation = location;
                }
                // Adding Track to Database
                TrackerDbUtils.addWaypoint(
                        context, new Waypoint(
                                location.getTime(),
                                mCurrentTrackId,
                                location.getLatitude(),
                                location.getLongitude(),
                                location.getAltitude(),
                                location.getTime()));

                // Putting Marker to map
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(currentLatLng));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));

                mTextTimestamp.setText(RouteTrackerUtils.convertMillisecondsToDateTimeFormat(location.getTime()));
                mTextLatitude.setText(String.format(Locale.getDefault(), "%f", location.getLatitude()));
                mTextLongitude.setText(String.format(Locale.getDefault(), "%f", location.getLongitude()));
                mTextAltitude.setText(String.format(Locale.getDefault(), "%f", location.getAltitude()));
                mTextDuration.setText(RouteTrackerUtils.convertMillisecondsToFormattedString(location.getTime() - mStartLocation.getTime()));
            } else {
                Toast.makeText(getContext(), "Last known location is missing", Toast.LENGTH_SHORT).show();
            }
            Log.d(TAG, "... exiting LocationReceiver.locationChanged()");
        }

        @Override
        public void providerStateChanged(boolean enabled) {
            Toast.makeText(getActivity(), "Location provider " + (enabled ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        }
    };

    public NewRouteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_route, container, false);
        // Binding views
        ButterKnife.bind(this, view);

        mStartButton.setEnabled(true);
        mStartButton.setOnClickListener(this);
        mStopButton.setEnabled(false);
        mStopButton.setOnClickListener(this);

        // Map fragment
        SupportMapFragment mapFragment =
                (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map_container);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mLocationReceiver,
                new IntentFilter(RouteManager.ACTION_LOCATION));
    }
    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mLocationReceiver);
        super.onStop();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(int code);
        void onTrackNameRequest();
    }

    @Override
    public void onClick(View view) {
        if (view == mStartButton) {
            final LocationManager manager = (LocationManager) getActivity().getSystemService( Context.LOCATION_SERVICE );
            if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                requestToEnableGps();
            } else {
                if(permissionsGranted()) {
                    // Notifying listener about change
                    if(mListener != null) {
                        mListener.onFragmentInteraction(RouteTrackerConstants.TRACK_DETAILS_ACTIVITY_RESULT_CODE_CREATED);
                    }
                    // Ask to enter track name
                    if(mListener != null) {
                        mListener.onTrackNameRequest();
                    }
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            RouteTrackerConstants.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    return;
                }
            }
        } else if (view == mStopButton) {
            // Stopping location tracking
            stopLocationTracking();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RouteTrackerConstants.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    if(mToast != null) {
                        mToast.cancel();
                    }
                    mToast.makeText(getContext(), "Permissions granted", Toast.LENGTH_SHORT).show();
                    // permission was granted!
                    // Ask to enter track name
                    if(mListener != null) {
                        mListener.onTrackNameRequest();
                    }
                } else {
                    // permission denied
                    if(mToast != null) {
                        mToast.cancel();
                    }
                    mToast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    /**
     * Checking is all necessary permissions are granted
     *
     * @return boolean
     */
    private boolean permissionsGranted() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Starts tracking location either after permissions are granted (if needed) or
     * straight away after start button is pressed (if permissions are granted earlier).
     */
    @SuppressLint("MissingPermission")
    protected void startLocationTracking(String trackName) {
        mStartButton.setEnabled(false);
        if (!RouteManager.getInstance().isTracking(getActivity())) {
            mCurrentTrackId  = Calendar.getInstance().getTimeInMillis();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            String userId = prefs.getString(getString(R.string.pref_key_default_user_id), getString(R.string.pref_value_default_user_id));
            String userName = prefs.getString(getString(R.string.pref_key_default_user_name), getString(R.string.pref_value_default_user_name));
            // creating track
            Track track = new Track(mCurrentTrackId, trackName, userId, userName, null);
            // request to start tracking
            RouteManager.getInstance().startTracking(getActivity(), track);
        }
        mStopButton.setEnabled(true);
    }

    /**
     * Stopping location tracking.
     */
    private void stopLocationTracking() {
        mStopButton.setEnabled(false);
        // stop tracking
        if (RouteManager.getInstance().isTracking(getActivity())) {
            RouteManager.getInstance().stopTracking(getActivity());
        }
        mStartButton.setEnabled(true);
    }

    /**
     * Requesting to switch on GPS
     */
    private void requestToEnableGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("GPS is disabled! Do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        // Check for location permission.
        if (permissionsGranted()) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {}

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }
}