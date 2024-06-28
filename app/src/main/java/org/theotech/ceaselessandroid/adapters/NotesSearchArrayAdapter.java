package org.theotech.ceaselessandroid.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.base.Joiner;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.realm.pojo.NotePOJO;
import org.theotech.ceaselessandroid.util.CommonUtils;
import org.theotech.ceaselessandroid.util.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class NotesSearchArrayAdapter extends ListAdapter<NotePOJO, NotesSearchArrayAdapter.NotesSearchViewHolder> {

    class NotesSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView noteDate;
        TextView noteText;
        TextView notePeopleTagged;
        RoundedImageView thumbnail1;
        RoundedImageView thumbnail2;

        public NotesSearchViewHolder(@NonNull View view) {
            super(view);
            noteDate = view.findViewById(R.id.note_date);
            noteText = view.findViewById(R.id.note_text);
            notePeopleTagged = view.findViewById(R.id.note_people_tagged);
            thumbnail1 = view.findViewById(R.id.person_tagged_thumbnail_1);
            thumbnail2 = view.findViewById(R.id.person_tagged_thumbnail_2);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            NotePOJO note = (NotePOJO) getItem(getBindingAdapterPosition());
            bundle.putInt(Constants.REQUESTING_ACTIVITY, Constants.REQUEST_CODE_ACTIVITY_SEARCH);
            bundle.putString(Constants.NOTE_ID_BUNDLE_ARG, note.getId());
            bundle.putInt(Constants.NOTE_POSITION_BUNDLE_ARG, getBindingAdapterPosition());
            Intent intent = new Intent(Constants.SHOW_NOTE_INTENT);
            intent.putExtras(bundle);
            ((Activity)view.getContext()).startActivityForResult(intent, Constants.REQUEST_CODE_ACTIVITY_SEARCH);
        }
    }

    public NotesSearchArrayAdapter(@NonNull DiffUtil.ItemCallback<NotePOJO> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public NotesSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_notes, parent, false);
        return new NotesSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesSearchViewHolder holder, int position) {
        final NotePOJO note = (NotePOJO) getItem(position);
        DateFormat formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT);
        holder.noteDate.setText(formatter.format(note.getLastUpdatedDate()));
        holder.noteText.setText(note.getText());
        holder.notePeopleTagged.setText(Joiner.on(", ").join(note.getPeopleTaggedNames()));

        List<String> peopleTagged = note.getPeopleTagged();
        if (peopleTagged == null || peopleTagged.size() == 0) {
            holder.notePeopleTagged.setVisibility(View.GONE);
            holder.thumbnail1.setVisibility(View.INVISIBLE);
            holder.thumbnail2.setVisibility(View.INVISIBLE);
        } else {
            holder.notePeopleTagged.setVisibility(View.VISIBLE);
            holder.thumbnail1.setVisibility(View.VISIBLE);
            holder.thumbnail2.setVisibility(View.VISIBLE);

            if (peopleTagged.size() > 0) {
                Picasso.get()
                        .load(CommonUtils.getContactUri(note.getPeopleTagged().get(0)))
                        .placeholder(R.drawable.placeholder_user)
                        .fit()
                        .into(holder.thumbnail1);
            }

            if (peopleTagged.size() > 1) {
                Picasso.get()
                        .load(CommonUtils.getContactUri(note.getPeopleTagged().get(1)))
                        .placeholder(R.drawable.placeholder_user)
                        .fit()
                        .into(holder.thumbnail2);
            } else {
                holder.thumbnail2.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemViewType (int position) {
        return R.layout.list_item_notes;
    }
}

