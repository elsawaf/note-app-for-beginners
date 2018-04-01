package com.elsawaf.thebrilliant.noteapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by The Brilliant on 25/03/2018.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    private SharedPreferences sharedPreferences;
    private Context context;
    private List<Note> noteList;
    private int textSize;

    public NotesAdapter(Context context, List<Note> noteList) {
        this.context = context;
        this.noteList = noteList;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String textSizeString = sharedPreferences.getString(context.getString(R.string.pref_text_size), "15");
        textSize = Integer.parseInt(textSizeString);

    }

    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
    }

    @Override
    public NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.note_item_card_view, parent, false);
        NotesViewHolder viewHolder = new NotesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NotesViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.titleTV.setText(note.getTitle());
        holder.descTV.setText(note.getDesc());
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    class NotesViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener{

        TextView titleTV;
        TextView descTV;
        public NotesViewHolder(View itemView) {
            super(itemView);

            titleTV = (TextView) itemView.findViewById(R.id.note_item_title);
            descTV = (TextView) itemView.findViewById(R.id.note_item_desc);

            descTV.setTextSize(textSize);
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(context, "Item Clicked: pos=" + getAdapterPosition(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            String textSizeString = sharedPreferences.getString(context.getString(R.string.pref_text_size), "15");
            textSize = Integer.parseInt(textSizeString);
            descTV.setTextSize(textSize);
        }

        public void clearResources(){
            if (sharedPreferences!=null) {
                sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
            }
        }
    }
}
