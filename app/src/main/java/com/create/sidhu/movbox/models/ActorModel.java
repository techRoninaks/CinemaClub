package com.create.sidhu.movbox.models;

import android.content.Context;

import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.helpers.SqlHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Holds data for actors
 */

public class ActorModel{
    private Context Context;
    private String Id;
    private String Name;
    private String Gender;
    private String Image;
    private float Rating;
    private float AverageRating;
    private String Type;
    private int TotalMovies;

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

    public void setRating(float rating) {
        Rating = rating;
    }

    public void setType(String type) {
        Type = type;
    }

    public void setAverageRating(float averageRating) {
        AverageRating = averageRating;
    }

    public void setTotalMovies(int totalMovies) {
        TotalMovies = totalMovies;
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

    public float getRating() {
        return Rating;
    }

    public String getType() {
        return Type;
    }

    public float getAverageRating() {
        return AverageRating;
    }

    public int getTotalMovies() {
        return TotalMovies;
    }
}
