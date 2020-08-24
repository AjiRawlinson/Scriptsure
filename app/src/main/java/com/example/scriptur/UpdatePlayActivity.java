package com.example.scriptur;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.scriptur.DataManipulation.Colours;
import com.example.scriptur.Database.DBAdaptor;
import com.example.scriptur.Database.Play;

import org.apache.commons.text.WordUtils;

import java.util.ArrayList;

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
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

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
        if (title.getText().toString().length() > 0) {
            ArrayList<Play> playList = DBA.getAllPlays();
            String titleCapitalized = WordUtils.capitalizeFully(title.getText().toString().trim());
            boolean validTitle = true;
            for(Play playDB: playList) {
                if (playDB.getTitle().equalsIgnoreCase(titleCapitalized) && playDB.getUID() != play.getUID()) {
                    validTitle = false;
                }
            }

            if(validTitle) {
                    play.setTitle(titleCapitalized);
                    play.setColour(colour);

                    DBA.updatePlay(play);
                    Intent in = new Intent(this, Play_List_Activity.class);
                    startActivity(in);
                } else {
                    title.setText("");
                    Toast.makeText(this, "Play with that title already exists.", Toast.LENGTH_LONG).show();
            }
        } else { Toast.makeText(this, "Please Enter Play Title", Toast.LENGTH_LONG).show(); }
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, Play_List_Activity.class);
        startActivity(in);
    }
}
