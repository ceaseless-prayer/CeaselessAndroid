package org.theotech.ceaselessandroid.fragment;

import android.os.Bundle;

/**
 * Created by uberx on 10/17/15.
 */
public class FragmentState {
    private final String fragmentName;
    private Bundle arguments;

    public FragmentState(String fragmentName) {
        this.fragmentName = fragmentName;
    }

    public FragmentState(String fragmentName, Bundle arguments) {
        this.fragmentName = fragmentName;
        this.arguments = arguments;
    }

    public String getFragmentName() {
        return fragmentName;
    }

    public Bundle getArguments() {
        return arguments;
    }

    public void setArguments(Bundle arguments) {
        this.arguments = arguments;
    }
}
