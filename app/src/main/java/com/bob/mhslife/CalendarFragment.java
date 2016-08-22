package com.bob.mhslife;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarFragment extends Fragment {

    private static final String TAG = "CalendarFragment";

    private DatabaseReference ref;
    private DatabaseReference eventRef;
    private ValueEventListener eventListener;

    private TextView monthTV;
    private CompactCalendarView compactCalendarView;
    private ListView dayEventsLV;

    private ArrayList<MHSEvent> events;
    private ArrayList<MHSEvent> viewableEvents;
    private ArrayList<MHSEvent> dayEvents;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.fragment_calendar, container, false);

        ref = FirebaseDatabase.getInstance().getReference();
        events = new ArrayList<MHSEvent>();
        User.favoriteEvents = new ArrayList<MHSEvent>();
        viewableEvents = new ArrayList<MHSEvent>();
        dayEvents = new ArrayList<MHSEvent>();

        monthTV = (TextView) V.findViewById(R.id.monthTV);
        dayEventsLV = (ListView) V.findViewById(R.id.dayEventsLV);

        compactCalendarView = (CompactCalendarView) V.findViewById(R.id.compactcalendar_view);
        compactCalendarView.setShouldShowMondayAsFirstDay(false);
        compactCalendarView.setUseThreeLetterAbbreviation(true);
        compactCalendarView.getFirstDayOfCurrentMonth();

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                dayEvents.clear();
                for(MHSEvent event : viewableEvents){
                    if(isOnSameDay(dateClicked, strToDate(event.date))){
                        dayEvents.add(event);
                    }
                }
                if(dayEvents.size() == 0){
                    dayEvents.add(new MHSEvent(false, "", "Select a Day With Events", "2000-01-01 00:00", "", ""));
                }
                dayEventsLV.setAdapter(new EventAdapter(getContext(), dayEvents));

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                monthTV.setText(android.text.format.DateFormat.format("MMMM", firstDayOfNewMonth));
            }
        });

        monthTV.setText(android.text.format.DateFormat.format("MMMM", compactCalendarView.getFirstDayOfCurrentMonth()));

        loadEvents();

        return V;
    }

    // Load Data
    private void loadEvents(){
        eventRef = ref.child("events/");
        eventListener = eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot event : dataSnapshot.getChildren()) {
                        events.add(new MHSEvent(
                                (Boolean) event.child("exclusive").getValue(),
                                (String) event.child("group").getValue(),
                                event.getKey(),
                                (String) event.child("date").getValue(),
                                (String) event.child("locaiton").getValue(),
                                (String) event.child("description").getValue()));
                    }
                }
                else{
                    Log.e(TAG, "No Events");
                }


                for(MHSEvent event : events){
                    // TODO: figure out why the .contains below doesn't work with "mcclintock"
                    if(User.favorites.contains(event.group) || event.group.equals("mcclintock")){
                        User.favoriteEvents.add(event);
                        viewableEvents.add(event);
                    }else if(!event.exclusive){
                        viewableEvents.add(event);
                    }
                }

                for(MHSEvent event : User.favoriteEvents){
                    Date eventDate = strToDate(event.date);
                    compactCalendarView.addEvent(new Event(Color.BLUE, eventDate.getTime()));
                }

                compactCalendarView.invalidate();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to connect");
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    private Date strToDate(String dateString){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try{
            Date date = format.parse(dateString);
            return date;
        }catch(ParseException e){
            Log.e(TAG, "Failed to Parse date");
            e.printStackTrace();
            return null;
        }
    }

    private boolean isOnSameDay(Date date1, Date date2){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(date1).equals(format.format(date2));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(eventListener != null){
            eventRef.removeEventListener(eventListener);
        }
    }
}
