package com.lozasolutions.spaces4all.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.lozasolutions.spaces4all.R;
import com.lozasolutions.spaces4all.application.BaseApplication;
import com.lozasolutions.spaces4all.fragments.ListPoi;
import com.lozasolutions.spaces4all.fragments.Map;
import com.lozasolutions.spaces4all.services.LocationService;

import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    //LOCATION SERVICE
    LocationService locationService;
    static boolean locationServiceBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.mipmap.ic_main);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();

        //BIND LOCATION SERVICE
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        BaseApplication.getInstance().startService(i);
        BaseApplication.getInstance().bindService(new Intent(this, LocationService.class), locationConnection, 0);


    }

    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText(getString(R.string.list_tab_title));
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_format_list_bulleted_white_24dp, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText(getString(R.string.map_tab_title));
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_map_white_24dp, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ListPoi(), getString(R.string.list_tab_title));
        adapter.addFrag(new Map(), getString(R.string.map_tab_title));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection locationConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            locationService = binder.getService();
            locationServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

            locationServiceBound = false;
        }
    };

    @Override
    protected void onDestroy() {
        if (isFinishing()) {
            if (locationServiceBound) {
                if (locationService != null) {

                    BaseApplication.getInstance().unbindService(locationConnection);
                    Intent i = new Intent(getApplicationContext(), LocationService.class);
                    BaseApplication.getInstance().stopService(i);


                }
            }
        }

        super.onDestroy();
    }
}
