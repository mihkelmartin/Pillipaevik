package com.vaskjala.vesiroosi20.pillipaevik;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;


import android.support.v7.widget.Toolbar;


/**
 * Created by mihkel on 11.06.2016.
 */
public class HarjutusteKalenderActivity extends AppCompatActivity {

    private HarjutusteKalenderAdapter mharjutusteKalenderAdapter;
    private ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harjutuste_kalender);

        Toolbar toolbar = (Toolbar) findViewById(R.id.kalender_toolbar);
        setSupportActionBar(toolbar);
        ActionBar mAction = getSupportActionBar();
        mAction.setDisplayHomeAsUpEnabled(true);


        mharjutusteKalenderAdapter =
                new HarjutusteKalenderAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mharjutusteKalenderAdapter);
    }

    public class HarjutusteKalenderAdapter extends FragmentPagerAdapter {
        @Override
        public CharSequence getPageTitle(int position) {
            return "Lehekülg " + position;
        }

        public HarjutusteKalenderAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new HarjutusteKalendriLeht();
            Bundle args = new Bundle();
            // Our object is just an integer :-P
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 1;
        }
    }





}

