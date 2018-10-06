package com.create.sidhu.movbox.models;

/**
 * Holds the data for Favourites List.
 *
 */

public class FavouritesModel {
    private String Id;
    private String Title;
    private String Type;
    private String SubType;
    private String Subtitle;
    private String Date;
    private String Time;
    private String ImageLocation;
    private String UserId;
    private String MovieId;
    private UserModel User;
    private MovieModel Movie;
    private boolean IsRead;

    //Setters
    public void setTitle(String title) {
        Title = title;
    }

    public void setType(String type) {
        Type = type;
    }

    public void setSubType(String subType) {
        SubType = subType;
    }

    public void setSubtitle(String subtitle) {
        Subtitle = subtitle;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setTime(String time) {
        Time = time;
    }

    public void setImageLocation(String imageLocation) {
        ImageLocation = imageLocation;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public void setMovieId(String movieId) {
        MovieId = movieId;
    }

    public void setMovie(MovieModel movie) {
        Movie = movie;
    }

    public void setUser(UserModel user) {
        User = user;
    }

    public void setRead(boolean read) {
        IsRead = read;
    }

    public void setId(String id) {
        Id = id;
    }

    //Getters
    public String getTitle() {
        return Title;
    }

    public String getType() {
        return Type;
    }

    public String getSubType() {
        return SubType;
    }

    public String getSubtitle() {
        return Subtitle;
    }

    public String getDate() {
        return Date;
    }

    public String getTime() {
        return Time;
    }

    public String getImageLocation() {
        return ImageLocation;
    }

    public String getUserId() {
        return UserId;
    }

    public String getMovieId() {
        return MovieId;
    }

    public MovieModel getMovie() {
        return Movie;
    }

    public UserModel getUser() {
        return User;
    }

    public boolean getRead(){
        return IsRead;
    }

    public String getId() {
        return Id;
    }
}
