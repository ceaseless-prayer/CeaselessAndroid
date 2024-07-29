package org.theotech.ceaselessandroid.prefs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.preference.PreferenceDialogFragmentCompat;

import org.theotech.ceaselessandroid.R;

public class TimePreferenceDialog extends PreferenceDialogFragmentCompat {
    private static final String TAG = TimePreferenceDialog.class.getSimpleName();

    private TimePicker picker = null;

    @Override
    protected View onCreateDialogView(Context context) {
        picker = new TimePicker(context);
        return picker;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        TimePreference pref = (TimePreference) getPreference();
        picker.setHour(pref.getHour());
        picker.setMinute(pref.getMinute());
        pref.updateTitle();
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            TimePreference pref = (TimePreference) getPreference();
            pref.setHour(picker.getHour());
            pref.setMinute(picker.getMinute());

            String time = TimePreference.timeToString(pref.getHour(), pref.getMinute());

            if (pref.callChangeListener(time)) {
                pref.persistStringValue(time);
            }
            pref.updateTitle();
        }
    }
}
