package com.bob.mhslife;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Bob on 8/21/2016.
 */
public class EventAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MHSEvent> events;
    private LayoutInflater inflater;

    private Typeface KGMissKindyChunky;

    private final class ViewHolder {
        private TextView eventTV;
        private TextView timeTV;
    }

    private ViewHolder holder;

    public EventAdapter(Context context, ArrayList<MHSEvent> events){
        this.context = context;
        this.events = events;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int index) {
        return events.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    public View getView(int index, View view, ViewGroup parent){
        ViewHolder holder;
        if(view == null){
            view = inflater.inflate(R.layout.event_cell, parent, false);
            holder = new ViewHolder();
            holder.eventTV = (TextView) view.findViewById(R.id.eventTV);
            holder.timeTV = (TextView) view.findViewById(R.id.timeTV);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        MHSEvent event = events.get(index);

        if(event != null){
            KGMissKindyChunky = Typeface.createFromAsset(context.getAssets(), "kgmisskindychunky.ttf");

            holder.eventTV.setText(event.name);
            holder.eventTV.setTypeface(KGMissKindyChunky);

            String time = new SimpleDateFormat("HH:mm").format(strToDate(event.date).getTime());
            if(time.equals("00:00")){
                holder.timeTV.setText("");
            }else{
                holder.timeTV.setText(time);
                holder.timeTV.setTypeface(KGMissKindyChunky);
            }
        }

        return view;
    }

    private Date strToDate(String dateString){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try{
            Date date = format.parse(dateString);
            return date;
        }catch(ParseException e){
            Log.e("EventAdapter", "Failed to Parse date");
            e.printStackTrace();
            return null;
        }
    }
}
