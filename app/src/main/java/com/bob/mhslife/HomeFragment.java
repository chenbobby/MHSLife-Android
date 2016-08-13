package com.bob.mhslife;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private DatabaseReference ref;
    private DatabaseReference accountRef;
    private ValueEventListener accountListener;
    private DatabaseReference newsRef;
    private ValueEventListener newsListener;

    private ArrayList<String> favorites;

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View V = inflater.inflate(R.layout.fragment_home, container, false);

        return V;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ref = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(super.getContext());
        progressDialog.setMessage("Loading Account...");
        progressDialog.show();

        loadAccount();
        loadNews();

        progressDialog.dismiss();
    }

    // Load Data
    private void loadAccount(){
        accountRef = ref.child("users/" + User.UID);
        accountListener = accountRef.addValueEventListener(new ValueEventListener() {
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
        newsRef = ref.child("news/");
        newsListener = newsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> news = new HashMap<String, Object>();
                for(DataSnapshot newsSnapshot : dataSnapshot.getChildren()){
                    news.put(newsSnapshot.getKey(), newsSnapshot.getValue());
                }
                Log.d(TAG, "Headline 1: " + news.get("headline1"));
                Log.d(TAG, "Headline 2: " + news.get("headline2"));
                Log.d(TAG, "Headline 3: " + news.get("headline3"));
                //TODO: Set textviews to these headlines
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to connect");
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(accountListener != null){
            accountRef.removeEventListener(accountListener);
        }
        if(newsListener != null){
            newsRef.removeEventListener(newsListener);
        }

        FirebaseAuth.getInstance().signOut();
        User.UID = null;
        User.favorites = null;
        User.favoriteEvents = null;
    }
}
