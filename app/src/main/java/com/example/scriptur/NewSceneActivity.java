package com.example.scriptur;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scriptur.Database.Character;
import com.example.scriptur.Database.DBAdaptor;

import java.util.ArrayList;
import java.util.List;

public class NewSceneActivity extends AppCompatActivity {

    DBAdaptor DBA;
    TextView playTitle;
    EditText name;
    ListView characterLV;
    ArrayList<Integer> characterIDs;
    ArrayList<String> characterNames;
    int playid, order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_scene);
        setTitle("New Scene");

        DBA = new DBAdaptor(this);
        name = (EditText) findViewById(R.id.etSceneName);
        characterLV = (ListView) findViewById(R.id.lvCharacters);
        characterIDs = new ArrayList<>();
        characterNames = new ArrayList<>();
        order = DBA.getNumberOfScenesInPlay(playid);

        Intent in = getIntent();
        playid = in.getIntExtra("PLAY_ID", 1);

        ArrayList<Character> characterList = DBA.getAllCharactersInPlay(playid);
        for(Character charater: characterList) {
            characterNames.add(charater.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, characterNames);
        characterLV.setAdapter(adapter);
        characterLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SparseBooleanArray charactersChecker = characterLV.getCheckedItemPositions();
                characterIDs.clear();

                for(int i = 0; i<characterNames.size(); i++) {
                    if(charactersChecker.get(i)) {
                        Toast.makeText(getApplicationContext(), "position: " + position + " i value: " + i, Toast.LENGTH_LONG).show();

//                        int key = charactersChecker.keyAt(position);//not sure if this is needed, but in tutorial
                        Character character = DBA.getCharacterByName(playid, characterLV.getItemAtPosition(i).toString());
                        characterIDs.add(character.getUID());
                    }
                }
            }
        });
    }

    public void saveSceneBtn(View v) {
        if(characterIDs.size() < 1) {
            Toast.makeText(this, "Scene must contain at least 1 character, Please select a Character.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Characters" + characterIDs + "Into play: " + playid, Toast.LENGTH_LONG).show();

            DBA.insertScene(name.getText().toString(), characterIDs, playid, order);

            Intent in = new Intent(this, Scene_List_Activity.class);
            in.putExtra("PLAY_ID", playid);
            startActivity(in);
        }
    }
}
