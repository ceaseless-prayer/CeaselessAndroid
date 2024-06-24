package org.theotech.ceaselessandroid.util;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import org.theotech.ceaselessandroid.realm.pojo.NotePOJO;

public class NotesDiffCallback extends DiffUtil.ItemCallback<NotePOJO> {
    @Override
    public boolean areItemsTheSame(@NonNull NotePOJO oldNote, @NonNull NotePOJO newNote) {
        return oldNote.equals(newNote);
    }

    @Override
    public boolean areContentsTheSame(@NonNull NotePOJO oldNote, @NonNull NotePOJO newNote) {
        return oldNote.getId().equals(newNote.getId());
    }
}