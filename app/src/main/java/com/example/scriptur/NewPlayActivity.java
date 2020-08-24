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
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

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
        if (title.getText().toString().length() > 0) {
            ArrayList<Play> playList = DBA.getAllPlays();
            String titleCapitalized = WordUtils.capitalizeFully(title.getText().toString().trim());
            boolean validTitle = true;
            for(Play play: playList) {
                if (play.getTitle().equalsIgnoreCase(titleCapitalized)) {
                     validTitle = false;
                }
            }

            if(validTitle) {
                DBA.insertPlay(titleCapitalized, colour);
                Play play = DBA.getPlayByTitle(title.getText().toString());

                Intent in = new Intent(this, NewCharacterActivity.class);
                in.putExtra("PLAY_ID", play.getUID());
                startActivity(in);
            } else {
                title.setText("");
                Toast.makeText(this, "Play with title already Exists.", Toast.LENGTH_LONG).show();
            }
        } else { Toast.makeText(this, "Please Enter Play Title", Toast.LENGTH_LONG).show(); }
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, Play_List_Activity.class);
        startActivity(in);
    }

}
