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
package vkurman.routetracker.provider;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import vkurman.routetracker.model.Track;
import vkurman.routetracker.model.User;
import vkurman.routetracker.model.Waypoint;

/**
 * TrackerDbUtils is a helper class to simplify CRUD operations: creating,retrieving,
 * updating and deleting operations in database.
 *
 * Created by Vassili Kurman on 07/08/2018.
 * Version 1.0
 */
public class TrackerDbUtils {

    private static final String TAG = TrackerDbUtils.class.getSimpleName();
    /**
     * Saves user into database
     *
     * @param context
     * @param user
     * @return Uri - uri pointing to row inserted
     */
    public static Uri addUser(Context context, User user) {
        // Create new empty ContentValues object
        ContentValues userContentValues = new ContentValues();
        // Put data into the ContentValues
        userContentValues.put(TrackerContract.UsersEntry.COLUMN_USERS_ID, user.getId());
        userContentValues.put(TrackerContract.UsersEntry.COLUMN_USERS_NAME, user.getName());
        // Insert the content values via a ContentResolver
        return context.getContentResolver().insert(TrackerContract.UsersEntry.CONTENT_URI_USERS, userContentValues);
    }

    /**
     * Saves track into database. No waypoints are saved in here.
     *
     * @param context
     * @param track
     * @return Uri - uri pointing to row inserted
     */
    public static Uri addTrack(Context context, Track track) {
        // Create new empty ContentValues object
        ContentValues trackContentValues = new ContentValues();
        // Put data into the ContentValues
        trackContentValues.put(TrackerContract.TracksEntry.COLUMN_TRACKS_ID, track.getId());
        trackContentValues.put(TrackerContract.TracksEntry.COLUMN_TRACKS_NAME, track.getName());
        trackContentValues.put(TrackerContract.TracksEntry.COLUMN_TRACKS_OWNER, track.getOwner());
        trackContentValues.put(TrackerContract.TracksEntry.COLUMN_TRACKS_OWNER_NAME, track.getOwnerName());
        trackContentValues.put(TrackerContract.TracksEntry.COLUMN_TRACKS_IMAGE, track.getImage());
        // Insert the content values via a ContentResolver
        return context.getContentResolver().insert(TrackerContract.TracksEntry.CONTENT_URI_TRACKS, trackContentValues);
    }

    /**
     * Saves waypoint into database
     *
     * @param context
     * @param waypoint
     * @return Uri - uri pointing to row inserted
     */
    public static Uri addWaypoint(Context context, Waypoint waypoint) {
        Log.d(TAG, "Entering addWaypoint()...");
        // Create new empty ContentValues object
        ContentValues waypointContentValues = new ContentValues();
        // Put data into the ContentValues
        waypointContentValues.put(TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_ID, waypoint.getId());
        waypointContentValues.put(TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_TRACK_ID, waypoint.getTrackId());
        waypointContentValues.put(TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_TIMESTAMP, waypoint.getTimeStamp());
        waypointContentValues.put(TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_LATITUDE, waypoint.getLatitude());
        waypointContentValues.put(TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_LONGITUDE, waypoint.getLongitude());
        waypointContentValues.put(TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_ALTITUDE, waypoint.getAltitude());
        Log.d(TAG, "... requesting insert waypoint and addWaypoint() exiting!");
        // Insert the content values via a ContentResolver
        return context.getContentResolver().insert(TrackerContract.WaypointsEntry.CONTENT_URI_WAYPOINTS, waypointContentValues);
    }

    /**
     * Removes user with specified id from database
     *
     * @param context
     * @param userId
     * @return int - number of rows deleted
     */
    public static int removeUser(Context context, String userId) {
        String selection = TrackerContract.UsersEntry.COLUMN_USERS_ID + "=?";
        String[] selectionArgs = new String[]{userId};
        // Delete the content values via a ContentResolver
        return context.getContentResolver().delete(TrackerContract.UsersEntry.CONTENT_URI_USERS, selection, selectionArgs);
    }

