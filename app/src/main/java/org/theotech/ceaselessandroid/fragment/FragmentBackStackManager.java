package org.theotech.ceaselessandroid.fragment;

import java.util.Stack;

/**
 * Created by uberx on 10/16/15.
 */
public class FragmentBackStackManager {

    private final Stack<FragmentState> backstack;

    public FragmentBackStackManager() {
        this.backstack = new Stack<>();
    }

    public void add(FragmentState fragmentState) {
        backstack.add(fragmentState);
    }

    public FragmentState pop() {
        return backstack.pop();
    }

    public FragmentState peek() {
        return backstack.peek();
    }

    public int size() {
        return backstack.size();
    }

    public boolean isEmpty() {
        return backstack.isEmpty();
    }
}
