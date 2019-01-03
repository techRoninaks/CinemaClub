package com.create.sidhu.movbox.models;

import java.util.ArrayList;

public class FilterModel {
    private ArrayList<PreferenceModel> LanguageList;
    private ArrayList<PreferenceModel> GenreList;
    private ArrayList<Boolean> Watched;
    private ArrayList<Integer> Rating;

    //Getters
    public ArrayList<Boolean> getWatched() {
        return Watched;
    }

    public ArrayList<Integer> getRating() {
        return Rating;
    }

    public ArrayList<PreferenceModel> getGenreList() {
        return GenreList;
    }

    public ArrayList<PreferenceModel> getLanguageList() {
        return LanguageList;
    }

    //Setters
    public void setGenreList(ArrayList<PreferenceModel> genreList) {
        GenreList = genreList;
    }

    public void setLanguageList(ArrayList<PreferenceModel> languageList) {
        LanguageList = languageList;
    }

    public void setRating(ArrayList<Integer> rating) {
        Rating = rating;
    }

    public void setWatched(ArrayList<Boolean> watched) {
        Watched = watched;
    }
}
