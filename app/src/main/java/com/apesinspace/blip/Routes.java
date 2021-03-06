package com.apesinspace.blip;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by samnwosu on 8/15/15.
 */
public class Routes {
    private String mId;
    private String mName;
    private String mAuthor;
    private int mAvgRating;
    private ArrayList<User> mUsers;
    private String mDiscription;
    private String mAuthorImage;

    public int getDistance() {
        return mDistance;
    }

    public void setDistance(int distance) {
        mDistance = distance;
    }

    private int mDistance;


    public String getDiscription() {
        return mDiscription;
    }

    public void setDiscription(String discription) {
        mDiscription = discription;
    }

    public String getAuthorImage(){
        return mAuthorImage;
    }

    public void setAuthorImage(String url){
        mAuthorImage = url;
    }

    public ArrayList<User> getUsers() {
        return mUsers;
    }

    public void setUsers(ArrayList<User> users) {
        mUsers = users;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public int getAvgRating() {
        return mAvgRating;
    }

    public void setAvgRating(int avgRating) {
        mAvgRating = avgRating;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getId() { return mId;}

    public void setId(String id) {
        mId = id;
    }

    public Routes(String name) {
        mName = name;
    }

    public Routes(){
    }

    public Routes(JSONObject object)throws JSONException{
        mName = object.getString("name");
        mId = object.getString("id");
        mAvgRating = object.getInt("star_rating");
        mDistance  = object.getInt("distance");
    }
}
