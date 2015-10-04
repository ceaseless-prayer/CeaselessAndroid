package org.theotech.ceaselessandroid.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.fragment.MainFragment;
import org.theotech.ceaselessandroid.notification.NotificationService;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.drawer_layout) DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigation;

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

        navigation.setNavigationItemSelectedListener(this);

        // notification service code
        alarmMethod();
    }

    private void alarmMethod(){
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        Intent myIntent = new Intent(this , NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, myIntent, PendingIntent.FLAG_NO_CREATE);
        // FLAG_NO_CREATE means this will return null if there is already a pending intent
        if (pendingIntent == null) {
            pendingIntent = PendingIntent.getService(this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Log.d(TAG, "Setting reminder notification alarm");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.SECOND, 0);
            //calendar.add(Calendar.SECOND, 3);
            calendar.set(Calendar.MINUTE, 30);
            calendar.set(Calendar.HOUR, 8);
            calendar.set(Calendar.AM_PM, Calendar.AM);
            calendar.add(Calendar.DAY_OF_MONTH, 1);

            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingIntent);
        } else {
            Log.d(TAG, "Not setting reminder notification alarm. Already set.");
            //PendingIntent.getService(this, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

        if (id == R.id.nav_home) {
            MainFragment fragment = new MainFragment();
            getFragmentManager().beginTransaction().replace(R.id.main_fragment, fragment).commit();
        } else if (id == R.id.nav_people) {

        } else if (id == R.id.nav_verse) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_contact_us) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
