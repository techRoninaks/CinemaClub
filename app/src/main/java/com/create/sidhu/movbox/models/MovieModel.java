package com.create.sidhu.movbox.models;

/**
 * Created by nihalpradeep on 28/07/18.
 */

public class MovieModel {
    private String Name;
    private String Image;
    private String Rating;
    private String Id;

    //Getters
    public String getId() {
        return Id;
    }

    public String getRating() {
        return Rating;
    }

    public String getImage() {
        return Image;
    }

    public String getName() {
        return Name;
    }

    //Setters
    public void setName(String name) {
        this.Name = name;
    }

    public void setImage(String image) {
        this.Image = image;
    }

    public void setRating(String rating) {
        this.Rating = rating;
    }

    public void setId(String id) {
        this.Id = id;
    }
}
