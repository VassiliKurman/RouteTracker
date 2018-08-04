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
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vkurman.routetracker.R;
import vkurman.routetracker.adapter.TracksAdapter;
import vkurman.routetracker.model.Track;

/**
 * RoutesFragment is a simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RoutesFragment.OnItemSelectedListener} interface
 * to handle interaction events.
 *
  * Created by Vassili Kurman on 04/08/2018.
 * Version 1.0
  */
public class RoutesFragment extends Fragment implements TracksAdapter.TrackClickListener {
    /**
     * Item click listener
     */
    private OnItemSelectedListener mListener;
    /**
     * Adapter for RecycleView
     */
    private TracksAdapter mAdapter;
    /**
     * RecycleView containing list of <code>Track</code>'s.
     */
    private RecyclerView mRecycleView;

    private FloatingActionButton mFloatingActionButton;

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
        // Get a reference to the ListView in the master_list_view xml layout file
        mRecycleView = rootView.findViewById(R.id.recycler_view);

        // This fragment is a static fragment and it is created before parent activity,
        // therefore recipes not set
        mAdapter = new TracksAdapter(getContext(), null, this);
        // Set the adapter on the ListView
        mRecycleView.setAdapter(mAdapter);

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
     * @param track
     */
    @Override
    public void onTrackClicked(Track track) {
        if (mListener != null) {
            mListener.onItemSelected(track);
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
        void onItemSelected(Track track);
    }
}
