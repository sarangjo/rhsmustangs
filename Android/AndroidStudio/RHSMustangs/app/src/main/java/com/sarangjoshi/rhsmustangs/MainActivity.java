package com.sarangjoshi.rhsmustangs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.sarangjoshi.rhsmustangs.fragments.HomeFragment;
import com.sarangjoshi.rhsmustangs.fragments.LinksFragment;
import com.sarangjoshi.rhsmustangs.fragments.NavigationDrawerFragment;
import com.sarangjoshi.rhsmustangs.fragments.ScheduleFragment;
import com.sarangjoshi.rhsmustangs.fragments.SettingsFragment;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * The section number of a fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fragment managing the behaviors, interactions and presentation of the navigation drawer.
        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)
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
                selected = HomeFragment.newInstance();
                break;
            case 1:
                selected = LinksFragment.newInstance(position);
                break;
            case 2:
                selected = ScheduleFragment.newInstance(position);
                break;
            case 3:
                selected = SettingsFragment.newInstance(position);
                break;
            default:
                selected = null;
                break;
        }
        if(selected != null)
            fragmentManager.beginTransaction()
                .replace(R.id.container, selected)
                .commit();
    }

    public void onSectionAttached(int number) {
        if (number == 0) {
            setTitle(getString(R.string.app_name));
        } else {
            setTitle(NavigationDrawerFragment.DRAWER_LIST[number]);
        }
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

}
