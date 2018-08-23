package com.create.sidhu.movbox.models;

/**
 * Holds the data for Favourites List.
 *
 */

public class FavouritesModel {
    private String Title;
    private String Type;
    private String Subtitle;
    private String Date;
    private String Time;
    private String ImageLocation;
    private String UserId;
    private String MovieId;

    //Setters
    public void setTitle(String title) {
        Title = title;
    }

    public void setType(String type) {
        Type = type;
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

    //Getters
    public String getTitle() {
        return Title;
    }

    public String getType() {
        return Type;
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
}
