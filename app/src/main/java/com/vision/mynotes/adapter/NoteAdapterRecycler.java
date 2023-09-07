package com.vision.mynotes.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.vision.mynotes.R;
import com.vision.mynotes.dao.Dao;
import com.vision.mynotes.database.NoteDatabase;
import com.vision.mynotes.enteties.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapterRecycler extends RecyclerView.Adapter<NoteAdapterRecycler.ViewHolder> {
    private List<Note> noteList = new ArrayList<>();
    private Context context;
    private OnItemClick onItemClick;

    public NoteAdapterRecycler(List<Note> noteList, Context context, OnItemClick onItemClick) {
        this.noteList = noteList;
        this.context = context;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.row_note_recycler,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(noteList.get(position));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                openDialog(position, noteList.get(position));
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.onClick(noteList.get(position) , position);
            }
        });
    }

    private void openDialog(int position, Note note) {
        Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_note_dialog);
        MaterialButton saveButton = dialog.findViewById(R.id.btnConfirm);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dao noteDoa ;
                NoteDatabase noteDatabase = NoteDatabase.getNoteDatabase(context);
                noteDoa = noteDatabase.noteDOA();

                NoteDatabase.databaseWriteExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        noteDoa.deleteNote(note);
                        noteList.remove(position);
                    }
                });
                notifyItemRemoved(position);
                notifyItemRangeRemoved(0, noteList.size());
            }
        });

        MaterialButton cancelButton = dialog.findViewById(R.id.btnDiscard);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();            }

        });

        dialog.getWindow().setBackgroundDrawable(null);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDateTime;
        LinearLayout layoutNote;
        RoundedImageView imageNote;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            layoutNote = itemView.findViewById(R.id.layoutNote);
            imageNote = itemView.findViewById(R.id.imageNote);
        }

        public void bind(Note note) {
            textTitle.setText(note.getTitle());
            textDateTime.setText(note.getDate());
            if (note.getImage()!=null &&  !note.getImage().isEmpty()){
                Uri uri = Uri.parse(note.getImage());
                imageNote.setVisibility(View.VISIBLE);
                Picasso.get().load(uri).into(imageNote);
            }else {
                imageNote.setVisibility(View.GONE);
            }

            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();
            gradientDrawable.setColor(Color.parseColor(note.getColor()));

        }
    }

    public interface OnItemClick{
        void onClick(Note note , int postion);
    }
}
