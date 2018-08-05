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
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import vkurman.routetracker.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewRouteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 *
 * Created by Vassili Kurman on 05/08/2018.
 * Version 1.0
 */
public class NewRouteFragment extends Fragment implements View.OnClickListener {

    // Parent activity listens for this fragments interactions
    private OnFragmentInteractionListener mListener;
    // Toast
    private Toast mToast;

    @BindView(R.id.btn_start) Button mStartButton;
    @BindView(R.id.btn_stop) Button mStopButton;

    public NewRouteFragment() {
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
        View view = inflater.inflate(R.layout.fragment_new_route, container, false);
        // Binding views
        ButterKnife.bind(this, view);

        mStartButton.setEnabled(true);
        mStartButton.setOnClickListener(this);
        mStopButton.setEnabled(false);
        mStopButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View view) {
        if(view == mStartButton) {
            mStartButton.setEnabled(false);
            // Displaying Toast
            if(mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(getContext(), "Started tracking!", Toast.LENGTH_SHORT);
            mToast.show();
            // TODO start tracking

            mStopButton.setEnabled(true);
        } else if (view == mStopButton) {
            mStopButton.setEnabled(false);
            // Displaying Toast
            if(mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(getContext(), "Stopped tracking!", Toast.LENGTH_SHORT);
            mToast.show();
            // TODO stop tracking

            mStartButton.setEnabled(true);
        }
    }
}
