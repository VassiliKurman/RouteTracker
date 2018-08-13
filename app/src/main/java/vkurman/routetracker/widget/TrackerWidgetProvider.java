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
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import vkurman.routetracker.R;
import vkurman.routetracker.ui.TrackDetailsActivity;

/**
 * TrackerWidgetProvider is an implementation of App Widget functionality
 *
 * Created by Vassili Kurman on 12/08/2018.
 * Version 1.0
 */
public class TrackerWidgetProvider extends AppWidgetProvider {

    private int mTrackId;
    private String mTrackName;

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId, long trackId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.track_widget);
        // Create an Intent to launch TrackDetailsActivity
        Intent clickIntent = new Intent(context, TrackDetailsActivity.class);
        PendingIntent pendingClickIntent = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_button, pendingClickIntent);
        views.setTextViewText(R.id.widget_title, Long.toString(trackId));

        // Set the list of ingredients for the specified recipe id using RemoteViewsService
//        Intent remoteAdapterIntent = new Intent(context, TrackerWidgetService.class);
//        remoteAdapterIntent.setData(Uri.fromParts("content", String.valueOf(trackId), null));
//        views.setRemoteAdapter(R.id.lv_widget_ingredients, remoteAdapterIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if(context != null) {
            // Getting up to date data from shared preferences
//            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
//            mRecipeId = sharedPreferences.getInt(context.getString(R.string.saved_recipe_id_key), mRecipeId);
//            mRecipeName = sharedPreferences.getString(context.getString(R.string.saved_recipe_name_key), mRecipeName);
//            // Notifying widget about update
//            for (int appWidgetId: appWidgetIds) {
//                updateAppWidget(
//                        context,
//                        appWidgetManager,
//                        appWidgetId,
//                        mRecipeId,
//                        mRecipeName);
//            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}