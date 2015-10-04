package org.theotech.ceaselessandroid;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "ben";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageView verseImage = (ImageView) findViewById(R.id.verse_image);
        verseImage.setImageResource(R.drawable.icon_76);

        new ScriptureFetcher().execute();

        new ImageFetcher().execute();
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

    private class ScriptureFetcher extends AsyncTask<String, Void, ScriptureData> {

        @Override
        protected ScriptureData doInBackground(String... params) {
            return new ScriptureService().getScripture();
        }

        @Override
        protected void onPostExecute(ScriptureData scripture) {
            if (scripture != null) {
                Log.d(TAG, "scripture = " + scripture.getJson());

                TextView verseTitle = (TextView) findViewById(R.id.verse_title);
                verseTitle.setText(scripture.getCitation());

                TextView verseText = (TextView) findViewById(R.id.verse_text);
                verseText.setText(scripture.getText());
            } else {
                Log.e(TAG, "Could not fetch scripture!");

                TextView verseTitle = (TextView) findViewById(R.id.verse_title);
                verseTitle.setText("Matthew 21:22");

                TextView verseText = (TextView) findViewById(R.id.verse_text);
                verseText.setText("And whatever you ask in prayer, you will receive, if you have faith.\"");
            }
        }
    }

    private class ImageFetcher extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                return new ImageURLService().getImageURL();
            }

            @Override
            protected void onPostExecute(String imageUrl) {
                if (imageUrl != null) {
                    Log.d(TAG, "imageUrl = " + imageUrl);

                    // TODO: Download the image and display using picasso
                } else {
                    Log.e(TAG, "Could not fetch scripture!");
                }
            }
    }
}
