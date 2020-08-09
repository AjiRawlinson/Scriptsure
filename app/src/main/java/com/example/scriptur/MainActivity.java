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
        Intent in = new Intent(this, NewPlayActivity.class); //change to library activity
        startActivity(in);
    }

    public void newPlayBtn(View v) {
        Intent in = new Intent(this, NewPlayActivity.class);
        startActivity(in);
    }
}
