package com.create.sidhu.movbox.models;

public class UpdatesModel {
    private String UserId;
    private String MovieId;
    private String ReviewId;
    private String Type;
    private String Id;
    private boolean IsUpdated;


    //Setters
    public void setType(String type) {
        Type = type;
    }

    public void setMovieId(String movieId) {
        MovieId = movieId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public void setReviewId(String reviewId) {
        ReviewId = reviewId;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setUpdated(boolean updated) {
        IsUpdated = updated;
    }

    //Getters
    public String getType() {
        return Type;
    }

    public String getUserId() {
        return UserId;
    }

    public String getReviewId() {
        return ReviewId;
    }

    public String getMovieId() {
        return MovieId;
    }

    public String getId() {
        return Id;
    }

    public boolean isUpdated() {
        return IsUpdated;
    }
}
