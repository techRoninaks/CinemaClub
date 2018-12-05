package com.create.sidhu.movbox.models;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    private String CensorRating;
    private String Story;
    private Date Release;
    private int TotalReviews;
    private int TotalRatings;
    private int Duration;
    private int TotalWatched;
    private String Cast;
    private boolean IsWatched;
    private boolean IsAddedToWatchlist;
    private boolean IsRated;
    private boolean IsReviewed;
    private String Language;

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

    public int getTotalWatched() {
        return TotalWatched;
    }

    public String getCensorRating() {
        return CensorRating;
    }

    public Date getRelease() {
        return Release;
    }

    public String getStory() {
        return Story;
    }

    public String getCast() {
        return Cast;
    }

    public boolean getIsWatched(){
        return IsWatched;
    }

    public boolean getIsAddedToWatchlist(){
        return  IsAddedToWatchlist;
    }

    public boolean getIsRated() {
        return IsRated;
    }

    public String getLanguage() {
        return Language;
    }

    public boolean getIsReviewed(){
        return IsReviewed;
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

    public void setTotalWatched(int totalWatched) {
        TotalWatched = totalWatched;
    }

    public void setCensorRating(String censorRating) {
        CensorRating = censorRating;
    }

    public void setRelease(String release) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Release = simpleDateFormat.parse(release);
        } catch (ParseException e) {
            Log.e("MovieModel", e.getMessage());
        }
    }

    public void setStory(String story) {
        Story = story;
    }

    public void setCast(String cast) {
        this.Cast = cast;
    }

    public void setWatched(boolean watched) {
        IsWatched = watched;
    }

    public void setAddedToWatchlist(boolean addedToWatchlist) {
        IsAddedToWatchlist = addedToWatchlist;
    }

    public void setRated(boolean rated) {
        IsRated = rated;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    public void setReviewed(boolean reviewed) {
        IsReviewed = reviewed;
    }
}
