package com.elsawaf.thebrilliant.noteapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddNoteActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText descEditText;
    private String title;
    private String desc;
    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        titleEditText = (EditText) findViewById(R.id.addnote_title);
        descEditText = (EditText) findViewById(R.id.addnote_desc);

    }


    public void saveButton(View view) {
        title = titleEditText.getText().toString();
        desc = descEditText.getText().toString();

        if (title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Can't add empty note", Toast.LENGTH_SHORT).show();
            return;
        }

        time = System.currentTimeMillis();
        Note note = new Note(title, desc, time);
        note.save();

        finish();

    }
}
