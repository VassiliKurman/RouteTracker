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
 * Waypoint is an individual waypoint among the collection of points with no
 * sequential relationship. It consists of WGS 84 (GPS) coordinates of a point
 * and other descriptive information.
 *
 * Created by Vassili Kurman on 01/08/2018.
 * Version 1.0
 */
public class Waypoint implements Parcelable {

    private long id;
    private long trackId;
    private double latitude;
    private double longitude;
    private double altitude;
    private long timeStamp;

    /**
     * Classes implementing the <code>Parcelable</code> interface must
     * also have a non-null static field called <code>CREATOR</code> of
     * a type that implements the <code>Parcelable.Creator</code> interface.
     */
    public static final Parcelable.Creator<Waypoint> CREATOR
            = new Parcelable.Creator<Waypoint>() {
        public Waypoint createFromParcel(Parcel in) {
            return new Waypoint(in);
        }

        public Waypoint[] newArray(int size) {
            return new Waypoint[size];
        }
    };

    private Waypoint(Parcel in) {
        id = in.readLong();
        trackId = in.readLong();
        latitude = in.readDouble();
        longitude = in.readDouble();
        altitude = in.readDouble();
        timeStamp = in.readLong();
    }

    /**
     * Public constructor to create brand new <code>Waypoint</code> object.
     */
    public Waypoint(long id, long trackId, double latitude, double longitude, double altitude, long timeStamp) {
        this.id = id;
        this.trackId = trackId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timeStamp = timeStamp;
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
        dest.writeLong(trackId);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(altitude);
        dest.writeLong(timeStamp);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTrackId() {
        return trackId;
    }

    public void setTrackId(long trackId) {
        this.trackId = trackId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}