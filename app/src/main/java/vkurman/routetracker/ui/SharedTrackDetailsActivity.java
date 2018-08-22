package vkurman.routetracker.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import vkurman.routetracker.R;
import vkurman.routetracker.firebase.FirebaseInterface;
import vkurman.routetracker.loader.SharedTrackDetailsLoader;
import vkurman.routetracker.loader.TrackDetailsLoader;
import vkurman.routetracker.model.Track;
import vkurman.routetracker.model.Waypoint;
import vkurman.routetracker.utils.RouteTrackerConstants;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_details);
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
            }
        }  else {
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
        // TODO
    }
}