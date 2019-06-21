package org.theotech.ceaselessandroid.view;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.theotech.ceaselessandroid.R;

/**
 * Created by kirisu on 6/14/16.
 * https://github.com/splitwise/TokenAutoComplete
 * We want custom behavior when a user clicks on a token: launching a detail view of the object.
 */
public class TokenLayout extends LinearLayout {

    private static final String TAG = TokenLayout.class.getSimpleName();

    public TokenLayout(Context context) {
        super(context);
    }

    public TokenLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TokenLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            TextView v = findViewById(R.id.person_tag);
            v.callOnClick();
        }
    }
}
