package org.theotech.ceaselessandroid.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.cache.CacheManager;
import org.theotech.ceaselessandroid.cache.LocalCacheData;
import org.theotech.ceaselessandroid.cache.LocalDailyCacheManagerImpl;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class VerseFragment extends Fragment {
    private static final String TAG = VerseFragment.class.getSimpleName();

    @Bind(R.id.verse_view_title)
    TextView verseViewTitle;
    @Bind(R.id.verse_view_text)
    TextView verseViewText;

    private CacheManager<LocalCacheData> cacheManager = null;

    public VerseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cacheManager = LocalDailyCacheManagerImpl.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.nav_verse));
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verse, container, false);
        ButterKnife.bind(this, view);

        LocalCacheData cacheData = cacheManager.getCacheData();
        verseViewTitle.setText(cacheData.getScriptureCitation());
        verseViewText.setText(cacheData.getScriptureText());

        return view;
    }


}
