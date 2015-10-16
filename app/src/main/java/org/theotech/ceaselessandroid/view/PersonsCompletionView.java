package org.theotech.ceaselessandroid.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.realm.pojo.PersonPOJO;

/**
 * Created by uberx on 10/16/15.
 */
public class PersonsCompletionView extends TokenCompleteTextView<PersonPOJO> {
    public PersonsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View getViewForObject(PersonPOJO personPOJO) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.person_token, (ViewGroup) PersonsCompletionView.this.getParent(), false);
        TextView personTag = (TextView) view.findViewById(R.id.person_tag);
        personTag.setText(personPOJO.getName());

        return view;
    }

    @Override
    protected PersonPOJO defaultObject(String completionText) {
        return null;
    }
}
