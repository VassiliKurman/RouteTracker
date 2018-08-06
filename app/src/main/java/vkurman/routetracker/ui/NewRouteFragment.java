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
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import vkurman.routetracker.R;
import vkurman.routetracker.model.RouteManager;
import vkurman.routetracker.model.Track;
import vkurman.routetracker.receiver.LocationReceiver;
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
     * MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
     * app-defined int constant. The callback method gets the
     * result of the request.
     */
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    // Parent activity listens for this fragments interactions
    private OnFragmentInteractionListener mListener;
    // Toast
    private Toast mToast;
    // Google Map
    private GoogleMap mMap;
    // List of Locations
    private List<Location> locations;

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
        protected void locationChanged(Location location) {
            if(location != null) {
                Toast.makeText(getContext(), "Retrieved last location", Toast.LENGTH_SHORT).show();
                if(locations == null) {
                    locations = new ArrayList<>();
                }

                locations.add(location);

                mTextTimestamp.setText(String.format(Locale.getDefault(), "%d milliseconds", location.getTime()));
                mTextLatitude.setText(String.format(Locale.getDefault(), "%f", location.getLatitude()));
                mTextLongitude.setText(String.format(Locale.getDefault(), "%f", location.getLongitude()));
                mTextAltitude.setText(String.format(Locale.getDefault(), "%f", location.getAltitude()));
                mTextDuration.setText(RouteTrackerUtils.convertMillisecondsToFormattedString(location.getTime() - locations.get(0).getTime()));
            } else {
                Toast.makeText(getContext(), "Last known location is missing", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void providerStateChanged(boolean enabled) {
            Toast.makeText(getActivity(), "Provider " + (enabled ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View view) {
        if (view == mStartButton) {
            // Displaying Toast
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(getContext(), "Started tracking!", Toast.LENGTH_SHORT);
            mToast.show();
            // TODO start tracking
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            } else {
                startLocationTracking();
            }
        } else if (view == mStopButton) {
            mStopButton.setEnabled(false);
            // Displaying Toast
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(getContext(), "Stopped tracking!", Toast.LENGTH_SHORT);
            mToast.show();
            // stop tracking
            if (RouteManager.getInstance().isTracking(getActivity())) {
                RouteManager.getInstance().stopLocationUpdates(getActivity());
            }
            mStartButton.setEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted! Do the task that need to do.
                    startLocationTracking();
                } else {
                    // permission denied! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationTracking() {
        mStartButton.setEnabled(false);
        mStopButton.setEnabled(true);
        if (!RouteManager.getInstance().isTracking(getActivity())) {
            RouteManager.getInstance().startLocationUpdates(getActivity());
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        // Check for location permission.
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
        }
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
