package com.bob.mhslife;

import java.util.ArrayList;

/**
 * Created by Bob on 8/12/2016.
 */
public class User {
    public static String UID;
    public static ArrayList<String> favorites;
    public static ArrayList<Event> favoriteEvents;

    public static ArrayList<String> removeDefault(){
        ArrayList<String> modifiedFavorites = User.favorites;
        if (modifiedFavorites.indexOf("mcclintock") != -1) {
            modifiedFavorites.remove("mcclintock");
        }
        return modifiedFavorites;
    }
}
