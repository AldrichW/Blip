package com.apesinspace.blip;

import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by aldrichW on 15-08-16.
 */
public class MarkerPoint {

    private LatLng markerPoint;
    private RouteFragmentActivity.MarkerType markerType;
    private String imagePath;
    private String markerUserDescription;

    public LatLng getMarkerPoint() {
        return markerPoint;
    }

    public RouteFragmentActivity.MarkerType getMarkerType() {
        return markerType;
    }

    public String getMarkerUserDescription() {
        return markerUserDescription;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setMarkerPoint(LatLng _markerPoint){
        markerPoint = _markerPoint;
    }

    public void setMarkerUserDescription(String _userDescription) {
        markerUserDescription = _userDescription;
    }

    public void setImagePath(String _imagePath){
        imagePath = _imagePath;
    }

    public void setMarkerType(RouteFragmentActivity.MarkerType _markerType) {
        markerType = _markerType;
    }

}
