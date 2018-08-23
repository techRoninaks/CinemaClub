package com.create.sidhu.movbox.models;

/**
 * Holds the movie recycler view information
 */

public class MovieModel {
    private String Name;
    private String Image;
    private String Rating;
    private String Id;
    private String Genre;
    private String DisplayDimension;
    private int TotalReviews;
    private int TotalRatings;
    private int Duration;

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

    public int getTotalReviews() {
        return TotalReviews;
    }

    public int getTotalRatings() {
        return TotalRatings;
    }

    public String getDisplayDimension() {
        return DisplayDimension;
    }

    public int getDuration() {
        return Duration;
    }

    public String getGenre() {
        return Genre;
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

    public void setTotalReviews(int totalReviews) {
        TotalReviews = totalReviews;
    }

    public void setTotalRatings(int totalRatings) {
        TotalRatings = totalRatings;
    }

    public void setDisplayDimension(String displayDimension) {
        DisplayDimension = displayDimension;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public void setGenre(String genre) {
        Genre = genre;
    }
}
