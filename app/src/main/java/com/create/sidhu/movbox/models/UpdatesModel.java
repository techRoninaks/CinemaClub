package com.create.sidhu.movbox.models;

public class UpdatesModel {
    private String UserId;
    private String MovieId;
    private String ReviewId;
    private String Type;
    private String Id;


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
}
