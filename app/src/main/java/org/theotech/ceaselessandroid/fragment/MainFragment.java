package org.theotech.ceaselessandroid.fragment;


import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.image.ImageURLServiceImpl;
import org.theotech.ceaselessandroid.notification.NotificationService;
import org.theotech.ceaselessandroid.person.Person;
import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.person.PersonManagerImpl;
import org.theotech.ceaselessandroid.person.PrayedForAllContacts;
import org.theotech.ceaselessandroid.scripture.ScriptureData;
import org.theotech.ceaselessandroid.scripture.ScriptureServiceImpl;

import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainFragment extends Fragment {

    private final String TAG = MainFragment.class.getSimpleName();

    @Bind(R.id.verse_image)
    ImageView verseImage;
    @Bind(R.id.pray_for_people_list)
    LinearLayout prayForPeopleList;
    @Bind(R.id.verse_title)
    TextView verseTitle;
    @Bind(R.id.verse_text)
    TextView verseText;

    private PersonManager personManager = null;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        personManager = PersonManagerImpl.getInstance(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        verseImage.setImageResource(R.drawable.icon_76);

        populatePrayForPeopleList();

        // asynchronous fetchers
        new ScriptureFetcher().execute();
        new ImageFetcher().execute();

        // notification service code
        alarmMethod();

        return view;
    }

    private void alarmMethod() {
        Context context = getActivity();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent myIntent = new Intent(getActivity(), NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, myIntent, PendingIntent.FLAG_NO_CREATE);
        // FLAG_NO_CREATE means this will return null if there is already a pending intent
        if (pendingIntent == null) {
            pendingIntent = PendingIntent.getService(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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

    private void populatePrayForPeopleList() {
        List<Person> persons = null;
        try {
            persons = personManager.getNextPeopleToPrayFor(3);
        } catch (PrayedForAllContacts prayedForAllContacts) {
            prayedForAllContacts.printStackTrace();
        }
        for (int i = 0; i < persons.size(); i++) {
            View row = getActivity().getLayoutInflater().inflate(R.layout.pray_for_people_list, null);
            TextView textView = (TextView) row.findViewById(R.id.pray_for_person_name);
            textView.setText(persons.get(i).getName());
            ImageView imageView = (ImageView) row.findViewById(R.id.pray_for_person_image);
            imageView.setImageResource(R.drawable.icon_76);
            prayForPeopleList.addView(row);
        }
    }

    private class ScriptureFetcher extends AsyncTask<String, Void, ScriptureData> {

        @Override
        protected ScriptureData doInBackground(String... params) {
            return new ScriptureServiceImpl().getScripture();
        }

        @Override
        protected void onPostExecute(ScriptureData scripture) {
            if (scripture != null) {
                Log.d(TAG, "scripture = " + scripture.getJson());

                verseTitle.setText(scripture.getCitation());

                verseText.setText(scripture.getText());
            } else {
                Log.e(TAG, "Could not fetch scripture!");

                verseTitle.setText("Matthew 21:22");

                verseText.setText("And whatever you ask in prayer, you will receive, if you have faith.\"");
            }
        }
    }

    private class ImageFetcher extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return new ImageURLServiceImpl().getImageURL();
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
