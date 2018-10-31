package com.create.sidhu.movbox.models;

import java.util.ArrayList;

public class ReviewModel {
    private String Type;
    private String ReviewId;
    private String UserId;
    private String UserPrivacy;
    private String MovieId;
    private String MovieName;
    private String UserName;
    private String ReviewText;
    private String Time;
    private int Likes;
    private String Replies;
    private Boolean IsFollowing;
    private Boolean IsLiked;
    private ArrayList<ReviewModel> RepliesList;

    //Setters

    public void setLikes(int likes) {
        Likes = likes;
    }

    public void setReplies(String replies) {
        Replies = replies;
        if(replies.startsWith("#")){
            RepliesList = new ArrayList<>();
        }
    }

    public void setReviewText(String reviewText) {
        ReviewText = reviewText;
    }

    public void setTime(String time) {
        Time = time;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public void setMovieId(String movieId) {
        MovieId = movieId;
    }

    public void setReviewId(String reviewId) {
        ReviewId = reviewId;
    }

    public void setMovieName(String movieName) {
        MovieName = movieName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setType(String type) {
        Type = type;
    }

    public void setFollowing(Boolean following) {
        IsFollowing = following;
    }

    public void setRepliesList(ArrayList<ReviewModel> repliesList) {
        RepliesList = repliesList;
    }

    public void setLiked(Boolean liked) {
        IsLiked = liked;
    }

    public void setUserPrivacy(int userPrivacy) {
        UserPrivacy = Integer.toBinaryString(0x4 | userPrivacy).substring(1);
    }
    //Getters

    public int getLikes() {
        return Likes;
    }

    public String getReplies() {
        return Replies;
    }

    public String getReviewText() {
        return ReviewText;
    }

    public String getTime() {
        return Time;
    }

    public String getUserId() {
        return UserId;
    }

    public String getMovieId() {
        return MovieId;
    }

    public String getReviewId() {
        return ReviewId;
    }

    public String getMovieName() {
        return MovieName;
    }

    public String getUserName() {
        return UserName;
    }

    public String getType() {
        return Type;
    }

    public Boolean getFollowing() {
        return IsFollowing;
    }

    public Boolean getLiked() {
        return IsLiked;
    }

    public ArrayList<ReviewModel> getRepliesList() {
        return RepliesList;
    }

    public String getUserPrivacy() {
        return UserPrivacy;
    }
}
