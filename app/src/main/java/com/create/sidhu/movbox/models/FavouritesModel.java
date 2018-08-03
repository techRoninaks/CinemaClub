package com.create.sidhu.movbox.models;

/**
 * Holds the data for Favourites List.
 * Created by nihalpradeep on 22/07/18.
 */

public class FavouritesModel {
    private String Title;
    private String Type;
    private String Subtitle;
    private String Date;
    private String Time;
    private String ImageLocation;

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
}
