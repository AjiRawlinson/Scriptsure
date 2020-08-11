package com.example.scriptur;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.scriptur.Database.DBAdaptor;
import com.example.scriptur.Database.Play;

public class NewPlayActivity extends AppCompatActivity {

    EditText title;
    Button colourBtn;
    DBAdaptor DBA;
    String colour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_play);
        setTitle("New Play");

        title = (EditText) findViewById(R.id.et_title);
        colourBtn = (Button) findViewById(R.id.btn_Play_Colour);

        Colours colours = new Colours();
        colour = colours.randomColour();
        colourBtn.setBackgroundColor(Color.parseColor(colour));
        DBA = new DBAdaptor(this);

    }

    public void setPlayColourBtn(View v) {
        Colours colours = new Colours();
        colour = colours.randomColour();
        colourBtn.setBackgroundColor(Color.parseColor(colour));
    }


    public void saveTitleBtn(View v) {
    //TODO Make sure titles are unique
        DBA.insertPlay(title.getText().toString().trim(), colour);
        Play play = DBA.getPlayByTitle(title.getText().toString());

        Intent in = new Intent(this, NewCharacterActivity.class);
        in.putExtra("PLAY_ID", play.getUID());
        startActivity(in);
    }




}
