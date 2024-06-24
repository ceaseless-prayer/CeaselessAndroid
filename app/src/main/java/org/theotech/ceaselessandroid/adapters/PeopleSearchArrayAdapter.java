package org.theotech.ceaselessandroid.adapters;

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

import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.theotech.ceaselessandroid.R;
import org.theotech.ceaselessandroid.realm.pojo.PersonPOJO;
import org.theotech.ceaselessandroid.util.CommonUtils;
import org.theotech.ceaselessandroid.util.Constants;

public class PeopleSearchArrayAdapter extends ListAdapter<PersonPOJO, PeopleSearchArrayAdapter.PeopleSearchViewHolder> {

    class PeopleSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        IconTextView favorite;
        RoundedImageView personThumbnail;
        TextView personListName;

        public PeopleSearchViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            PersonPOJO person = (PersonPOJO) getItem(getBindingAdapterPosition());
            bundle.putString(Constants.PERSON_ID_BUNDLE_ARG, person.getId());
            Intent intent = new Intent(Constants.SHOW_PERSON_INTENT);
            intent.putExtras(bundle);
            view.getContext().startActivity(intent);
        }
    }

    public PeopleSearchArrayAdapter(@NonNull DiffUtil.ItemCallback<PersonPOJO> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public PeopleSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PeopleSearchViewHolder holder;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_people_active, parent, false);
        holder = new PeopleSearchViewHolder(view);
        holder.favorite = view.findViewById(R.id.person_active_favorite);
        holder.favorite.setVisibility(View.GONE);
        holder.personThumbnail = view.findViewById(R.id.person_active_thumbnail);
        holder.personListName = view.findViewById(R.id.person_active_list_name);
        view.setOnClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleSearchViewHolder holder, int position) {
        final PersonPOJO person = (PersonPOJO) getItem(position);
        // thumbnail picture
        Picasso.get()
                .load(CommonUtils.getContactUri(person.getId()))
                .placeholder(R.drawable.placeholder_user)
                .fit().into(holder.personThumbnail);
        // person name
        holder.personListName.setText(person.getName());
    }

    @Override
    public int getItemViewType (int position) {
        return R.layout.list_item_people_active;
    }
}
