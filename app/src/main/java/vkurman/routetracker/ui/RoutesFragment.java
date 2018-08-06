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
import vkurman.routetracker.adapter.TracksAdapter;

/**
 * RoutesFragment is a simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RoutesFragment.OnItemSelectedListener} interface
 * to handle interaction events.
 *
 * Created by Vassili Kurman on 04/08/2018.
 * Version 1.0
 */
public class RoutesFragment extends Fragment implements TracksAdapter.TrackClickListener, View.OnClickListener {
    /**
     * Item click listener
     */
    private OnItemSelectedListener mListener;
    /**
     * Adapter for RecycleView
     */
    private TracksAdapter mAdapter;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_routes, container, true);
        // Binding views
        ButterKnife.bind(this, rootView);
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false);
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
            mListener = (OnItemSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This method called when item selected from the list.
     *
     * @param trackId
     */
    @Override
    public void onTrackClicked(long trackId) {
        if (mListener != null) {
            mListener.onItemSelected(trackId);
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
    }

    /**
     * Callback for click on a button
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        if(view == mFloatingActionButton) {
            Intent intent = new Intent(getActivity(), NewRouteActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Pass new Cursor here into fragment if data has been updated.
     *
     * @param data
     */
    protected void updateData(Cursor data) {
        if(refreshLayout != null && refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }

        if(mAdapter == null) {
            mAdapter = new TracksAdapter(getActivity(), data, this);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.updateData(data);
        }
    }
}
