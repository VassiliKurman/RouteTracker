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
package vkurman.routetracker.firebase;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

import vkurman.routetracker.model.Track;
import vkurman.routetracker.model.Waypoint;
import vkurman.routetracker.utils.RouteTrackerConstants;

/**
 * FirebaseInterface
 * Created by Vassili Kurman on 12/08/2018.
 * Version 1.0
 */
public class FirebaseInterface {

    private static FirebaseInterface mFirebaseInterface;
    private static FirebaseDatabase mFirebaseDatabase;
    private static DatabaseReference mDatabaseReferenceTracks;
    private static DatabaseReference mDatabaseReferenceWaypoints;
    private ChildEventListener mChildEventListener;

    private static FirebaseInterfaceListener mListener;
    private static List<Track> track;

    private FirebaseInterface(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReferenceTracks = mFirebaseDatabase.getReference().child(RouteTrackerConstants.FIREBASE_DATABASE_REFERENCE_TRACKS);
        mDatabaseReferenceWaypoints = mFirebaseDatabase.getReference().child(RouteTrackerConstants.FIREBASE_DATABASE_REFERENCE_WAYPOINTS);
    }

    public static FirebaseInterface getInstance(){
        if(mFirebaseInterface == null) {
            mFirebaseInterface = new FirebaseInterface();
        }
        return mFirebaseInterface;
    }

    /**
     * Save new track to database.
     *
     * @param track - Track
     * @param waypoints - Waypoint[]
     */
    public static void shareTrack(Track track, Waypoint[] waypoints) {
        if(track == null || waypoints == null) {
            return;
        }
        mDatabaseReferenceTracks.child(Long.toString(track.getId())).setValue(track);

        List<Waypoint> list = Arrays.asList(waypoints);
        shareWaypoints(track, list);
    }

    /**
     * Called by shareTrack() method to store waypoints
     *
     * @param track - Track
     * @param waypoints - List<Waypoint>
     */
    private static void shareWaypoints(Track track, List<Waypoint> waypoints) {
        mDatabaseReferenceWaypoints.child(Long.toString(track.getId())).setValue(waypoints);
    }

    public interface FirebaseInterfaceListener {
        void onTrackAdded(Track track);
    }

    public void attachListener(FirebaseInterfaceListener listener) {
        mListener = listener;
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Track track = dataSnapshot.getValue(Track.class);
                mListener.onTrackAdded(track);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDatabaseReferenceTracks.addChildEventListener(mChildEventListener);
    }

    public void detachListener() {
        mDatabaseReferenceTracks.removeEventListener(mChildEventListener);
        mListener = null;
    }

    public DatabaseReference getTrackDatabaseReference(String trackId) {
        return mDatabaseReferenceTracks.child(trackId);
    }

    public DatabaseReference getWaypointsDatabaseReference(String trackId) {
        return mDatabaseReferenceWaypoints.child(trackId);
    }
}