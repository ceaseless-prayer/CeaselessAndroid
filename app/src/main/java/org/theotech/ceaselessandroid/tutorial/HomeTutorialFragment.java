package org.theotech.ceaselessandroid.tutorial;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.CirclePageIndicator;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.activity.MainActivity;
import org.theotech.ceaselessandroid.fragment.BlankSupportFragment;
import org.theotech.ceaselessandroid.fragment.FragmentState;
import org.theotech.ceaselessandroid.fragment.FragmentStateListener;
import org.theotech.ceaselessandroid.transformer.ZoomOutPageTransformer;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * created by travis Feb.2016
 */

public class HomeTutorialFragment extends Fragment {
    private static final String TAG = HomeTutorialFragment.class.getSimpleName();
    private static final Integer CARD_COUNT = 3;
    @Bind(R.id.tutorial_viewpager)
    ViewPager viewPager;
    @Bind(R.id.tutorial_indicator)
    CirclePageIndicator indicator;

    private FragmentStateListener mListener;

    public HomeTutorialFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // notify fragment state
        try {
            mListener = (FragmentStateListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement FragmentStateListener");
        }
        Bundle currentState = new Bundle();
        mListener.notify(new FragmentState(getString(R.string.nav_home_tutorial), currentState));

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {

        // set title
        String title = getString(R.string.nav_home_tutorial);
        getActivity().setTitle(title);

        // create view
        View view = inflater.inflate(R.layout.fragment_home_tutorial, container, false);
        ButterKnife.bind(this, view);

       final FragmentStatePagerAdapter pagerAdapter =
               new FragmentStatePagerAdapter(((AppCompatActivity) getActivity()).getSupportFragmentManager()) {
                   List<android.support.v4.app.Fragment> pages = Arrays.asList(new android.support.v4.app.Fragment[CARD_COUNT]);

                   @Override
                   public android.support.v4.app.Fragment getItem(int i) {
                       if (pages.get(i) == null) {
                           switch (i) {
                               case 0:
                                   pages.set(i, HTFDemoScriptureFragment.newInstance(viewPager));
                                   break;
                               case 1:
                                   pages.set(i, HTFDemoPersonFragment.newInstance(getString(R.string.tutorial_person_name), true));
                                   break;
                               case 2:
                                   pages.set(i, new HTFDemoProgressFragment());
                                   break;
                               default:
                                   pages.set(i, new BlankSupportFragment());
                                   break;
                           }
                       }
                       return pages.get(i);
                   }

                   @Override
                   public int getCount() {
                       return CARD_COUNT;
                   }
               };

        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                ((HTFDemoFragment) pagerAdapter.getItem(i)).onSelected();
            }
        });
        indicator.setViewPager(viewPager);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu, menu);
        MenuItem search = menu.findItem(R.id.search);
        MenuItem addNote = menu.findItem(R.id.person_add_note);
        MenuItem skipTutorial = menu.findItem(R.id.skip_tutorial);
        search.setVisible(false);
        addNote.setVisible(false);
        skipTutorial.setVisible(true);
        skipTutorial.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ((MainActivity) getActivity()).loadMainFragment();
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }


}
