package vkurman.routetracker.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import vkurman.routetracker.R;
import vkurman.routetracker.firebase.FirebaseInterface;
import vkurman.routetracker.loader.SharedTrackDetailsLoader;
import vkurman.routetracker.loader.TrackDetailsLoader;
import vkurman.routetracker.model.Track;
import vkurman.routetracker.model.Waypoint;
import vkurman.routetracker.utils.RouteTrackerConstants;
import vkurman.routetracker.utils.RouteTrackerUtils;

/**
 *
 */
public class SharedTrackDetailsActivity extends AppCompatActivity {

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
    private ArrayList mWaypoints;
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
        // Track loader id
        mTracksLoaderId = SharedTrackDetailsLoader.ID;

        if(savedInstanceState != null) {
            mTrackId = savedInstanceState.getLong(TRACK_ID);
            mTrack = savedInstanceState.getParcelable(TRACK);
            mWaypoints = (ArrayList) savedInstanceState.getParcelableArrayList(WAYPOINTS);
            // Setting title for actionBar
            if(mTrack != null) {
                getSupportActionBar().setTitle(mTrack.getName());
                // Displaying track details
                displayData();
            }
        }  else {
            mWaypoints = new ArrayList();
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
        outState.putParcelableArrayList(WAYPOINTS, mWaypoints);
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