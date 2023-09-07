package com.vision.mynotes.ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.vision.mynotes.R;
import com.vision.mynotes.adapter.NoteAdapterRecycler;
import com.vision.mynotes.dao.Dao;
import com.vision.mynotes.database.NoteDatabase;
import com.vision.mynotes.databinding.ActivityHomeBinding;
import com.vision.mynotes.enteties.Note;
import com.vision.mynotes.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements NoteAdapterRecycler.OnItemClick {
    private ActivityHomeBinding binding;
    private NoteAdapterRecycler noteAdapterRecycler;
    private List<Note> noteList = new ArrayList<>();
    private boolean isNightMode = false;
    private boolean isArabic = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        setUp();

        getAllData();


    }

    private void setUp() {

        SharedPreferences sharedPreferences = getSharedPreferences("MyNoteAppPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        isNightMode = sharedPreferences.getBoolean("IS_NIGHT", false);
        isArabic = sharedPreferences.getBoolean("IS_ARABIC", false);

        if (isNightMode) {
            binding.btnMode.setImageDrawable(getDrawable(R.drawable.baseline_wb_sunny_24));
        } else {
            binding.btnMode.setImageDrawable(getDrawable(R.drawable.baseline_nightlight_round_24));
        }

        binding.btnLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isArabic){
                    setLocale("en");
                    isArabic = false;
                    editor.putBoolean("IS_ARABIC", false);
                }else {
                    setLocale("ar");
                    isArabic = true;
                    editor.putBoolean("IS_ARABIC", true);
                }
                editor.apply();
                finish();
                startActivity(getIntent());

            }
        });
        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, SearchActivity.class));
            }
        });

        binding.btnMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNightMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("IS_NIGHT", false);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("IS_NIGHT", true);

                }

                editor.apply();

            }
        });


        binding.fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddNoteActivity.class);
                launcher.launch(intent);
            }
        });
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent intent = result.getData();

                String status = intent.getExtras().getString(Constants.NOTE_STATUS_KEY);
                Dao noteDoa;
                NoteDatabase noteDatabase = NoteDatabase.getNoteDatabase(getApplicationContext());
                noteDoa = noteDatabase.noteDOA();
                if (!status.equals(Constants.NOTE_STATUS_KEY)) {
                    NoteDatabase.databaseWriteExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            List<Note> list = noteDoa.getAllNotes();
                            Note lastNote = list.get(0);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!list.isEmpty()) {
                                        binding.linerHolder.setVisibility(View.GONE);

                                        noteList.add(0, lastNote);
                                        noteAdapterRecycler.notifyItemInserted(0);
                                        binding.recyclerView.smoothScrollToPosition(0);

                                    }
                                }
                            });
                        }
                    });
                } else {
                    int pos = intent.getExtras().getInt(Constants.NOTE_POSITION_KEY, 0);
                    int id = intent.getExtras().getInt(Constants.NOTE_ID_KEY, 0);
                    noteList.remove(pos);
                    NoteDatabase.databaseWriteExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            Note note = noteDoa.getNote(id);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    noteList.add(pos, note);
                                    noteAdapterRecycler.notifyItemChanged(pos);
                                }
                            });
                        }
                    });

                }
            }
        }
    });

    private void getAllData() {
        noteList.clear();

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(layoutManager);

        noteAdapterRecycler = new NoteAdapterRecycler(noteList, this, this);
        binding.recyclerView.setAdapter(noteAdapterRecycler);
        Dao noteDoa;
        NoteDatabase noteDatabase = NoteDatabase.getNoteDatabase(getApplicationContext());
        noteDoa = noteDatabase.noteDOA();
        NoteDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<Note> list = noteDoa.getAllNotes();
                noteList.addAll(list);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!list.isEmpty()) {
                            binding.linerHolder.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onClick(Note note, int postion) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        intent.putExtra(Constants.NOTE_KEY, note);
        intent.putExtra(Constants.NOTE_POSITION_KEY, postion);
        launcher.launch(intent);

    }
    public void setLocale(String langCode) {
        Resources resources = this.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

    }

}