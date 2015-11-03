package org.theotech.ceaselessandroid.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import org.codechimp.apprater.AppRater;
import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.fragment.FragmentBackStackManager;
import org.theotech.ceaselessandroid.fragment.FragmentState;
import org.theotech.ceaselessandroid.fragment.FragmentStateListener;
import org.theotech.ceaselessandroid.fragment.HomeFragment;
import org.theotech.ceaselessandroid.notification.DailyNotificationReceiver;
import org.theotech.ceaselessandroid.person.PersonManagerImpl;
import org.theotech.ceaselessandroid.util.Constants;
import org.theotech.ceaselessandroid.util.FragmentUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        FragmentStateListener,
        ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int POPULATE_CONTACTS_REQUEST_CODE = 1;

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

        populateContacts();
        setNotification();

        // initialize the back stack
        backStackManager = new FragmentBackStackManager();

        // load the main fragment
        getFragmentManager().beginTransaction().add(R.id.fragment, new HomeFragment(),
                getString(R.string.nav_home)).commit();

        // rate my app dialog
        AppRater.app_launched(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case POPULATE_CONTACTS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PersonManagerImpl.getInstance(this).populateContacts();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "READ_CONTACTS Denied",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!backStackManager.isEmpty()) {
                FragmentState fragmentState = backStackManager.pop();
                FragmentUtils.loadFragment(this, getFragmentManager(), navigation, FragmentUtils.getNavigationItemIdForFragmentName(this, fragmentState.getFragmentName()),
                        fragmentState.getState());
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
            FragmentUtils.loadFragment(this, getFragmentManager(), navigation, R.id.nav_rate_this_app, bundle, currentFragment);
        } else {
            // replace fragment if it's not already visible
            FragmentUtils.loadFragment(this, getFragmentManager(), navigation, id, currentFragment);
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
            FragmentUtils.loadFragment(this, getFragmentManager(), navigation, id, currentFragment.getState(),
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

    private void populateContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.READ_CONTACTS)) {
                Log.d(TAG, "Should show rationale");
                // TODO: Display a fragment or something here.
                // From the developers handbook:
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        populateContacts();
                    }
                }, 100);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        POPULATE_CONTACTS_REQUEST_CODE);
            }
            return;
        }
        PersonManagerImpl.getInstance(this).populateContacts();
    }

    private void setNotification() {
        Intent dailyNotificationReceiver = new Intent(getApplicationContext(), DailyNotificationReceiver.class);
        getApplicationContext().sendBroadcast(dailyNotificationReceiver);
    }
}
