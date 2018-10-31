package com.create.sidhu.movbox.models;

public class PreferenceModel {
    private String Type;
    private String Id;
    private String Name;
    private boolean IsChecked;

    //Getters
    public String getType() {
        return Type;
    }

    public String getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public boolean getChecked() {
        return IsChecked;
    }

    //Setters
    public void setType(String type) {
        Type = type;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setChecked(boolean checked) {
        IsChecked = checked;
    }
}
