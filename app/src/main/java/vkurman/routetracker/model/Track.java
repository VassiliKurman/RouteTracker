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
package vkurman.routetracker.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <code>Track</code> is a track, made of at least one segment containing
 * <code>Waypoint</code>'s - an ordered list of points describing the path.
 * <p>
 * Tracks are a record of where a person has been and routes are suggestions
 * about where they might go in the future.
 *
 * Created by Vassili Kurman on 01/08/2018.
 * Version 1.0
 */
public class Track implements Parcelable {

    private long id;
    private String name;
    private String owner;
    private String image;
    private Waypoint[] waypoints;

    /**
     * Classes implementing the <code>Parcelable</code> interface must
     * also have a non-null static field called <code>CREATOR</code> of
     * a type that implements the <code>Parcelable.Creator</code> interface.
     */
    public static final Parcelable.Creator<Track> CREATOR
            = new Parcelable.Creator<Track>() {
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    private Track(Parcel in) {
        id = in.readLong();
        name = in.readString();
        owner = in.readString();
        image = in.readString();
        waypoints = in.createTypedArray(Waypoint.CREATOR);
    }

    /**
     * Public constructor to create object that has been retrieved from persistant
     * location, like database.
     */
    public Track(long id, String name, String owner, String image, Waypoint[] waypoints) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.image = image;
        this.waypoints = waypoints;
    }

    /**
     * Public constructor to create brand new object to start new track.
     */
    public Track(long id, String name, String owner, String image) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.image = image;
        this.waypoints = null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flattens this object into the <code>Parcel</code>.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(owner);
        dest.writeString(image);
        dest.writeTypedArray(waypoints, 0);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Waypoint[] getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(Waypoint[] waypoints) {
        this.waypoints = waypoints;
    }
}