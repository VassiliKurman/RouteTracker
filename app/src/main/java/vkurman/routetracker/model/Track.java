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
    private String ownerName;
    private String image;

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
        ownerName = in.readString();
        image = in.readString();
    }

    public Track(){
        // Default constructor required for calls to DataSnapshot.getValue(Track.class)
    }

    /**
     * Public constructor to create object that has been retrieved from persistent
     * location, like database.
     */
    public Track(long id, String name, String owner, String ownerName, String image) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.ownerName = ownerName;
        this.image = image;
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
        dest.writeString(ownerName);
        dest.writeString(image);
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

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}