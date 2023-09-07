package com.vision.mynotes.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.vision.mynotes.R;
import com.vision.mynotes.adapter.NoteAdapterRecycler;
import com.vision.mynotes.dao.Dao;
import com.vision.mynotes.database.NoteDatabase;
import com.vision.mynotes.databinding.ActivitySearchBinding;
import com.vision.mynotes.enteties.Note;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements NoteAdapterRecycler.OnItemClick {
    private ActivitySearchBinding binding;
    private NoteAdapterRecycler noteAdapterRecycler;
    private List<Note> noteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2 , StaggeredGridLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(layoutManager);

        noteAdapterRecycler = new NoteAdapterRecycler(noteList, this , this);
        binding.recyclerView.setAdapter(noteAdapterRecycler);

        Dao noteDoa ;
        NoteDatabase noteDatabase = NoteDatabase.getNoteDatabase(getApplicationContext());
        noteDoa = noteDatabase.noteDOA();

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                noteList.clear();

                NoteDatabase.databaseWriteExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        String q = "%" + query + "%";
                        List list =  noteDoa.searchNote(q);
                        noteList.addAll(list);

                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });





    }

    @Override
    public void onClick(Note note, int postion) {

    }
}