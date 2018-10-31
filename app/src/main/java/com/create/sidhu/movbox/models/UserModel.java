package com.create.sidhu.movbox.models;

import com.create.sidhu.movbox.activities.MainActivity;

/**
 * Holds User information
 */

//TODO Set preference
public class UserModel {
    private String UserId;
    private String Name;
    private String Email;
    private String Phone;
    private String Country;
    private String City;
    private String Gender;
    private String Dob;
    private String Image;
    private String Privacy;
    private String LanguagePreference;
    private String GenrePreference;
    private int TotalReviews;
    private int TotalWatched;
    private int Followers;
    private int Following;
    private boolean IsFollowing;

    //Getters

    public String getUserId() {
        return UserId;
    }

    public String getName() {
        return Name;
    }

    public String getEmail() {
        return Email;
    }

    public String getPhone() {
        return Phone;
    }

    public String getCountry() {
        return Country;
    }

    public String getGender() {
        return Gender;
    }

    public String getDob() {
        return Dob;
    }

    public String getImage() {
        return Image;
    }

    public int getTotalReviews() {
        return TotalReviews;
    }

    public int getTotalWatched() {
        return TotalWatched;
    }

    public int getFollowers() {
        return Followers;
    }

    public int getFollowing() {
        return Following;
    }

    public String getCity() {
        return City;
    }

    public String getPrivacy() {
        return Privacy;
    }

    public boolean getIsFollowing() {
        return IsFollowing;
    }

    public String getLanguagePreference() {
        return LanguagePreference;
    }

    public String getGenrePreference() {
        return GenrePreference;
    }
    //Setters

    public void setGender(String gender) {
        Gender = gender;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setTotalWatched(int totalWatched) {
        TotalWatched = totalWatched;
    }

    public void setTotalReviews(int totalReviews) {
        TotalReviews = totalReviews;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public void setDob(String dob) {
        Dob = dob;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setFollowers(int followers) {
        Followers = followers;
    }

    public void setFollowing(int following) {
        Following = following;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public void setCity(String city) {
        City = city;
    }

    public void setPrivacy(int privacy) {
        Privacy = Integer.toBinaryString(0x4 | privacy).substring(1);
    }

    public void setIsFollowing(boolean following){
        IsFollowing = following;
    }

    public void setPreferences(String preferences){
        if(preferences.isEmpty() || preferences == null || preferences == "null"){
            LanguagePreference = "NULL";
            GenrePreference = "NULL";
        }else {
            String language = preferences.split("!~")[0];
            String genre = preferences.split("!~")[1];
            int languageLength = Integer.parseInt(language.split("!@")[0]);
            language = language.split("!@")[1];
            int genreLength = Integer.parseInt(genre.split("!@")[0]);
            genre = genre.split("!@")[1];
            LanguagePreference = Integer.toBinaryString((int) Math.pow(2, languageLength) | Integer.parseInt(language)).substring(1);
            GenrePreference = Integer.toBinaryString((int) Math.pow(2, genreLength) | Integer.parseInt(genre)).substring(1);
        }
    }

    public void setGenrePreference(String genrePreference) {
        GenrePreference = genrePreference;
    }

    public void setLanguagePreference(String languagePreference) {
        LanguagePreference = languagePreference;
    }
}
