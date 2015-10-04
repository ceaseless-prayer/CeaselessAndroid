package org.theotech.ceaselessandroid.fragment;


import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.cache.CacheManager;
import org.theotech.ceaselessandroid.cache.LocalCacheData;
import org.theotech.ceaselessandroid.cache.LocalDailyCacheManagerImpl;
import org.theotech.ceaselessandroid.image.ImageURLService;
import org.theotech.ceaselessandroid.image.ImageURLServiceImpl;
import org.theotech.ceaselessandroid.notification.NotificationService;
import org.theotech.ceaselessandroid.person.AlreadyPrayedForAllContactsException;
import org.theotech.ceaselessandroid.person.Person;
import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.person.PersonManagerImpl;
import org.theotech.ceaselessandroid.person.Persons;
import org.theotech.ceaselessandroid.prefs.TimePickerDialogPreference;
import org.theotech.ceaselessandroid.scripture.ScriptureData;
import org.theotech.ceaselessandroid.scripture.ScriptureService;
import org.theotech.ceaselessandroid.scripture.ScriptureServiceImpl;
import org.theotech.ceaselessandroid.util.Constants;

import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainFragment extends Fragment {
    private static final String TAG = MainFragment.class.getSimpleName();

    private final boolean useCache;

    @Bind(R.id.verse_image)
    ImageView verseImage;
    @Bind(R.id.pray_for_people_list)
    LinearLayout prayForPeopleList;
    @Bind(R.id.verse_title)
    TextView verseTitle;
    @Bind(R.id.verse_text)
    TextView verseText;
    @Bind(R.id.verse_share)
    TextView shareVerse;
    @Bind(R.id.view_and_pray)
    TextView viewAndPray;
    @Bind(R.id.prayer_progress)
    ProgressBar progress;
    @Bind(R.id.prayed_for_text)
    TextView prayedFor;
    @Bind(R.id.prayer_settings)
    Button prayerSettings;

    private ScriptureService scriptureService = null;
    private PersonManager personManager = null;
    private ImageURLService imageService = null;
    private CacheManager<LocalCacheData> cacheManager = null;

    public MainFragment() {
        // Required empty public constructor
        useCache = true;
    }

    public MainFragment(boolean useCache) {
        this.useCache = useCache;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scriptureService = ScriptureServiceImpl.getInstance();
        personManager = PersonManagerImpl.getInstance(getActivity());
        imageService = ImageURLServiceImpl.getInstance();
        cacheManager = LocalDailyCacheManagerImpl.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.app_name));
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        // verse card onclick listeners
        View.OnClickListener verseCardOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = getString(R.string.nav_verse);
                getFragmentManager().beginTransaction().replace(R.id.fragment, new VerseFragment(),
                        title).addToBackStack(null).commit();
            }
        };
        verseImage.setOnClickListener(verseCardOnClickListener);
        verseTitle.setOnClickListener(verseCardOnClickListener);
        verseText.setOnClickListener(verseCardOnClickListener);


        // populate prayer list
        populatePrayForPeopleList();
        // view and pray onclick listener
        viewAndPray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = getString(R.string.nav_people);
                getFragmentManager().beginTransaction().replace(R.id.fragment, new PeopleFragment(),
                        title).addToBackStack(null).commit();
            }
        });
        // update progress bar
        updateProgressBar();
        // prayer settings onclick listener
        prayerSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = getString(R.string.nav_settings);
                getFragmentManager().beginTransaction().replace(R.id.fragment, new SettingsFragment(),
                        title).addToBackStack(null).commit();
            }
        });

        // attempt to retrieve data from cache, otherwise kick off asynchronous fetchers
        final LocalCacheData cacheData = cacheManager.getCacheData();
        if (useCache && cacheData != null &&
                cacheData.getScriptureJson() != null &&
                !cacheData.getScriptureJson().isEmpty() &&
                cacheData.getScriptureText() != null &&
                !cacheData.getScriptureText().isEmpty() &&
                cacheData.getScriptureCitation() != null &&
                !cacheData.getScriptureCitation().isEmpty()) {
            Log.d(TAG, "Retrieving scripture from local daily cache");
            populateVerse(cacheData.getScriptureCitation(), cacheData.getScriptureText());
        } else {
            new ScriptureFetcher().execute();
        }

        View.OnClickListener shareVerseOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                if (useCache && cacheData != null  &&
                        cacheData.getScriptureText() != null &&
                        !cacheData.getScriptureText().isEmpty() &&
                        cacheData.getScriptureCitation() != null &&
                        !cacheData.getScriptureCitation().isEmpty())  {
                    sendIntent.putExtra(Intent.EXTRA_TEXT, cacheData.getScriptureCitation() + "\n" + cacheData.getScriptureText());
                } else {
                    sendIntent.putExtra(Intent.EXTRA_TEXT, verseTitle.getText() + "\n" + verseText.getText());
                }
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.verse_share)));
            }
        };
        shareVerse.setOnClickListener(shareVerseOnClickListener);
        if (useCache && cacheData != null &&
                cacheData.getVerseImageURL() != null &&
                !cacheData.getVerseImageURL().isEmpty()) {
            Log.d(TAG, "Retrieving verse imageUrl from local daily cache");
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage(getString(R.string.loading));
            dialog.show();
            Picasso.with(getActivity()).load(cacheData.getVerseImageURL()).fit().centerCrop().into(verseImage,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onError() {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    });
        } else {
            new ImageFetcher().execute();
        }

        // notification service code
        alarmMethod();

        return view;
    }

    private void populateVerse(String citation, String text) {
        verseTitle.setText(citation);
        verseText.setText(text);
    }

    private void populatePrayForPeopleList() {
        List<Person> persons = null;
        LocalCacheData cacheData = cacheManager.getCacheData();
        if (useCache && cacheData != null && cacheData.getPeopleToPrayFor() != null) {
            Log.d(TAG, "Retrieving prayForPeople list from local daily cache");
            persons = Persons.convert(cacheData.getPeopleToPrayFor());
        } else {
            try {
                persons = personManager.getNextPeopleToPrayFor(Constants.NUM_PERSONS);
                LocalCacheData localCacheData = new LocalCacheData();
                localCacheData.setCreationDate(LocalDailyCacheManagerImpl.generateCreationDate());
                localCacheData.setPeopleToPrayFor(Persons.convert(persons));
                cacheManager.cacheData(localCacheData);
            } catch (AlreadyPrayedForAllContactsException e) {
                // TODO: Celebrate!
            }
        }
        for (int i = 0; persons != null && !persons.isEmpty() && i < persons.size(); i++) {
            final View row = getActivity().getLayoutInflater().inflate(R.layout.pray_for_people_list, null);
            TextView textView = (TextView) row.findViewById(R.id.pray_for_person_name);
            textView.setText(persons.get(i).getName());

            final Uri u = getPhotoUri(persons.get(i).getId());
            final ImageView imageView = (ImageView) row.findViewById(R.id.pray_for_person_image);
            if (u != null) {
                imageView.setImageURI(u);
                if (imageView.getDrawable() == null) {
                    imageView.setImageResource(R.drawable.icon_76);
                }
            } else {
                imageView.setImageResource(R.drawable.icon_76);
            }
            prayForPeopleList.addView(row);
        }
    }

    /**
     * @return the contact's photo URI
     */
    private Uri getPhotoUri(String personId) {
        try {
            Cursor cur = getActivity().getApplicationContext().getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + "=" + personId + " AND "
                            + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                    null);
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            return null;
        }
        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long
                .parseLong(personId));
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    private void updateProgressBar() {
        long numPrayed = personManager.getNumPrayed();
        long numPeople = personManager.getNumPeople();
        prayedFor.setText(String.format(getString(R.string.prayed_for), numPrayed, numPeople));
        progress.setProgress((int) ((float) numPrayed / numPeople * 100.0f));
        progress.requestLayout();
    }

    private void alarmMethod() {
        Context context = getActivity();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean("showNotifications", true)) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent myIntent = new Intent(getActivity(), NotificationService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, myIntent, PendingIntent.FLAG_NO_CREATE);
            // FLAG_NO_CREATE means this will return null if there is not already a pending intent
            if (pendingIntent == null) {
                String time = preferences.getString("notificationTime", "08:30");
                pendingIntent = PendingIntent.getService(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                Log.d(TAG, "Setting reminder notification alarm");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.SECOND, 0);
                //calendar.add(Calendar.SECOND, 3);
                calendar.set(Calendar.MINUTE, TimePickerDialogPreference.getMinute(time));
                calendar.set(Calendar.HOUR, TimePickerDialogPreference.getHour(time));
                calendar.set(Calendar.AM_PM, Calendar.AM);
                calendar.add(Calendar.DAY_OF_MONTH, 1);

                alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingIntent);
            } else {
                Log.d(TAG, "Not setting reminder notification alarm. Already set.");
                //PendingIntent.getService(this, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            }
        }
    }

    private class ScriptureFetcher extends AsyncTask<String, Void, ScriptureData> {

        @Override
        protected ScriptureData doInBackground(String... params) {
            return scriptureService.getScripture();
        }

        @Override
        protected void onPostExecute(ScriptureData scripture) {
            if (scripture != null) {
                Log.d(TAG, "scripture = " + scripture.getJson());
                populateVerse(scripture.getCitation(), scripture.getText());
                // cache
                LocalCacheData cacheData = new LocalCacheData();
                cacheData.setCreationDate(LocalDailyCacheManagerImpl.generateCreationDate());
                cacheData.setScriptureCitation(scripture.getCitation());
                cacheData.setScriptureText(scripture.getText());
                cacheData.setScriptureJson(scripture.getJson());
                cacheManager.cacheData(cacheData);
            } else {
                Log.e(TAG, "Could not fetch scripture!");
                populateVerse(getString(R.string.default_verse_title), getString(R.string.default_verse_text));
            }
        }
    }

    private class ImageFetcher extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getString(R.string.loading));
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return imageService.getImageURL();
        }

        @Override
        protected void onPostExecute(String imageUrl) {
            if (imageUrl != null) {
                Log.d(TAG, "imageUrl = " + imageUrl);
                Picasso.with(getActivity()).load(imageUrl).fit().centerCrop().into(verseImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onError() {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
                // cache
                LocalCacheData cacheData = new LocalCacheData();
                cacheData.setCreationDate(LocalDailyCacheManagerImpl.generateCreationDate());
                cacheData.setVerseImageURL(imageUrl);
                cacheManager.cacheData(cacheData);
            } else {
                Log.e(TAG, "Could not fetch scripture!");
            }
        }
    }

}
