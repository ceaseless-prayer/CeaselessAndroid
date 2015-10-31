package org.theotech.ceaselessandroid.fragment;

import android.os.Bundle;

/**
 * Created by uberx on 10/17/15.
 */
public class FragmentState {
    private final String fragmentName;
    private Bundle state;

    public FragmentState(String fragmentName) {
        this.fragmentName = fragmentName;
    }

    public FragmentState(String fragmentName, Bundle state) {
        this.fragmentName = fragmentName;
        this.state = state;
    }

    public String getFragmentName() {
        return fragmentName;
    }

    public Bundle getState() {
        return state;
    }

    public void setState(Bundle state) {
        this.state = state;
    }
}
