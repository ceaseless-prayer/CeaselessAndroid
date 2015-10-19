package org.theotech.ceaselessandroid.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.codechimp.apprater.AppRater;
import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.fragment.FragmentBackStackManager;
import org.theotech.ceaselessandroid.fragment.FragmentState;
import org.theotech.ceaselessandroid.fragment.FragmentStateListener;
import org.theotech.ceaselessandroid.fragment.HomeFragment;
import org.theotech.ceaselessandroid.util.ActivityUtils;
import org.theotech.ceaselessandroid.util.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        FragmentStateListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigation;

    private FragmentBackStackManager backStackManager;
    private FragmentState currentFragment;

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

        // initialize the back stack
        backStackManager = new FragmentBackStackManager();

        // load the main fragment
        getFragmentManager().beginTransaction().add(R.id.fragment, new HomeFragment(),
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
            if (!backStackManager.isEmpty()) {
                FragmentState fragmentState = backStackManager.pop();
                ActivityUtils.loadFragment(this, getFragmentManager(), navigation, ActivityUtils.getNavigationItemIdForFragmentName(this, fragmentState.getFragmentName()),
                        fragmentState.getArguments());
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // handle navigation view item clicks here
        int id = item.getItemId();
        if (id == R.id.nav_rate_this_app) { // easter egg - option to load home fragment without using cache data
            // TODO: Remove this easter egg
            Bundle bundle = new Bundle();
            bundle.putBoolean(Constants.USE_CACHE_BUNDLE_ARG, false);
            ActivityUtils.loadFragment(this, getFragmentManager(), navigation, R.id.nav_rate_this_app, bundle, currentFragment);
        } else {
            // replace fragment if it's not already visible
            ActivityUtils.loadFragment(this, getFragmentManager(), navigation, id, currentFragment);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.person_add_note) {
            ActivityUtils.loadFragment(this, getFragmentManager(), navigation, id, currentFragment.getArguments(),
                    currentFragment);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public FragmentBackStackManager getFragmentBackStackManager() {
        return backStackManager;
    }

    @Override
    public void notify(FragmentState fragmentState) {
        // update current fragment
        currentFragment = fragmentState;
    }
}
