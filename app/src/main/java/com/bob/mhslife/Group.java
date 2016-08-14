package com.bob.mhslife;

/**
 * Created by Bob on 8/12/2016.
 */
public class Group {
    public String name;
    public String description;
    public int image;

    public Group(){}

    public Group(String name, String description, int image){
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public void setImage(int image){
        this.image = image;
    }
}
