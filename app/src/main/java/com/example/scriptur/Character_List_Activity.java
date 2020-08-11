package com.example.scriptur;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scriptur.Database.Character;
import com.example.scriptur.Database.DBAdaptor;
import com.example.scriptur.RecyclerViewAdaptors.RVCharacterAdaptor;

import java.util.ArrayList;

public class Character_List_Activity extends AppCompatActivity {

    RecyclerView rvCharacter;
    ArrayList<Character> characterList;
    DBAdaptor DBA;
    RVCharacterAdaptor RVACharacter;
    TextView playTitle;
    int playid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_list);

        DBA = new DBAdaptor(this);
        Intent in = getIntent();
        playid = in.getIntExtra("PLAY_ID", 0);
        setTitle(DBA.getPlayByID(playid).getTitle());

        rvCharacter = (RecyclerView) findViewById(R.id.RVCharacter);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rvCharacter.setLayoutManager(llm);
        DBAdaptor DBA = new DBAdaptor(this);
        characterList = DBA.getAllCharactersInPlay(playid);
        RVACharacter = new RVCharacterAdaptor(this, characterList);
        rvCharacter.setAdapter(RVACharacter);
    }

    public void addCharacterBtn(View v) {
        Intent in = new Intent(this, NewCharacterActivity.class);
        in.putExtra("PLAY_ID", playid);
        startActivity(in);
    }

    public void viewScenesBtn(View v) {
        Intent in = new Intent(this, Scene_List_Activity.class);
        in.putExtra("PLAY_ID", playid);
        startActivity(in);
    }
}
