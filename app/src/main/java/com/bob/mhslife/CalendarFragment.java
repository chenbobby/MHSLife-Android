package com.bob.mhslife;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
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
    private TextView emptyEventsTV;
    private PopupWindow eventInfoPopupWindow;
    private LayoutInflater layoutInflater;

    private Typeface Biko;
    private Typeface KGMissKindyChunky;
    private Typeface RoundedElegance;

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
        emptyEventsTV = (TextView) V.findViewById(R.id.emptyEventsTV);

        initFonts();

        monthTV.setTypeface(Biko);
        emptyEventsTV.setTypeface(KGMissKindyChunky);

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
                    emptyEventsTV.setVisibility(View.VISIBLE);
                }else{
                    emptyEventsTV.setVisibility(View.GONE);
                }
                dayEventsLV.setAdapter(new EventAdapter(getContext(), dayEvents));
                setEventLVCLickListener();

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
                                (String) event.child("location").getValue(),
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
                    compactCalendarView.addEvent(new Event(Color.RED, eventDate.getTime()));
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

    private void setEventLVCLickListener(){
        dayEventsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final MHSEvent event = (MHSEvent) (adapterView.getAdapter().getItem(i));
                layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.popoverwindow_eventinfo, null);

                eventInfoPopupWindow = new PopupWindow(container, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);

                ((TextView) eventInfoPopupWindow.getContentView().findViewById(R.id.eventGroupTV)).setText(event.group);
                ((TextView) eventInfoPopupWindow.getContentView().findViewById(R.id.eventNameTV)).setText(event.name);
                ((TextView) eventInfoPopupWindow.getContentView().findViewById(R.id.eventLocationTV)).setText(event.location);
                ((TextView) eventInfoPopupWindow.getContentView().findViewById(R.id.eventDescriptionTV)).setText(event.description);
                Date eventDateTime = strToDate(event.date);
                ((TextView) eventInfoPopupWindow.getContentView().findViewById(R.id.eventDateTV)).setText(android.text.format.DateFormat.format("EEEE MMMM d", eventDateTime));
                ((TextView) eventInfoPopupWindow.getContentView().findViewById(R.id.eventTimeTV)).setText(android.text.format.DateFormat.format("HH:mm", eventDateTime));

                if(event.group.equals("mcclintock")){
                    ((TextView) eventInfoPopupWindow.getContentView().findViewById(R.id.eventGroupTV)).setText("McClintock");
                }

                if(android.text.format.DateFormat.format("HH:mm", eventDateTime).equals("00:00")){
                    ((TextView) eventInfoPopupWindow.getContentView().findViewById(R.id.eventTimeTV)).setText("");
                }

                ((TextView) eventInfoPopupWindow.getContentView().findViewById(R.id.eventGroupTV)).setTypeface(Biko);
                ((TextView) eventInfoPopupWindow.getContentView().findViewById(R.id.eventNameTV)).setTypeface(RoundedElegance);
                ((TextView) eventInfoPopupWindow.getContentView().findViewById(R.id.eventLocationTV)).setTypeface(Biko);
                ((TextView) eventInfoPopupWindow.getContentView().findViewById(R.id.eventDescriptionTV)).setTypeface(Biko);
                ((TextView) eventInfoPopupWindow.getContentView().findViewById(R.id.eventDateTV)).setTypeface(Biko);
                ((TextView) eventInfoPopupWindow.getContentView().findViewById(R.id.eventTimeTV)).setTypeface(RoundedElegance);

                eventInfoPopupWindow.showAtLocation(getView(), Gravity.NO_GRAVITY, 0, 0);
                container.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        eventInfoPopupWindow.dismiss();
                        return true;
                    }
                });
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

    //Init Fonts
    private void initFonts(){
        Biko = Typeface.createFromAsset(getActivity().getAssets(), "biko.otf");
        KGMissKindyChunky = Typeface.createFromAsset(getActivity().getAssets(), "kgmisskindychunky.ttf");
        RoundedElegance = Typeface.createFromAsset(getActivity().getAssets(), "roundedelegance.ttf");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(eventListener != null){
            eventRef.removeEventListener(eventListener);
        }
    }
}
