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

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.text.SimpleDateFormat;

/**
 * RouteTrackerUtils is a small utility class to perform general text and number
 * formatting and conversion.
 *
 * Created by Vassili Kurman on 06/08/2018.
 * Version 1.0
 */
public class RouteTrackerUtils {
    /**
     * Converts Milliseconds to seconds.
     *
     * @param milliseconds
     * @return long - seconds
     */
    public static long convertMillisecondsToSeconds(long milliseconds) {
        return milliseconds / 1000;
    }

    /**
     * Converts milliseconds and returns String in format "hh:mm:ss".
     *
     * @param milliseconds
     * @return String - formatted time
     */
    public static String convertMillisecondsToFormattedString(long milliseconds) {
        int seconds = (int) convertMillisecondsToSeconds(milliseconds);
        int sec = seconds % 60;
        int min = ((seconds - sec) / 60) % 60;
        int h = (seconds - (min * 60) - seconds) / 3600;
        return String.format("%02d:%02d:%02d", h, min, sec);
    }

    /**
     * Converts milliseconds and returns in local Date and Time format.
     *
     * @param milliseconds
     * @return String - formatted local Date and Time
     */
    public static String convertMillisecondsToDateTimeFormat(long milliseconds) {
        return SimpleDateFormat.getDateTimeInstance().format(milliseconds);
    }

    /**
     * Checking is all necessary permissions are granted
     *
     * @param context - Context
     * @return boolean
     */
    public static boolean permissionsGranted(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}