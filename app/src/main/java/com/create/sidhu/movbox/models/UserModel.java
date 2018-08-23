package com.create.sidhu.movbox.models;

/**
 * Holds User information
 */

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
    private int TotalReviews;
    private int TotalWatched;
    private int Followers;
    private int Following;

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
}
