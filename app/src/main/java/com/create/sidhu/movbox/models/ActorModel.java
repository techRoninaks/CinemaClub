package com.create.sidhu.movbox.models;

/**
 * Holds data for actors
 */

public class ActorModel {
    private String Id;
    private String Name;
    private String Gender;
    private String Image;
    private int Rating;
    private String Type;

    //Setters

    public void setId(String id) {
        Id = id;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public void setRating(int rating) {
        Rating = rating;
    }

    public void setType(String type) {
        Type = type;
    }
    //Getters

    public String getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String getGender() {
        return Gender;
    }

    public String getImage() {
        return Image;
    }

    public int getRating() {
        return Rating;
    }

    public String getType() {
        return Type;
    }
}
