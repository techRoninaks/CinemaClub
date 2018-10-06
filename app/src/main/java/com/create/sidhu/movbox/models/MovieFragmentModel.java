package com.create.sidhu.movbox.models;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

/**
 * Model to hold the MovieFragment data
 */

public class MovieFragmentModel {
    private TextView Title;
    private RecyclerView Movie;

    //Getters
    public RecyclerView getMovie() {
        return Movie;
    }

    public TextView getTitle() {
        return Title;
    }

    //Setters
    public void setTitle(TextView title) {
        Title = title;
    }

    public void setMovie(RecyclerView movie) {
        Movie = movie;
    }
}
