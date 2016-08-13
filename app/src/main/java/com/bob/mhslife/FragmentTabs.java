package com.bob.mhslife;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

/**
 * Created by Bob on 8/12/2016.
 */
public class FragmentTabs extends FragmentActivity {
    private FragmentTabHost mTabHost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tabs);

        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("home").setIndicator("Home"), HomeFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("calendar").setIndicator("Calendar"), CalendarFragment.class, null);
    }
}
