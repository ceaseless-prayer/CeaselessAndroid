package org.theotech.ceaselessandroid.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.realm.pojo.PersonPOJO;
import org.theotech.ceaselessandroid.util.Constants;

/**
 * Created by uberx on 10/16/15.
 */
public class PersonsCompletionView extends TokenCompleteTextView<PersonPOJO> {

    public PersonsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTokenClickStyle(TokenClickStyle.SelectDeselect);
    }

    @Override
    protected View getViewForObject(final PersonPOJO personPOJO) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.person_token, (ViewGroup) PersonsCompletionView.this.getParent(), false);
        TextView personTag = (TextView) view.findViewById(R.id.person_tag);
        personTag.setText(personPOJO.getName());
        personTag.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.PERSON_ID_BUNDLE_ARG, personPOJO.getId());
                Intent intent = new Intent(Constants.SHOW_PERSON_INTENT);
                intent.putExtras(bundle);
                getContext().startActivity(intent);
            }
        });

        return view;
    }

    @Override
    protected PersonPOJO defaultObject(String completionText) {
        return null;
    }
}
