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

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Route is a base class for recording coordinates for new route.
 * Created by Vassili Kurman on 28/07/2018.
 * Version 1.0
 */
public class Route {

    private long id;
    private String name;
    private List<Location> locations;

    /**
     * Default constructor with all values set to default.
     */
    public Route() {
        id = -1;
        name = "Undefined";
        locations = new ArrayList<>();
    }

    /**
     * Returns current <code>Route</code>'s id
     * @return long
     */
    public long getId() {
        return id;
    }

    /**
     * Setting id for <code>route</code>
     * @param id - route id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns <code>Route</code>'s custom name
     * @return String - name of the route
     */
    public String getName() {
        return name;
    }

    /**
     * Setting <code>Route</code>'s name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns first <code>Location</code>'s time.
     * @return long
     */
    public long getStartTime() {
        return (locations.isEmpty()) ? -1 : locations.get(0).getTime();
    }

    /**
     * Returns last <code>Location</code>'s time.
     * @return long
     */
    public long getEndTime() {
        return (locations.isEmpty()) ? -1 : locations.get(locations.size() - 1).getTime();
    }

    /**
     * Returns all <code>Location</code>'s associated with this <code>Route</code>
     * @return List<Location>
     */
    public List<Location> getLocations() {
        return locations;
    }

    /**
     * Setting locations for current <code>Route</code>
     * @param locations
     */
    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    /**
     * Adds <code>Location</code> to the list of locations
     * @param location
     */
    public void addLocation(Location location) {
        if(location != null) {
            locations.add(location);
        }
    }

    /**
     * Returns <code>Route</code> duration time
     * @return long - duration in milliseconds
     */
    public long getDuration() {
        long duration = 0;
        if(!locations.isEmpty()) {
            duration = locations.get(locations.size() - 1).getTime() - locations.get(0).getTime();
        }
        return duration;
    }
}