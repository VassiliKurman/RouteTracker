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

import vkurman.routetracker.model.Track;
import vkurman.routetracker.model.User;
import vkurman.routetracker.model.Waypoint;

/**
 * TrackerDbUtils
 * Created by Vassili Kurman on 07/08/2018.
 * Version 1.0
 */
public class TrackerDbUtils {
    /**
     * Saves user into database
     *
     * @param context
     * @param user
     */
    public static Uri addUser(Context context, User user) {
        // Create new empty ContentValues object
        ContentValues userContentValues = new ContentValues();
        // Put the task description and selected mPriority into the ContentValues
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
     */
    public static Uri addTrack(Context context, Track track) {
        // Create new empty ContentValues object
        ContentValues trackContentValues = new ContentValues();
        // Put the task description and selected mPriority into the ContentValues
        trackContentValues.put(TrackerContract.TracksEntry.COLUMN_TRACKS_ID, track.getId());
        trackContentValues.put(TrackerContract.TracksEntry.COLUMN_TRACKS_NAME, track.getName());
        trackContentValues.put(TrackerContract.TracksEntry.COLUMN_TRACKS_OWNER, track.getOwner());
        trackContentValues.put(TrackerContract.TracksEntry.COLUMN_TRACKS_IMAGE, track.getImage());
        // Insert the content values via a ContentResolver
        return context.getContentResolver().insert(TrackerContract.TracksEntry.CONTENT_URI_TRACKS, trackContentValues);
    }
    /**
     * Saves waypoint into database
     *
     * @param context
     * @param waypoint
     */
    public static Uri addWaypoint(Context context, Waypoint waypoint) {
        // Create new empty ContentValues object
        ContentValues waypointContentValues = new ContentValues();
        // Put the task description and selected mPriority into the ContentValues
        waypointContentValues.put(TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_ID, waypoint.getId());
        waypointContentValues.put(TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_TRACK_ID, waypoint.getTrackId());
        waypointContentValues.put(TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_TIMESTAMP, waypoint.getTimeStamp());
        waypointContentValues.put(TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_LATITUDE, waypoint.getLatitude());
        waypointContentValues.put(TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_LONGITUDE, waypoint.getLongitude());
        waypointContentValues.put(TrackerContract.WaypointsEntry.COLUMN_WAYPOINTS_ALTITUDE, waypoint.getAltitude());
        // Insert the content values via a ContentResolver
        return context.getContentResolver().insert(TrackerContract.UsersEntry.CONTENT_URI_USERS, waypointContentValues);
    }
}