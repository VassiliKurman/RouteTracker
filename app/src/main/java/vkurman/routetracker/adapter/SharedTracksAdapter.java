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

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import vkurman.routetracker.R;
import vkurman.routetracker.model.Track;
import vkurman.routetracker.utils.RouteTrackerUtils;

/**
 * SharedTracksAdapter is an Adapter for RecycleView to display tracks.
 *
 * Created by Vassili Kurman on 13/08/2018.
 * Version 1.0
 */
public class SharedTracksAdapter extends RecyclerView.Adapter<SharedTracksAdapter.SharedTracksViewHolder> {

    private String mUserId;
    /**
     * Application context
     */
    final private Context mContext;
    /**
     * An on-click handler that allows for an Activity to interface with RecyclerView
     */
    final private SharedTracksListener mSharedTracksListener;
    /**
     * List<Track> of tracks
     */
    private List<Track> mTracks;

    /**
     * An on-click handler that allows for an Activity to interface with RecyclerView
     */
    public interface SharedTracksListener {
        void onSharedTrackClicked(long id);
    }

    /**
     * Constructor for SharedTracksAdapter
     *
     * @param context            - context
     * @param tracks             - list of tracks
     * @param trackClickListener - item click listener
     */
    public SharedTracksAdapter(Context context, List<Track> tracks, SharedTracksListener trackClickListener) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultUserId = context.getString(R.string.pref_value_default_user_id);
        mUserId = sharedPreferences.getString(
                context.getString(R.string.pref_key_default_user_id),
                defaultUserId);

        mContext = context;
        mTracks = tracks;
        mSharedTracksListener = trackClickListener;
    }

    /**
     * Provides a reference to the views for each data item.
     */
    class SharedTracksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.container_card_view)
        CardView container;
        @BindView(R.id.iv_list_track_image)
        ImageView mTrackImage;
        @BindView(R.id.tv_list_track_name)
        TextView mTrackName;
        @BindView(R.id.tv_list_track_owner)
        TextView mTrackOwner;
        @BindView(R.id.tv_list_track_timestamp)
        TextView mTrackTimestamp;

        SharedTracksViewHolder(View view) {
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
            if (mTracks == null) {
                return;
            }
            int position = getAdapterPosition();
            if (position >= 0 && position < mTracks.size()) {
                long id = mTracks.get(position).getId();
                mSharedTracksListener.onSharedTrackClicked(id);
            }
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public SharedTracksAdapter.SharedTracksViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                             int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_track_layout;

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new SharedTracksAdapter.SharedTracksViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull SharedTracksViewHolder holder, int position) {
        if (position >= 0 && position < mTracks.size()) {
            Track track = mTracks.get(position);
                String name = track.getName();
                String owner = TextUtils.isEmpty(track.getOwnerName()) ? track.getOwner() : track.getOwnerName();
                String image = track.getImage();
                long timestamp = track.getId();

                if (!owner.equals(mUserId)) {
                    holder.container.setBackgroundColor(mContext.getResources().getColor(R.color.colorCardViewBackgroundShared, null));
                }

                holder.mTrackName.setText(name);
                holder.mTrackOwner.setText(owner);
                holder.mTrackTimestamp.setText(RouteTrackerUtils.convertMillisecondsToDateTimeFormat(timestamp));
                Picasso.get()
                        .load(image == null ? null : image.isEmpty() ? null : image)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .into(holder.mTrackImage);

        }
    }

    // Return the size of list (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mTracks == null ? 0 : mTracks.size();
    }

    /**
     * Update data in Adapter.
     *
     * @param tracks - list of tracks
     */
    public void updateData(List<Track> tracks) {
        mTracks = tracks;
        notifyDataSetChanged();
    }

    /**
     * Adds track to list
     *
     * @param track - Track
     */
    public void addData(Track track) {
        if(mTracks == null) {
            mTracks = new ArrayList<>();
        }

        if(mTracks.add(track)) {
            notifyItemInserted(mTracks.size() - 1);
        }
    }
}