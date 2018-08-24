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
package vkurman.routetracker.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import vkurman.routetracker.R;
import vkurman.routetracker.adapter.SharedTracksAdapter;
import vkurman.routetracker.adapter.TracksAdapter;
import vkurman.routetracker.model.Track;
import vkurman.routetracker.utils.RouteTrackerConstants;

/**
 * RoutesFragment is a simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RoutesFragment.OnItemSelectedListener} interface
 * to handle interaction events.
 *
 * Created by Vassili Kurman on 04/08/2018.
 * Version 1.0
 */
public class RoutesFragment extends Fragment implements TracksAdapter.TrackClickListener,
        SharedTracksAdapter.SharedTracksListener, View.OnClickListener {
    /**
     * Item click listener
     */
    private OnItemSelectedListener mOnItemSelectedListener;
    /**
     * Adapter for RecycleView
     */
    private RecyclerView.Adapter mAdapter;
    /**
     * LayoutManager for RecyclerView
     */
    private RecyclerView.LayoutManager mLayoutManager;

    /**
     * Binding Views
     */
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.fab_add_new_track) FloatingActionButton mFloatingActionButton;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout refreshLayout;

    public RoutesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_routes, container, true);
        // Binding views
        ButterKnife.bind(this, rootView);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // This fragment is a static fragment and it is created before parent activity,
        // therefore recipes not set
        mAdapter = new TracksAdapter(getContext(), null, this);
        // Set the adapter on the ListView
        mRecyclerView.setAdapter(mAdapter);
        // Setting listener for FAB
        mFloatingActionButton.setOnClickListener(this);
        // Setting SwipeRefreshLayout.OnRefreshListener
        if(getActivity() instanceof SwipeRefreshLayout.OnRefreshListener) {
            refreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) getActivity());
        }

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            mOnItemSelectedListener = (OnItemSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnItemSelectedListener = null;
    }

    /**
     * This method called when item selected from the list.
     *
     * @param trackId - track id
     */
    @Override
    public void onTrackClicked(long trackId) {
        if (mOnItemSelectedListener != null) {
            mOnItemSelectedListener.onItemSelected(trackId);
        }
    }

    @Override
    public void onSharedTrackClicked(long trackId) {
        if (mOnItemSelectedListener != null) {
            mOnItemSelectedListener.onSharedItemSelected(trackId);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnItemSelectedListener {
        void onItemSelected(long trackId);
        void onSharedItemSelected(long trackId);
    }

    /**
     * Callback for click on a button
     *
     * @param view - View
     */
    @Override
    public void onClick(View view) {
        if(view == mFloatingActionButton) {
            Intent intent = new Intent(getActivity(), NewRouteActivity.class);
            getActivity().startActivityForResult(intent, RouteTrackerConstants.ROUTES_ACTIVITY_REQUEST_CODE_FOR_RESULT);
        }
    }

    /**
     * Pass new Cursor here into fragment if data has been updated.
     *
     * @param data - Cursor
     */
    protected void updateData(Cursor data) {
        if(refreshLayout != null && refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }

        if(mAdapter == null || mAdapter instanceof SharedTracksAdapter) {
            mAdapter = new TracksAdapter(getActivity(), data, this);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            if(mAdapter instanceof TracksAdapter) {
                ((TracksAdapter) mAdapter).updateData(data);
            }
        }
    }

    /**
     * Pass new Track here into fragment if data has been updated.
     *
     * @param track - Track
     */
    protected void addTrack(Track track) {
        if(refreshLayout != null && refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }

        if(mAdapter == null || mAdapter instanceof TracksAdapter) {
            mAdapter = new SharedTracksAdapter(getActivity(), null, this);
            mRecyclerView.setAdapter(mAdapter);
            ((SharedTracksAdapter) mAdapter).addData(track);
        } else {
            if(mAdapter instanceof SharedTracksAdapter) {
                ((SharedTracksAdapter) mAdapter).addData(track);
            }
        }
    }
}
