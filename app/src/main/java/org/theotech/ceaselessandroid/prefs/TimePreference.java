package org.theotech.ceaselessandroid.prefs;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.preference.DialogPreference;

import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;

import org.theotech.ceaselessandroid.R;

import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by jprobert on 10/4/2015.
 * with a lot of help from
 * https://github.com/commonsguy/cw-lunchlist/blob/master/19-Alarm/LunchList/src/apt/tutorial/TimePreference.java
 */
public class TimePreference extends DialogPreference {
    private static final String TAG = TimePreference.class.getSimpleName();

    private int mHour = 0;
    private int mMinute = 0;

    public TimePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time = null;

        if (restoreValue) {
            if (defaultValue == null) {
                time = getPersistedString(getContext().getString(
                        R.string.default_notification_time));
            }
        } else {
            time = defaultValue.toString();
        }

        mHour = parseHour(time);
        mMinute = parseMinute(time);
        updateTitle();
    }

    public void updateTitle() {
        setTitle(getContext().getString(R.string.reminder_time_picker_title) + "   " +
                getPersistedStringValue(getContext().getString(R.string.default_notification_time)));
    }

    public void persistStringValue(String value) {
        persistString(value);
    }

    public String getPersistedStringValue(String defaultValue) {
        return getPersistedString(defaultValue);
    }

    public int getHour() {
        return mHour;
    }

    public void setHour(int mHour) {
        this.mHour = mHour;
    }

    public int getMinute() {
        return mMinute;
    }

    public void setMinute(int mMinute) {
        this.mMinute = mMinute;
    }

    public static String timeToString(int h, int m) {
        return String.format("%02d", h) + ":" + String.format("%02d", m);
    }

    public static int parseHour(String time) {
        try {
            String[] pieces = time.split(":");
            return Integer.parseInt(pieces[0]);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing hour, returning 0", e);
            return 0;
        }
    }

    public static int parseMinute(String time) {
        try {
            String[] pieces = time.split(":");
            return Integer.parseInt(pieces[1]);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing minute, returning 0", e);
            return 0;
        }
    }
}
