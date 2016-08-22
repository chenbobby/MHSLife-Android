package com.bob.mhslife;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupSearchActivity extends Activity {

    private static final String TAG = "GroupSearchActivity";
    private DatabaseReference ref;

    private ArrayList<Group> groups;
    private ArrayList<Group> filteredGroups;

    private EditText searchEditText;
    private ListView groupsLV;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_search);

        ref = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Groups");
        progressDialog.show();

        loadGroups();
        configSearchBar();

        progressDialog.dismiss();
    }

    private void loadGroups(){
        ref.child("groups/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groups = new ArrayList<Group>();
                for(DataSnapshot group : dataSnapshot.getChildren()){
                    String name = group.getKey();
                    String description = group.child("description").getValue().toString();
                    int image = (User.favorites.contains(name)) ? R.drawable.ic_star_filled : R.drawable.ic_home;
                    groups.add(new Group(name, description, image));
                }
                groupsLV = (ListView) findViewById(R.id.groupsLV);
                groupsLV.setAdapter(new GroupAdapter(getApplicationContext(), groups));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to connect");
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    private void configSearchBar(){
        searchEditText = (EditText)findViewById(R.id.searchET);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int arg1, int arg2, int arg3) {
                int textlength = charSequence.length();
                filteredGroups = new ArrayList<Group>();
                for(Group group : groups){
                    if(textlength <= group.name.length()){
                        if(group.name.toLowerCase().contains(charSequence.toString().toLowerCase())){
                            filteredGroups.add(group);
                        }
                    }
                }
                groupsLV.setAdapter(new GroupAdapter(getApplicationContext(), filteredGroups));
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }
}
