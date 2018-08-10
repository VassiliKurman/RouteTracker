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
    public static final String INTENT_NAME_FOR_TRACK_ID = "trackId";

    // Request codes
    public static final int ROUTES_ACTIVITY_REQUEST_CODE_FOR_RESULT = 801;

    // Result codes
    public static final int TRACK_DETAILS_ACTIVITY_RESULT_CODE_UNCHANGED = 901;
    public static final int TRACK_DETAILS_ACTIVITY_RESULT_CODE_UPDATED = 902;
    public static final int TRACK_DETAILS_ACTIVITY_RESULT_CODE_DELETED = 903;
    public static final int TRACK_DETAILS_ACTIVITY_RESULT_CODE_SHARED = 904;
}