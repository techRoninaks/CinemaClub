package com.create.sidhu.movbox.models;

public class RatingsModel {
    private String UserName;
    private String UserId;
    private float UserRating;
    private boolean IsFollowing;
    private String RatingId;

    //Getters
    public String getUserName() {
        return UserName;
    }

    public String getUserId() {
        return UserId;
    }

    public String getRatingId() {
        return RatingId;
    }

    public float getUserRating() {
        return UserRating;
    }

    public boolean getFollowing() {
        return IsFollowing;
    }

    //Setters
    public void setFollowing(boolean following) {
        IsFollowing = following;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public void setRatingId(String ratingId) {
        RatingId = ratingId;
    }

    public void setUserRating(float userRating) {
        UserRating = userRating;
    }
}
