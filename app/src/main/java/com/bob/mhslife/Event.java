package com.bob.mhslife;

/**
 * Created by Bob on 8/12/2016.
 */
public class Event {
    public boolean exclusive;
    public String group;
    public String name;
    public String date;
    public String location;
    public String description;

    public Event(){}

    public Event(boolean exclusive, String group, String name, String date, String location, String description){
        this.exclusive = exclusive;
        this.group = group;
        this.name = name;
        this.date = date;
        this.location = location;
        this.description = description;
    }
}
