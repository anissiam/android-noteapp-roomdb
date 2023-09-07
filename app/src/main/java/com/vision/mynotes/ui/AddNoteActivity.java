package com.vision.mynotes.ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;
import com.vision.mynotes.R;
import com.vision.mynotes.dao.Dao;
import com.vision.mynotes.database.NoteDatabase;
import com.vision.mynotes.databinding.ActivityAddNoteBinding;
import com.vision.mynotes.enteties.Note;
import com.vision.mynotes.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddNoteActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityAddNoteBinding binding;
    private static int PERSMISSION_REQUEST_CODE = 1;
    private String imgPath = "";
    private String color = "#B89EFD";
    private ImageView selectedColor ;
    private Note oldNote;
    /**
     * 0 mean new note
     * -1 update note
     */
    private int tag = 0;
    private int pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUp();
        Intent intent = getIntent();
        Note note =(Note) intent.getSerializableExtra(Constants.NOTE_KEY);
        if (note!=null){
            pos = intent.getIntExtra(Constants.NOTE_POSITION_KEY, 0);
            tag = -1;
            oldNote = note;
            getNoteData(note);
        }
       /* AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Save changes ?");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(AddNoteActivity.this, "Hi", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialog alertDialog = builder.show();
                alertDialog.dismiss();

            }
        });
        builder.create().show();*/

    }

    private void getNoteData(Note note) {
        binding.tvDate.setText(note.getDate());
        binding.edtTitle.setText(note.getTitle());
        binding.edtText.setText(note.getText());
        if (!note.getImage().isEmpty()){
            imgPath = note.getImage().toString();

            binding.imageNote.setVisibility(View.VISIBLE);
            binding.imageRemoveImage.setVisibility(View.VISIBLE);
            Picasso.get().load(note.getImage()).into(binding.imageNote);

        }

        switch (note.getColor()) {
            case "#1C5879":
                color = "#1C5879";
                binding.imgBlue.setImageResource(R.drawable.round_done_24);
                selectedColor.setImageResource(0);
                selectedColor = binding.imgBlue;
                break;
            case "#B89EFD":
                color = "#B89EFD";
                binding.imgPurple.setImageResource(R.drawable.round_done_24);
                selectedColor.setImageResource(0);
                selectedColor = binding.imgPurple;
                break;
            case "#FCB6B4":
                color = "#FCB6B4";
                binding.imgPink.setImageResource(R.drawable.round_done_24);
                selectedColor.setImageResource(0);
                selectedColor = binding.imgPink;
                break;
            case "#FCE0B5":
                color = "#FCE0B5";
                binding.imgOrange.setImageResource(R.drawable.round_done_24);
                selectedColor.setImageResource(0);
                selectedColor = binding.imgOrange;
                break;
        }
    }

    private void setUp() {
        binding.tvDate.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a",
                Locale.getDefault()).format(new Date()));

        selectedColor = binding.imgPurple;
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String persmission[] = {Manifest.permission.READ_EXTERNAL_STORAGE};
        binding.fabAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext() ,
                        Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(AddNoteActivity.this , persmission,PERSMISSION_REQUEST_CODE );

                }else {
                    pickImage();
                }
            }
        });


        binding.framePurple.setOnClickListener(this);
        binding.frameBlue.setOnClickListener(this);
        binding.framePink.setOnClickListener(this);
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
        binding.imageRemoveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgPath = "";
                binding.imageNote.setImageResource(0);
                binding.imageNote.setVisibility(View.GONE);
                binding.imageRemoveImage.setVisibility(View.GONE);

            }
        });
    }

    private void openDialog() {
        if (binding.edtTitle.getText().toString().isEmpty()){
            Toast.makeText(this, "Please add title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (binding.edtText.getText().toString().isEmpty()){
            Toast.makeText(this, "Please add text", Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.save_note_dialog);
        MaterialButton saveButton = dialog.findViewById(R.id.btnSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote(dialog);
            }
        });

        MaterialButton cancelButton = dialog.findViewById(R.id.btnDiscard);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                dialog.dismiss();
                finish();
            }
        });

        dialog.getWindow().setBackgroundDrawable(null);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();




    }

    public void saveNote(Dialog dialog){

        Note note = new Note();
        note.setTitle(binding.edtTitle.getText().toString());
        note.setText(binding.edtText.getText().toString());
        note.setDate(binding.tvDate.getText().toString());
        note.setColor(color);
        note.setImage(imgPath);

        Dao noteDoa ;
        NoteDatabase noteDatabase = NoteDatabase.getNoteDatabase(getApplicationContext());
        noteDoa = noteDatabase.noteDOA();
        if (tag==0){
            NoteDatabase.databaseWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    noteDoa.insertNote(note);
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    dialog.dismiss();
                    finish();
                }
            });
        }else {
            oldNote.setTitle(binding.edtTitle.getText().toString());
            oldNote.setText(binding.edtText.getText().toString());
            oldNote.setDate(binding.tvDate.getText().toString());
            oldNote.setColor(color);
            oldNote.setImage(imgPath);

            NoteDatabase.databaseWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    noteDoa.updateNote(oldNote);
                    Intent intent = new Intent();
                    intent.putExtra(Constants.NOTE_STATUS_KEY, Constants.NOTE_STATUS_KEY);
                    intent.putExtra(Constants.NOTE_POSITION_KEY, pos);
                    intent.putExtra(Constants.NOTE_ID_KEY, oldNote.getId());
                    setResult(RESULT_OK, intent);
                    dialog.dismiss();
                    finish();
                }
            });
        }

    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        resultLauncher.launch(intent);


/*
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);*/
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==binding.framePurple.getId()){
            color = "#B89EFD";
            selectedColor.setImageResource(0);
            binding.imgPurple.setImageResource(R.drawable.round_done_24);
            selectedColor = binding.imgPurple;
        }else if (view.getId()==binding.framePink.getId()){
            color = "#FCB6B4";
            selectedColor.setImageResource(0);
            binding.imgPink.setImageResource(R.drawable.round_done_24);
            selectedColor = binding.imgPink;

        }else if (view.getId()==binding.frameBlue.getId()){
            color = "#1C5879";
            selectedColor.setImageResource(0);
            binding.imgBlue.setImageResource(R.drawable.round_done_24);
            selectedColor = binding.imgBlue;

        }else if (view.getId()==binding.frameOrange.getId()){
            color = "#FCE0B5";
            selectedColor.setImageResource(0);
            binding.imgOrange.setImageResource(R.drawable.round_done_24);
            selectedColor = binding.imgOrange;
        }

    }
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()==RESULT_OK){
                        Intent intent = result.getData();
                        Uri uri = intent.getData();
                        imgPath = uri.toString();

                        binding.imageNote.setVisibility(View.VISIBLE);
                        binding.imageRemoveImage.setVisibility(View.VISIBLE);

                        Picasso.get().load(uri).into(binding.imageNote);

                    }
                }
            });
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERSMISSION_REQUEST_CODE && grantResults.length > 0) {
            if (permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE) {
                 pickImage();
            }
        }else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }

    }


}