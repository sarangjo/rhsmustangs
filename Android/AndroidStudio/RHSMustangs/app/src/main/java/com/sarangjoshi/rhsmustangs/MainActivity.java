package com.sarangjoshi.rhsmustangs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.sarangjoshi.rhsmustangs.fragments.LinksFragment;
import com.sarangjoshi.rhsmustangs.fragments.NavigationDrawerFragment;
import com.sarangjoshi.rhsmustangs.fragments.ScheduleFragment;
import com.sarangjoshi.rhsmustangs.fragments.SettingsFragment;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, SettingsFragment.SettingsListener {
    /**
     * The section number of a fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";
    private boolean overrideRefresh = false;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fragment managing the behaviors, interactions and presentation of the navigation drawer.
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // Update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment selected;

        switch (position) {
            case 0:
                selected = LinksFragment.newInstance(position);
                break;
            case 1:
                selected = ScheduleFragment.newInstance(position);
                // First sees if the schedule has been naturally initialized
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                boolean init = sp.getBoolean(ScheduleFragment.PREF_INITIALIZED, false);
                boolean upd = sp.getBoolean(ScheduleFragment.PREF_UPDATED, false);

                ((ScheduleFragment) selected).setInitialized(!overrideRefresh && init);
                ((ScheduleFragment) selected).setUpdated(upd);

                this.overrideRefresh = false;
                break;
            case 2:
                selected = SettingsFragment.newInstance(this, position);
                break;
            default:
                selected = null;
                break;
        }

        if (selected != null)
            fragmentManager.beginTransaction()
                    .replace(R.id.container, selected)
                    .commit();
    }

    /**
     * @param position the position of the section
     */
    public void onSectionAttached(int position) {
        if (position == 0) {
            setTitle(getString(R.string.app_name));
        } else {
            setTitle(NavigationDrawerFragment.DRAWER_LIST[position]);
        }
    }

    @Override
    public void refreshBase() {
        this.overrideRefresh = true;
        mNavigationDrawerFragment.selectItem(1);
    }
}
