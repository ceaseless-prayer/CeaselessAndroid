package org.theotech.ceaselessandroid.fragment;


import android.app.Fragment;
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
import org.theotech.ceaselessandroid.person.Person;
import org.theotech.ceaselessandroid.person.PersonManager;
import org.theotech.ceaselessandroid.person.PersonManagerImpl;
import org.theotech.ceaselessandroid.person.PrayedForAllContacts;
import org.theotech.ceaselessandroid.scripture.ScriptureData;
import org.theotech.ceaselessandroid.scripture.ScriptureServiceImpl;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainFragment extends Fragment {

    private final String TAG = MainFragment.class.getSimpleName();

    @Bind(R.id.verse_image) ImageView verseImage;
    @Bind(R.id.pray_for_people_list) LinearLayout prayForPeopleList;
    @Bind(R.id.verse_title) TextView verseTitle;
    @Bind(R.id.verse_text) TextView verseText;

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
        final View view = inflater.inflate(R.layout.fragment_main, container);
        ButterKnife.bind(this, view);

        verseImage.setImageResource(R.drawable.icon_76);

        populatePrayForPeopleList();

        // asynchronous fetchers
        new ScriptureFetcher().execute();
        new ImageFetcher().execute();

        return view;
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
