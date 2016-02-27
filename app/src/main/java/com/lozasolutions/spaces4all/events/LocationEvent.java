package com.lozasolutions.spaces4all.events;

import android.location.Location;

/**
 * Created by Loza on 26/02/2016.
 */
public class LocationEvent {

    public final Location location;

    public LocationEvent(Location location) {
        this.location = location;
    }

}
