package org.theotech.ceaselessandroid.prefs;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import org.theotech.ceaselessandroid.R;

/**
 * Created by jprobert on 10/4/2015.
 * with a lot of help from
 * https://github.com/commonsguy/cw-lunchlist/blob/master/19-Alarm/LunchList/src/apt/tutorial/TimePreference.java
 */
public class TimePickerDialogPreference extends DialogPreference {
    private static final String TAG = TimePickerDialogPreference.class.getSimpleName();

    private int lastHour = 0;
    private int lastMinute = 0;
    private TimePicker picker = null;
    private TextView timeView = null;

    public TimePickerDialogPreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);
    }

    public static int getHour(String time) {
        String[] pieces = time.split(":");
        return Integer.parseInt(pieces[0]);
    }

    public static int getMinute(String time) {
        String[] pieces = time.split(":");
        return Integer.parseInt(pieces[1]);
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        return picker;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.setCurrentHour(lastHour);
        picker.setCurrentMinute(lastMinute);
    }

    @Override
    protected void onBindView(View v) {
        super.onBindView(v);
        timeView = (TextView) v.findViewById(R.id.timeTextView);
        timeView.setText(getPersistedString(getContext().getString(R.string.default_notification_time)));
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            lastHour = picker.getCurrentHour();
            lastMinute = picker.getCurrentMinute();

            String time = String.format("%02d", lastHour) + ":" + String.format("%02d", lastMinute);

            if (callChangeListener(time)) {
                persistString(time);
            }

            timeView.setText(getPersistedString(getContext().getString(R.string.default_notification_time)));
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time = null;

        if (restoreValue) {
            if (defaultValue == null) {
                time = getPersistedString("00:00");
            }
        } else {
            time = defaultValue.toString();
        }

        lastHour = getHour(time);
        lastMinute = getMinute(time);
    }
}
