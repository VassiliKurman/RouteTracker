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

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * <code>TrackerContract</code> contract class is a container for constants that
 * define names for URIs, tables and columns. It allows to use same constants across
 * all the other classes in the same package.
 *
 * Created by Vassili Kurman on 01/08/2018.
 * Version 1.0
 */
public class TrackerContract {
    /**
     * The authority, which is how your code knows which Content Provider to access
     */
    public static final String AUTHORITY = "vkurman.routetracker";
    /**
     * The base content URI = "content://" + <authority>
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    /**
     * This is the path for the "users" directory
     */
    public static final String PATH_USERS = "users";
    /**
     * This is the path for the "tracks" directory
     */
    public static final String PATH_TRACKS = "tracks";
    /**
     * This is the path for the "waypoints" directory
     */
    public static final String PATH_WAYPOINTS = "waypoints";

    public static final long INVALID_USER_ID = -1;
    public static final long INVALID_TRACK_ID = -1;
    public static final long INVALID_WAYPOINT_ID = -1;

    /**
     * Inner class for User columns
     */
    public static final class UsersEntry implements BaseColumns {
        // Entry content URI = base content URI + path
        public static final Uri CONTENT_URI_USERS =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERS).build();
        // Users table
        public static final String TABLE_NAME_USERS = "users";
        public static final String COLUMN_USERS_ID = "id";
        public static final String COLUMN_USERS_NAME = "name";
    }

    /**
     * Inner class for Track columns
     */
    public static final class TracksEntry implements BaseColumns {
        // Entry content URI = base content URI + path
        public static final Uri CONTENT_URI_TRACKS =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRACKS).build();
        // Tracks table
        public static final String TABLE_NAME_TRACKS = "tracks";
        public static final String COLUMN_TRACKS_ID = "id";
        public static final String COLUMN_TRACKS_NAME = "name";
        public static final String COLUMN_TRACKS_OWNER = "owner";
        public static final String COLUMN_TRACKS_OWNER_NAME = "ownerName";
        public static final String COLUMN_TRACKS_IMAGE = "image";
    }

    /**
     * Inner class for Waypoint columns
     */
    public static final class WaypointsEntry implements BaseColumns {
        // Entry content URI = base content URI + path
        public static final Uri CONTENT_URI_WAYPOINTS =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WAYPOINTS).build();
        // Waypoints table
        public static final String TABLE_NAME_WAYPOINTS = "waypoints";
        public static final String COLUMN_WAYPOINTS_ID = "id";
        public static final String COLUMN_WAYPOINTS_TRACK_ID = "trackId";
        public static final String COLUMN_WAYPOINTS_LATITUDE = "latitude";
        public static final String COLUMN_WAYPOINTS_LONGITUDE = "longitude";
        public static final String COLUMN_WAYPOINTS_ALTITUDE = "altitude";
        public static final String COLUMN_WAYPOINTS_TIMESTAMP = "timestamp";
    }
}