package org.theotech.ceaselessandroid.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.cache.CacheManager;
import org.theotech.ceaselessandroid.cache.LocalCacheData;
import org.theotech.ceaselessandroid.cache.LocalDailyCacheManagerImpl;
import org.theotech.ceaselessandroid.person.Person;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private CacheManager<LocalCacheData> cacheManager;

    public PersonFragment() {
        // Required empty public constructor
        cacheManager = LocalDailyCacheManagerImpl.getInstance(getActivity());
    }

    public static PersonFragment newInstance(int sectionNumber) {
        PersonFragment fragment = new PersonFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        int index = getArguments().getInt(ARG_SECTION_NUMBER);
        // get data from cache
        Person person = cacheManager.getCacheData().getPeopleToPrayFor().get(index);

        TextView personName = (TextView) view.findViewById(R.id.person_name);
        personName.setText(person.getName());
        ImageView personImage = (ImageView) view.findViewById(R.id.person_image);
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.loading));
        dialog.show();
        Picasso.with(getActivity()).load(R.drawable.icon_76_2x).fit().centerCrop().into(personImage, new Callback() {
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

        return view;
    }


}
