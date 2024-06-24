package org.theotech.ceaselessandroid.util;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import org.theotech.ceaselessandroid.realm.pojo.PersonPOJO;

public class PeopleDiffCallback extends DiffUtil.ItemCallback<PersonPOJO> {

    @Override
    public boolean areItemsTheSame(@NonNull PersonPOJO oldPerson, @NonNull PersonPOJO newPerson) {
        return oldPerson.equals(newPerson);
    }

    @Override
    public boolean areContentsTheSame(@NonNull PersonPOJO oldPerson, @NonNull PersonPOJO newPerson) {
        return oldPerson.getId().equals(newPerson.getId());
    }
}