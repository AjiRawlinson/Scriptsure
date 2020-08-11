package com.example.scriptur;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.scriptur.Database.DBAdaptor;
import com.example.scriptur.Database.Play;

public class UpdatePlayActivity extends AppCompatActivity {

    EditText title;
    Button colourBtn;
    DBAdaptor DBA;
    String colour;
    Play play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_play);
        setTitle("Update Play");

        title = (EditText) findViewById(R.id.et_Update_title);
        colourBtn = (Button) findViewById(R.id.btn_Update_Play_Colour);
        DBA = new DBAdaptor(this);

        Intent in = getIntent();
        int id = in.getIntExtra("PLAY_ID", 1);
        play = DBA.getPlayByID(id);

        colour = play.getColour();
        colourBtn.setBackgroundColor(Color.parseColor(colour));
        title.setText(play.getTitle());
    }

    public void updatePlayColourBtn(View v) {
        Colours colours = new Colours();
        colour = colours.randomColour();
        colourBtn.setBackgroundColor(Color.parseColor(colour));
    }

    public void updateTitleBtn(View v) {
        //TODO Make sure titles are unique
        play.setTitle(title.getText().toString());
        play.setColour(colour);


        DBA.updatePlay(play);
        Intent in = new Intent(this, Play_List_Activity.class);
        startActivity(in);
    }

}
