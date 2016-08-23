package com.bob.mhslife;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

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

    private TextView headline1TV;
    private TextView headline2TV;
    private TextView headline3TV;
    private TextView emptyFavoritesTV;
    private ListView favoritesLV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View V = inflater.inflate(R.layout.fragment_home, container, false);
        headline1TV = (TextView)V.findViewById(R.id.headline1);
        headline2TV = (TextView)V.findViewById(R.id.headline2);
        headline3TV = (TextView)V.findViewById(R.id.headline3);
        emptyFavoritesTV = (TextView)V.findViewById(R.id.emptyFavoritesTV);
        favoritesLV = (ListView)V.findViewById(R.id.favoritesLV);
        favoritesLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View view, int i, long l) {
                final String groupName = (String) adapter.getItemAtPosition(i);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle("Woah there...");
                alertDialogBuilder
                        .setMessage("Are you sure you want to unfavorite " + groupName + "?")
                        .setCancelable(false)
                        .setPositiveButton("Yeah, Pretty Sure",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                User.toggleSubscription(groupName);
                            }
                        })
                        .setNegativeButton("Not that sure...",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                return false;
            }
        });

        ref = FirebaseDatabase.getInstance().getReference();

        loadAccount();
        loadNews();

        return V;
    }

    // Load Data
    private void loadAccount(){
        accountRef = ref.child("users/" + User.UID);
        accountListener = accountRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList<String> favoritesArr = new ArrayList<String>();
                    for(DataSnapshot group : dataSnapshot.getChildren()){
                        favoritesArr.add(group.getKey());
                    }
                    User.favorites = favoritesArr;
                    for(String favorite : User.favorites){
                        Log.d(TAG, favorite);
                    }
                    favorites = User.removeDefault();

                    if(favorites.size() == 0){
                        emptyFavoritesTV.setVisibility(View.VISIBLE);
                    }else{
                        emptyFavoritesTV.setVisibility(View.GONE);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, favorites);
                    favoritesLV.setAdapter(adapter);
                }else{
                    Map<String, Object> newUser = new HashMap<String, Object>();
                    newUser.put("mcclintock", true);
                    ref.child("users").child(User.UID).setValue(newUser);
                    //TODO: Subscribe to mcclintock
                    loadAccount();
                }
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

                headline1TV.setText((String) news.get("headline1"));
                headline2TV.setText((String) news.get("headline2"));
                headline3TV.setText((String) news.get("headline3"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to connect");
                Log.e(TAG, databaseError.getMessage());

                headline1TV.setText("Failed to Connect");
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
