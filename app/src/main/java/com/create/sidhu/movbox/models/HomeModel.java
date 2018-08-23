package com.create.sidhu.movbox.models;

import java.util.ArrayList;

/**
 * Holds the home page tile information
 */

public class HomeModel {
    private String Image;
    private String MovieName;
    private String Genre;
    private int Duration;
    private String DisplayDimension;
    private String Type;
    private int Rating;
    private ArrayList<ActorModel> ActorList;
    private String DefinitionImage;
    private String DefinitionTitle;
    private String DefinitionSubtitle;
    private int TotalReviews;
    private int TotalWatched;
    private UserModel UserInfo;
    private MovieModel MovieInfo;

    public HomeModel(){
        ActorList = new ArrayList<>();
    }

    //Setters

    public void setImage(String image) {
        Image = image;
    }

    public void setMovieName(String movieName) {
        MovieName = movieName;
    }

    public void setGenre(String genre) {
        Genre = genre;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public void setDisplayDimension(String displayDimension) {
        DisplayDimension = displayDimension;
    }

    public void setType(String type) {
        Type = type;
    }

    public void setRating(int rating) {
        Rating = rating;
    }

    public void setActorList(ArrayList<ActorModel> actorList) {
        ActorList = actorList;
    }

    public void setDefinitionImage(String definitionImage) {
        DefinitionImage = definitionImage;
    }

    public void setDefinitionTitle(String definitionTitle) {
        DefinitionTitle = definitionTitle;
    }

    public void setDefinitionSubtitle(String definitionSubtitle) {
        DefinitionSubtitle = definitionSubtitle;
    }

    public void setTotalReviews(int totalReviews) {
        TotalReviews = totalReviews;
    }

    public void setTotalWatched(int totalWatched) {
        TotalWatched = totalWatched;
    }

    public void setUserInfo(UserModel userInfo) {
        UserInfo = userInfo;
    }

    public void setMovieInfo(MovieModel movieInfo) {
        MovieInfo = movieInfo;
    }
    //Getters

    public String getImage() {
        return Image;
    }

    public String getMovieName() {
        return MovieName;
    }

    public String getGenre() {
        return Genre;
    }

    public int getDuration() {
        return Duration;
    }

    public String getDisplayDimension() {
        return DisplayDimension;
    }

    public String getType() {
        return Type;
    }

    public int getRating() {
        return Rating;
    }

    public ArrayList<ActorModel> getActorList() {
        return ActorList;
    }

    public String getDefinitionImage() {
        return DefinitionImage;
    }

    public String getDefinitionTitle() {
        return DefinitionTitle;
    }

    public String getDefinitionSubtitle() {
        return DefinitionSubtitle;
    }

    public int getTotalReviews() {
        return TotalReviews;
    }

    public int getTotalWatched() {
        return TotalWatched;
    }

    public UserModel getUserInfo() {
        return UserInfo;
    }

    public MovieModel getMovieInfo() {
        return MovieInfo;
    }
}
