package com.bob.mhslife;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends Activity {

    private static final String TAG = "HomeActivity";
    private DatabaseReference ref;

    private ArrayList<String> favorites;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ref = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Account...");
        progressDialog.show();

        loadAccount();
        loadNews();

        progressDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    // Load Data
    private void loadAccount(){
        DatabaseReference accountRef = ref.child("users/" + User.UID);
        accountRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Log.d(TAG, "Account Exists");
                    ArrayList<String> favoritesArr = new ArrayList<String>();
                    for(DataSnapshot group : dataSnapshot.getChildren()){
                        favoritesArr.add(group.getKey());
                    }
                    User.favorites = favoritesArr;
                    for(String favorite : User.favorites){
                        Log.d("TAG", favorite);
                    }
                    favorites = User.removeDefault();
                }else{
                    Map<String, Object> newUser = new HashMap<String, Object>();
                    newUser.put("mcclintock", true);
                    ref.child("users").child(User.UID).setValue(newUser);
                    //TODO: Subscribe to mcclintock
                    Log.d(TAG, "Rerunning loadAccount");
                    loadAccount();
                }
                //TODO: reload tableview data
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to connect");
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    private void loadNews(){
        DatabaseReference newsRef = ref.child("news/");
        newsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> news = new HashMap<String, Object>();
                for(DataSnapshot newsSnapshot : dataSnapshot.getChildren()){
                    news.put(newsSnapshot.getKey(), newsSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to connect");
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }
}
