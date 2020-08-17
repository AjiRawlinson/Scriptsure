package com.example.scriptur;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void libraryBtn(View v) {
        Intent in = new Intent(this, Play_List_Activity.class); //change to library activity
        startActivity(in);
    }

    public void newPlayBtn(View v) {
        Intent in = new Intent(this, SceneCharacterTabbedActivity.class);
        in.putExtra("PLAY_ID", 2); //TODO Delete this Mock Data
        startActivity(in);
    }
}
