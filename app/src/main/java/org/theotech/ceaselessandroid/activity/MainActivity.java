package org.theotech.ceaselessandroid.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import org.codechimp.apprater.AppRater;
import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.fragment.MainFragment;
import org.theotech.ceaselessandroid.util.ActivityUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigation;
    @Bind(R.id.fragment)
    RelativeLayout fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigation.setCheckedItem(R.id.nav_home);
        navigation.setNavigationItemSelectedListener(this);

        // load the main fragment
        getFragmentManager().beginTransaction().add(R.id.fragment, new MainFragment(),
                getString(R.string.app_name)).commit();

        // rate my app dialog
        AppRater.app_launched(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // if there are fragments in the stack
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                // update the navigation checked item
                String fragmentName = getFragmentManager()
                        .getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 1)
                        .getName();
                navigation.setCheckedItem(ActivityUtils.getNavigationItemResourceIdForFragmentName(this, fragmentName));
                // go back to the previous fragment in the stack
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = ActivityUtils.getFragmentForNavigationItemId(id);
        String title = ActivityUtils.getTitleForNavigationItemId(this, id);
        if (id == R.id.nav_rate_this_app) { // hack option to load home fragment without using cache data
            // TODO: Remove this easter egg
            getFragmentManager().beginTransaction().replace(R.id.fragment, new MainFragment(false),
                    "Hack").addToBackStack(getTitle().toString()).commit();
        } else {
            // replace fragment if it's not already visible
            Fragment currentFragment = getFragmentManager().findFragmentByTag(title);
            if (fragment != null && (currentFragment == null || (currentFragment != null && !currentFragment.isVisible()))) {
                getFragmentManager().beginTransaction().replace(R.id.fragment, fragment, title)
                        .addToBackStack(getTitle().toString()).commit();
            } else {
                Log.d(TAG, String.format("Required fragment %s already visible, not reloading", title));
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
