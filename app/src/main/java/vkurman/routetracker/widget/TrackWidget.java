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
package vkurman.routetracker.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Calendar;

import vkurman.routetracker.R;
import vkurman.routetracker.model.RouteManager;
import vkurman.routetracker.model.Track;
import vkurman.routetracker.model.Waypoint;
import vkurman.routetracker.provider.TrackerDbUtils;
import vkurman.routetracker.ui.RoutesActivity;
import vkurman.routetracker.utils.RouteTrackerUtils;

/**
 * TrackWidget is an implementation of App Widget functionality
 *
 * Created by Vassili Kurman on 12/08/2018.
 * Version 1.0
 */
public class TrackWidget extends AppWidgetProvider {

    public static final String TAG = TrackWidget.class.getSimpleName();

    public static final String WIDGET_ACTION_START = "vkurman.routetracker.widget.action.WIDGET_ACTION_START";
    public static final String WIDGET_ACTION_STOP = "vkurman.routetracker.widget.action.WIDGET_ACTION_STOP";
    public static final String WIDGET_ACTION_SHOW = "vkurman.routetracker.widget.action.WIDGET_ACTION_SHOW";
    public static final String ACTION_LOCATION = "vkurman.routetracker.ACTION_LOCATION";

    public static String CURRENT_WIDGET_ACTION = WIDGET_ACTION_START;

    /**
     * Current track id
     */
    private static long mCurrentTrackId;

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.track_widget);
        // Create an Intent to launch TrackDetailsActivity
//        Intent clickIntent = new Intent(context, TrackWidget.class);
//        clickIntent.setAction(WIDGET_ACTION_START);
//        PendingIntent pendingClickIntent = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        remoteViews.setOnClickPendingIntent(R.id.widget_button, pendingClickIntent);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate() called");
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.track_widget);
        // Attach onClickListener to tha button
        remoteViews.setOnClickPendingIntent(R.id.widget_button, getCustomPendingIntent(context, CURRENT_WIDGET_ACTION));
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive() called");
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.track_widget);
        if(intent.getAction().equals(WIDGET_ACTION_START)) {
            Log.d(TAG, "Action received: start");
            // Checking if permissions are granted
            if(RouteTrackerUtils.permissionsGranted(context)) {
                CURRENT_WIDGET_ACTION = WIDGET_ACTION_STOP;
                remoteViews.setTextViewText(R.id.widget_button, context.getString(R.string.widget_button_stop));
                startLocationTracking(context);
            } else {
                requestPermissions(context);
            }
        } else if(intent.getAction().equals(WIDGET_ACTION_STOP)) {
            Log.d(TAG, "Action received: stop");
            // Stopping location tracking
            stopLocationTracking(context);
            // updating widget views
            CURRENT_WIDGET_ACTION = WIDGET_ACTION_SHOW;
            remoteViews.setTextViewText(R.id.widget_button, context.getString(R.string.widget_button_show));
        } else if(intent.getAction().equals(WIDGET_ACTION_SHOW)) {
            Log.d(TAG, "Action received: show");
            CURRENT_WIDGET_ACTION = WIDGET_ACTION_START;
            mCurrentTrackId = -1;
            remoteViews.setTextViewText(R.id.widget_data_latitude, "0.0");
            remoteViews.setTextViewText(R.id.widget_data_longitude, "0.0");
            remoteViews.setTextViewText(R.id.widget_data_altitude, "0.0");
            remoteViews.setTextViewText(R.id.widget_data_timestamp, "Unspecified");
            remoteViews.setTextViewText(R.id.widget_button, context.getString(R.string.widget_button_start));
            // Start app
            context.startActivity(new Intent(context, RoutesActivity.class));
        } else if (intent.getAction().equals(ACTION_LOCATION)) {
            // Checking if widget exists before saving
            ComponentName componentName = new ComponentName(context, TrackWidget.class);
            int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(componentName);
            if (ids.length > 0) {
                Log.d(TAG, "Action received: location");
                Location location = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
                if (location != null) {
                    // Saving location to database
                    saveLocation(context, location);
                    // Updating texts on widget
                    remoteViews.setTextViewText(R.id.widget_data_latitude, Double.toString(location.getLatitude()));
                    remoteViews.setTextViewText(R.id.widget_data_longitude, Double.toString(location.getLongitude()));
                    remoteViews.setTextViewText(R.id.widget_data_altitude, Double.toString(location.getAltitude()));
                    remoteViews.setTextViewText(R.id.widget_data_timestamp, RouteTrackerUtils.convertMillisecondsToDateTimeFormat(location.getTime()));
                }
            }
        }
        // Attach onClickListener to tha button
        remoteViews.setOnClickPendingIntent(R.id.widget_button, getCustomPendingIntent(context, CURRENT_WIDGET_ACTION));
        AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, TrackWidget.class), remoteViews);
    }

    /**
     * Helper method to create PendingIntent with custom actions for button click.
     *
     * @param context - Context
     * @param action - String
     * @return PendingIntent
     */
    protected PendingIntent getCustomPendingIntent(Context context, String action) {
        // Create an Intent to launch TrackDetailsActivity
        Intent intent = new Intent(context, TrackWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.d(TAG, "onEnabled() called");
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.track_widget);
        remoteViews.setOnClickPendingIntent(R.id.widget_button, getCustomPendingIntent(context, CURRENT_WIDGET_ACTION));
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    /**
     * Requesting permissions.
     *
     * @param context - Context
     */
    protected void requestPermissions(Context context) {
        Toast.makeText(context, "Permissions not granted! Please start app and grant permissions!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Starting location tracking.
     *
     * @param context - Context
     */
    protected void startLocationTracking(Context context) {
        Log.d(TAG, "Entered startLocationTracking()...");
        if (!RouteManager.getInstance().isTracking(context)) {
            mCurrentTrackId  = Calendar.getInstance().getTimeInMillis();
            Log.d(TAG, "Starting tracking. Track id is: " + mCurrentTrackId);

            String trackName = "W" + mCurrentTrackId;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String userId = prefs.getString(context.getString(R.string.pref_key_default_user_id), context.getString(R.string.pref_value_default_user_id));
            String userName = prefs.getString(context.getString(R.string.pref_key_default_user_name), context.getString(R.string.pref_value_default_user_name));
            // creating track
            Track track = new Track(mCurrentTrackId, trackName, userId, userName, null);
            // request to start tracking
            RouteManager.getInstance().startTracking(context, track);
        }
        Log.d(TAG, "... exiting startLocationTracking()");
    }

    /**
     * Stopping location tracking.
     *
     * @param context - Context
     */
    private void stopLocationTracking(Context context) {
        if (RouteManager.getInstance().isTracking(context)) {
            RouteManager.getInstance().stopTracking(context);
        }
        mCurrentTrackId = -1;
    }

    /**
     * Saving Waypoint to database.
     *
     * @param context - Context
     * @param location - Location
     */
    private void saveLocation(Context context, Location location) {
        // Adding Track to Database
        Log.d(TAG, "saveLocation() is started...");
        Log.d(TAG, "Track id is: " + mCurrentTrackId);
        TrackerDbUtils.addWaypoint(
                context, new Waypoint(
                        location.getTime(),
                        mCurrentTrackId,
                        location.getLatitude(),
                        location.getLongitude(),
                        location.getAltitude(),
                        location.getTime()));
        Log.d(TAG, "... saveLocation() completed!");
    }
}