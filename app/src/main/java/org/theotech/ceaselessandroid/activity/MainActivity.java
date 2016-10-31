package org.theotech.ceaselessandroid.activity;

import android.Manifest;
import android.app.Fragment;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
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
import android.widget.ImageView;
import android.widget.Toast;

import org.codechimp.apprater.AppRater;
import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.tutorial.Tutorial;
import org.theotech.ceaselessandroid.fragment.AddNoteFragment;
import org.theotech.ceaselessandroid.fragment.FragmentBackStackManager;
import org.theotech.ceaselessandroid.fragment.FragmentState;
import org.theotech.ceaselessandroid.fragment.FragmentStateListener;
import org.theotech.ceaselessandroid.fragment.HomeFragment;
import org.theotech.ceaselessandroid.tutorial.HomeTutorialFragment;
import org.theotech.ceaselessandroid.fragment.PeopleFragment;
import org.theotech.ceaselessandroid.notification.DailyNotificationReceiver;
import org.theotech.ceaselessandroid.person.PersonManagerImpl;
import org.theotech.ceaselessandroid.util.CommonUtils;
import org.theotech.ceaselessandroid.util.Constants;
import org.theotech.ceaselessandroid.util.FragmentUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        FragmentStateListener,
        ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int POPULATE_CONTACTS_REQUEST_CODE = 1;
    private static final int ADD_CONTACT_REQUEST_CODE = 2;
    private boolean homeFragmentCreated = false;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigation;
    @Bind(R.id.backgroundImageView)
    ImageView backgroundImageView;

    private FragmentBackStackManager backStackManager;
    private FragmentState currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Log.d(TAG, "reloading from savedInstanceState " + savedInstanceState);
        } else {
            Log.d(TAG, "Starting activity fresh");
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        navigation.setCheckedItem(R.id.nav_home);
        navigation.setNavigationItemSelectedListener(this);

        populateContacts();
        setNotification();
        CommonUtils.setupBackgroundImage(this, backgroundImageView);

        // initialize the back stack
        backStackManager = new FragmentBackStackManager();

        // if we were called with a special action, load the right fragment
        Intent intent = getIntent();
        handleIntent(intent);
        // TODO decide if we want to use AppRater's logic to surface a review request
        // AppRater.app_launched(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
        super.onNewIntent(intent);
    }

    private void handleIntent(Intent intent) {
        // if we were called with a special action, load the right fragment
        if (Constants.SHOW_PERSON_INTENT.equals(intent.getAction())) {
            PeopleFragment peopleFragment = new PeopleFragment();
            peopleFragment.setArguments(intent.getExtras());
            getFragmentManager().beginTransaction().replace(R.id.fragment, peopleFragment,
                    getString(R.string.nav_people)).commit();
        } else if (Constants.SHOW_NOTE_INTENT.equals(intent.getAction())) {
            AddNoteFragment addNoteFragment = new AddNoteFragment();
            addNoteFragment.setArguments(intent.getExtras());
            getFragmentManager().beginTransaction().replace(R.id.fragment, addNoteFragment,
                    getString(R.string.nav_journal)).commit();
        } else if (Tutorial.shouldShowTutorial(this)) {
            //loadHomeTutorialFragment();
            // We are testing to see response rates when we disable the tutorial and jump
            // straight to the main screen.
            loadMainFragment();
        } else {
            loadMainFragment();
        }
    }

    /**
     * Requests a backup operation to be done.
     */
    public void requestBackup() {
        BackupManager bm = new BackupManager(this);
        Log.i(TAG, "Backing up database and preferences");
        bm.dataChanged();
    }

    /**
     * Get a variable representing whether or not this activity
     * has ever created a {@link HomeFragment}.
     * The HomeFragment tracks which page it is on and returns to that
     * page when it is recreated unless the MainActivity has never created it before.
     * So for example, when a user starts the app, HomeFragment starts on the first page,
     * but when the user returns to Home from an AddNote fragment, it returns to the page
     * the user was on.
     * @return true if this activity has created a HomeFragment before, false otherwise.
     */
    public boolean getHomeFragmentCreated() {
        return homeFragmentCreated;
    }

    public void setHomeFragmentCreated(boolean homeFragmentCreated) {
        this.homeFragmentCreated = homeFragmentCreated;
    }

    public void loadMainFragment() {
        getFragmentManager().beginTransaction().replace(R.id.fragment, new HomeFragment(),
                getString(R.string.nav_home)).commit();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        toggle.syncState();

        // TODO when do we request backups?
        requestBackup();
    }

    public void loadHomeTutorialFragment() {
        Fragment frag = new HomeTutorialFragment();
        String title = getString(R.string.nav_home_tutorial);
        getFragmentManager().beginTransaction().replace(R.id.fragment, frag, title).commit();
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
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
                    Toast.makeText(MainActivity.this, getString(R.string.contacts_access_required),
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
            // rate my app dialog
            AppRater.rateNow(this);
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

        if (id == R.id.add_contact) {
            Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
            intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
            startActivityForResult(intent, ADD_CONTACT_REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_CONTACT_REQUEST_CODE) {
            // pick up any new contacts that have been added.
            // TODO make this more efficient by adding just the one that has been added.
            // I did this because I noticed that the data was coming back null even though a contact was added.
            // Also right now the populateContacts has a filtering logic and so if they add a contact
            // without email or phone number, it still won't show up in Ceaseless unfortunately.
            populateContacts();
        }
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
