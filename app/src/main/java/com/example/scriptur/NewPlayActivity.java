package com.example.scriptur;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.scriptur.Database.DBAdaptor;
import com.example.scriptur.Database.Play;

public class NewPlayActivity extends AppCompatActivity {

    EditText title;
    DBAdaptor DBA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_play);

        title = (EditText) findViewById(R.id.et_title);
        DBA = new DBAdaptor(this);
    }

        public void saveTitleBtn(View v) {
        //TODO Make sure titles are unique
            DBA.insertPlay(title.getText().toString().trim());
            Play play = DBA.getPlayByTitle(title.getText().toString());

            Intent in = new Intent(this, NewCharacterActivity.class);
            in.putExtra("PLAY_ID", play.getUID());
            startActivity(in);
        }




}
