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
package vkurman.routetracker.utils;

/**
 * RouteTrackerConstants
 * Created by Vassili Kurman on 09/08/2018.
 * Version 1.0
 */
public class RouteTrackerConstants {
    // Intent extras
    public static final String INTENT_EXTRA_NAME_FOR_TRACK_ID = "trackId";
    public static final String INTENT_EXTRA_IS_TRACK_SHARED = "shared";

    public static final String INTENT_EXTRA_FOR_RESULT_CODE = "track_result_code";

    // Widget pending intent actions
    public static final String WIDGET_PENDING_INTENT_ACTION_START = "start";
    public static final String WIDGET_PENDING_INTENT_ACTION_STOP = "stop";
    public static final String WIDGET_PENDING_INTENT_ACTION_SHARE = "share";

    // Request codes
    public static final int ROUTES_ACTIVITY_REQUEST_CODE_FOR_RESULT = 801;
    public static final int ROUTES_ACTIVITY_REQUEST_CODE_FOR_RESULT_SHARED_TRACK = 802;

    // Result codes
    public static final int TRACK_DETAILS_ACTIVITY_RESULT_CODE_UNCHANGED = 901;
    public static final int TRACK_DETAILS_ACTIVITY_RESULT_CODE_CREATED = 902;
    public static final int TRACK_DETAILS_ACTIVITY_RESULT_CODE_UPDATED = 903;
    public static final int TRACK_DETAILS_ACTIVITY_RESULT_CODE_DELETED = 904;
    public static final int TRACK_DETAILS_ACTIVITY_RESULT_CODE_SHARED = 905;

    // Loader codes
    public static final int LOADER_TRACKS_ID = 1001;
    public static final int LOADER_SHARED_TRACKS_ID = 1002;
    public static final int LOADER_TRACK_INDEX = 0;
    public static final int LOADER_WAYPOINTS_INDEX = 1;

    // Firebase database
    public static final String FIREBASE_DATABASE_REFERENCE_TRACKS = "tracks";
    public static final String FIREBASE_DATABASE_REFERENCE_WAYPOINTS = "waypoints";

    /**
     * MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
     * app-defined int constant. The callback method gets the
     * result of the request.
     */
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
}