    /**
     * Removes track with specified id from database. No waypoints are removed in here.
     *
     * @param context
     * @param trackId
     * @return int - number of rows deleted
     */
    public static int removeTrack(Context context, long trackId) {
        String selection = TrackerContract.TracksEntry.COLUMN_TRACKS_ID + "=?";
        String[] selectionArgs = new String[]{Long.toString(trackId)};
        // Delete the content values via a ContentResolver
        return context.getContentResolver().delete(TrackerContract.TracksEntry.CONTENT_URI_TRACKS, selection, selectionArgs);
    }

    /**
     * Removes all waypoints from database for specified track id.
     *
     * @param context
     * @param trackId
     * @return int - number of rows deleted
     */
    public static int removeWaypoints(Context context, long trackId) {
        String selection = TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_TRACK_ID + "=?";
        String[] selectionArgs = new String[]{Long.toString(trackId)};
        // Delete the content values via a ContentResolver
        return context.getContentResolver().delete(TrackerContract.WaypointsEntry.CONTENT_URI_WAYPOINTS, selection, selectionArgs);
    }

    /**
     * Updates user in database
     *
     * @param context
     * @param user
     * @return int - number of rows updated
     */
    public static int updateUser(Context context, User user) {
        // Create new empty ContentValues object
        ContentValues userContentValues = new ContentValues();
        // Put data into the ContentValues
        userContentValues.put(TrackerContract.UsersEntry.COLUMN_USERS_NAME, user.getName());

        String selection = TrackerContract.UsersEntry.COLUMN_USERS_ID + "=?";
        String[] selectionArgs = new String[]{user.getId()};
        // Insert the content values via a ContentResolver
        return context.getContentResolver().update(TrackerContract.UsersEntry.CONTENT_URI_USERS, userContentValues, selection, selectionArgs);
    }

    /**
     * Updates track in database. No waypoints are updated in here.
     *
     * @param context
     * @param track
     * @return int - number of rows updated
     */
    public static int updateTrack(Context context, Track track) {
        // Create new empty ContentValues object
        ContentValues trackContentValues = new ContentValues();
        // Put data into the ContentValues
        trackContentValues.put(TrackerContract.TracksEntry.COLUMN_TRACKS_NAME, track.getName());
        trackContentValues.put(TrackerContract.TracksEntry.COLUMN_TRACKS_OWNER, track.getOwner());
        trackContentValues.put(TrackerContract.TracksEntry.COLUMN_TRACKS_OWNER_NAME, track.getOwnerName());
        trackContentValues.put(TrackerContract.TracksEntry.COLUMN_TRACKS_IMAGE, track.getImage());

        String selection = TrackerContract.TracksEntry.COLUMN_TRACKS_ID + "=?";
        String[] selectionArgs = new String[]{Long.toString(track.getId())};
        // Insert the content values via a ContentResolver
        return context.getContentResolver().update(TrackerContract.TracksEntry.CONTENT_URI_TRACKS, trackContentValues, selection, selectionArgs);
    }

    /**
     * Updates waypoint in database
     *
     * @param context
     * @param waypoint
     * @return int - number of rows updated
     */
    public static int updateWaypoint(Context context, Waypoint waypoint) {
        // Create new empty ContentValues object
        ContentValues waypointContentValues = new ContentValues();
        // Put data into the ContentValues
        waypointContentValues.put(TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_TIMESTAMP, waypoint.getTimeStamp());
        waypointContentValues.put(TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_LATITUDE, waypoint.getLatitude());
        waypointContentValues.put(TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_LONGITUDE, waypoint.getLongitude());
        waypointContentValues.put(TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_ALTITUDE, waypoint.getAltitude());

        String selection = TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_ID + "=?";
        String[] selectionArgs = new String[]{Long.toString(waypoint.getId())};
        // Insert the content values via a ContentResolver
        return context.getContentResolver().update(TrackerContract.WaypointsEntry.CONTENT_URI_WAYPOINTS, waypointContentValues, selection, selectionArgs);
    }
}