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
 * User
 * Created by Vassili Kurman on 07/08/2018.
 * Version 1.0
 */
public class User implements Parcelable {

    private String id;
    private String name;

    /**
     * Classes implementing the <code>Parcelable</code> interface must
     * also have a non-null static field called <code>CREATOR</code> of
     * a type that implements the <code>Parcelable.Creator</code> interface.
     */
    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    private User(Parcel in) {
        id = in.readString();
        name = in.readString();
    }

    /**
     * Public constructor to create object that has been retrieved from persistent
     * location, like database.
     */
    public User(String id, String name) {
        this.id = id;
        this.name = name;
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
        dest.writeString(id);
        dest.writeString(name);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}