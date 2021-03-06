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
package vkurman.routetracker.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.BindView;
import vkurman.routetracker.R;
import vkurman.routetracker.provider.TrackerContract;
import vkurman.routetracker.utils.RouteTrackerUtils;

/**
 * TracksAdapter is an Adapter for RecycleView to display tracks.
 *
 * Created by Vassili Kurman on 02/08/2018.
 * Version 1.0
 */
public class TracksAdapter extends RecyclerView.Adapter<TracksAdapter.TracksViewHolder> {

    private String mUserId;
    /**
     * Application context
     */
    final private Context mContext;
    /**
     * An on-click handler that allows for an Activity to interface with RecyclerView
     */
    final private TrackClickListener mTrackClickListener;
    /**
     * Cursor of tracks
     */
    private Cursor mTracks;

    /**
     * An on-click handler that allows for an Activity to interface with RecyclerView
     */
    public interface TrackClickListener {
        void onTrackClicked(long id);
    }

    /**
     * Constructor for TracksAdapter
     *
     * @param context - context
     * @param tracks - list of tracks
     * @param trackClickListener - item click listener
     */
    public TracksAdapter(Context context, Cursor tracks, TrackClickListener trackClickListener) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultUserId = context.getString(R.string.pref_value_default_user_id);
        mUserId = sharedPreferences.getString(
                context.getString(R.string.pref_key_default_user_id),
                defaultUserId);

        mContext = context;
        mTracks = tracks;
        mTrackClickListener = trackClickListener;
    }

    /**
     * Provides a reference to the views for each data item.
     */
    class TracksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.container_card_view) CardView container;
        @BindView(R.id.iv_list_track_image) ImageView mTrackImage;
        @BindView(R.id.tv_list_track_name) TextView mTrackName;
        @BindView(R.id.tv_list_track_owner) TextView mTrackOwner;
        @BindView(R.id.tv_list_track_timestamp) TextView mTrackTimestamp;

        TracksViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            view.setOnClickListener(this);
        }

        /**
         * Called whenever a user clicks on an item in the list.
         *
         * @param view - The View that was clicked
         */
        @Override
        public void onClick(View view) {
            if(mTracks == null) {
                return;
            }
            int position = getAdapterPosition();
            if(position >= 0 && position < mTracks.getCount()) {
                if(mTracks.moveToPosition(position)) {
                    long id = mTracks.getLong(mTracks.getColumnIndex(TrackerContract.TracksEntry.COLUMN_TRACKS_ID));
                    mTrackClickListener.onTrackClicked(id);
                }
            }
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public TracksAdapter.TracksViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                             int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_track_layout;

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new TracksAdapter.TracksViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull TracksViewHolder holder, int position) {
        if(position >= 0 && position < mTracks.getCount()) {
            if(mTracks.moveToPosition(position)) {
                String name = mTracks.getString(mTracks.getColumnIndex(TrackerContract.TracksEntry.COLUMN_TRACKS_NAME));
                String ownerId = mTracks.getString(mTracks.getColumnIndex(TrackerContract.TracksEntry.COLUMN_TRACKS_OWNER));
                String ownerName = mTracks.getString(mTracks.getColumnIndex(TrackerContract.TracksEntry.COLUMN_TRACKS_OWNER_NAME));
                String image = mTracks.getString(mTracks.getColumnIndex(TrackerContract.TracksEntry.COLUMN_TRACKS_IMAGE));
                long timestamp = mTracks.getLong(mTracks.getColumnIndex(TrackerContract.TracksEntry.COLUMN_TRACKS_ID));

                if(!ownerId.equals(mUserId)) {
                    holder.container.setBackgroundColor(mContext.getResources().getColor(R.color.colorCardViewBackgroundShared, null));
                }

                holder.mTrackName.setText(name);
                holder.mTrackOwner.setText((TextUtils.isEmpty(ownerName)) ? ownerId : ownerName);
                holder.mTrackTimestamp.setText(RouteTrackerUtils.convertMillisecondsToDateTimeFormat(timestamp));
                Picasso.get()
                        .load(image == null ? null : image.isEmpty() ? null : image)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .into(holder.mTrackImage);
            }
        }
    }

    // Return the size of list (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mTracks == null ? 0 : mTracks.getCount();
    }

    /**
     * Update data in Adapter.
     *
     * @param tracks - provided new list of tracks
     */
    public void updateData(Cursor tracks) {
        mTracks = tracks;
        notifyDataSetChanged();
    }
}