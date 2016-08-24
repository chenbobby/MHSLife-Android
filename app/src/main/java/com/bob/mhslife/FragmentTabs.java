package com.bob.mhslife;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Bob on 8/12/2016.
 */
public class FragmentTabs extends FragmentActivity {
    private FragmentTabHost mTabHost;
    private static final String TAG = "FragmentTabs";

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tabs);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    User.UID = user.getUid();
//                    Log.d(TAG, "User Signed in: " + User.UID);
                }else{
//                    Log.d(TAG, "User not signed in...");
                }
            }
        };

        if(firebaseAuth.getCurrentUser() == null){
            finish();
        }

        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("home").setIndicator("", getResources().getDrawable(R.drawable.tab_home)), HomeFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("calendar").setIndicator("", getResources().getDrawable(R.drawable.tab_calendar)), CalendarFragment.class, null);
    }

    public void goToGroupSearch(View view){
//        Log.d(TAG, "GO TO GROUP SEARCH");
        Intent goToGroupSearch = new Intent(this, GroupSearchActivity.class);
        startActivity(goToGroupSearch);
    }

    //Init/Deinit AuthListeners
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
