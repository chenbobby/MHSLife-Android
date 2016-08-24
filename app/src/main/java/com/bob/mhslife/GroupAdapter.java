package com.bob.mhslife;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Bob on 8/13/2016.
 */
public class GroupAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Group> groups;
    private LayoutInflater inflater;

    private Typeface KGMissKindyChunky;

    private final class ViewHolder{
        TextView groupTV;
        TextView descriptionTV;
        ImageButton groupFavoriteButton;

        public void toggleFavorite(String groupName){
            User.toggleSubscription(groupName);
        }
    }

    private ViewHolder holder;

    public GroupAdapter(Context context, ArrayList<Group> groups){
        this.context = context;
        this.groups = groups;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return groups.size();
    }

    @Override
    public Object getItem(int index) {
        return index;
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int index, View view, ViewGroup parent){
        ViewHolder holder;
        if(view == null){
            view = inflater.inflate(R.layout.group_cell, parent, false);
            holder = new ViewHolder();
            holder.groupTV = (TextView)view.findViewById(R.id.groupTV);
            holder.descriptionTV = (TextView)view.findViewById(R.id.descriptionTV);
            holder.groupFavoriteButton = (ImageButton)view.findViewById(R.id.groupFavoriteButton);
            view.setTag(holder);
        }else{
            holder = (ViewHolder)view.getTag();
        }

        Group group = groups.get(index);

        if(group != null){
            KGMissKindyChunky = Typeface.createFromAsset(context.getAssets(), "kgmisskindychunky.ttf");

            holder.groupTV.setText(group.name);
            holder.descriptionTV.setText(group.description);
            holder.groupTV.setTypeface(KGMissKindyChunky);
            holder.descriptionTV.setTypeface(KGMissKindyChunky);
            holder.groupFavoriteButton.setImageResource(group.image);
            holder.groupFavoriteButton.setOnClickListener(favoriteListener);
            holder.groupFavoriteButton.setTag(index);
        }

        return view;
    }

    private View.OnClickListener favoriteListener = new View.OnClickListener(){
        public void onClick(View v){
            int pos = (Integer) v.getTag();
            Group group = groups.get(pos);
            if(User.favorites.contains(group.name)){
                group.setImage(R.drawable.ic_home);
            }else{
                group.setImage(R.drawable.ic_star_filled);
            }
            User.toggleSubscription(group.name);
            GroupAdapter.this.notifyDataSetChanged();
        }
    };
}
