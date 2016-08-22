package com.bob.mhslife;

import android.util.Log;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bob on 8/12/2016.
 */
public class User {
    public static String UID;
    public static ArrayList<String> favorites;
    public static ArrayList<MHSEvent> favoriteEvents;

    public static  void toggleSubscription(final String groupName){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        if(isFavorited(groupName)){
            ref.child("users/" + User.UID + "/" + groupName).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError != null){
                        Log.e("User", "Failed to Connect to Server");
                        Log.d("User", databaseError.getMessage());
                    }else{
                        Log.d("User", groupName + " removed from favorites");
                        //TODO: unsubscribe
                    }
                }
            });
        }else{
            User.favorites.add(groupName);
            Map<String, Object> group = new HashMap<String, Object>();
            group.put(groupName, true);
            ref.child("users/" + User.UID).updateChildren(group, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError != null){
                        Log.e("User", "Failed to Connect to Server");
                        Log.d("User", databaseError.getMessage());
                    }else{
                        Log.d("User", groupName + " added to favorites");
                        //TODO: subscribe
                    }
                }
            });
        }

    }

    public static Boolean isFavorited(String groupName){
        return User.favorites.contains(groupName);
    }

    public static ArrayList<String> removeDefault(){
        ArrayList<String> modifiedFavorites = User.favorites;
        if (modifiedFavorites.indexOf("mcclintock") != -1) {
            modifiedFavorites.remove("mcclintock");
        }
        return modifiedFavorites;
    }
}
