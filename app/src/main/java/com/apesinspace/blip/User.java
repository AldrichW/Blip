package com.apesinspace.blip;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by samnwosu on 8/14/15.
 */
public class User {
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getBikeModel() {
        return mBikeModel;
    }

    public void setBikeModel(String bikeModel) {
        mBikeModel = bikeModel;
    }

    public String getReview() {
        return mReview;
    }

    public void setReview(String review) {
        mReview = review;
    }

    public int getRating() {
        return mRating;
    }

    public void setRating(int rating) {
        mRating = rating;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    private String mName;
    private String mBikeModel;
    private String mReview;
    private int mRating;
    private String mImageUrl;

    public User(String name, String review) {
        mName = name;
        mReview = review;
    }

    public User(JSONObject object)throws JSONException{
        mName = object.getString("Name");
        mBikeModel = object.getString("mBikeModel");
        mReview = object.getString("Review");
        mRating = object.getInt("Rating");
    }



}
