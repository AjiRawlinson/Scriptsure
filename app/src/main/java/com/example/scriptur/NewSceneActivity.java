package com.example.scriptur;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.scriptur.DataManipulation.Colours;
import com.example.scriptur.Database.Character;
import com.example.scriptur.Database.DBAdaptor;
import com.example.scriptur.Database.Scene;

import org.apache.commons.text.WordUtils;

import java.util.ArrayList;

public class NewSceneActivity extends AppCompatActivity {

    DBAdaptor DBA;
    EditText name;
    ListView characterLV;
    Button colourBtn;
    ArrayList<Integer> characterIDs;
    ArrayList<String> characterNames;
    int playid, order;
    String colour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_scene);
        setTitle("New Scene");
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        DBA = new DBAdaptor(this);
        name = (EditText) findViewById(R.id.etSceneName);
        characterLV = (ListView) findViewById(R.id.lvCharacters);
        characterIDs = new ArrayList<>();
        characterNames = new ArrayList<>();
        Colours colours = new Colours();
        colourBtn = (Button) findViewById(R.id.btn_Scene_Colour);
        colour = colours.randomColour();
        colourBtn.setBackgroundColor(Color.parseColor(colour));

        Intent in = getIntent();
        playid = in.getIntExtra("PLAY_ID", 1);
        order = DBA.getNumberOfScenesInPlay(playid);//Will this still work when scenes are deleted


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
                    Character character = DBA.getCharacterByName(playid, characterLV.getItemAtPosition(i).toString());
                    characterIDs.add(character.getUID());
                }
            }
            }
        });
    }

    public void setSceneColourBtn(View v) {
        Colours colours = new Colours();
        colour = colours.randomColour();
        colourBtn.setBackgroundColor(Color.parseColor(colour));
    }

    public void saveSceneBtn(View v) {
        if(name.getText().toString().length() > 0) {
            ArrayList<Scene> sceneList = DBA.getAllScenesInPlay(playid);
            String nameCapitalized = WordUtils.capitalizeFully(name.getText().toString().trim());
            boolean validName = true;
            for(Scene sceneDB: sceneList) {
                if (sceneDB.getName().equalsIgnoreCase(nameCapitalized)) {
                    validName = false;
                }
            }

            if(validName) {
                if(characterIDs.size() < 1) {
                    Toast.makeText(this, "Scene must contain at least 1 character, Please select a Character.", Toast.LENGTH_LONG).show();
                } else {
                    DBA.insertScene(nameCapitalized, characterIDs, colour, playid, order);

                    Intent in = new Intent(this, SceneCharacterTabbedActivity.class);
                    in.putExtra("PLAY_ID", playid);
                    in.putExtra("TAB_NUM", 1); //decides which tab to open on 0 = characters, 1 = scenes
                    startActivity(in);
                }
            } else {
                name.setText("");
                Toast.makeText(this, "Scene with that name already exists in this play.", Toast.LENGTH_LONG).show();
            }
        } else { Toast.makeText(this, "Please Enter Name of Scene", Toast.LENGTH_LONG).show(); }
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, SceneCharacterTabbedActivity.class);
        in.putExtra("PLAY_ID", playid);
        in.putExtra("TAB_NUM", 1);
        startActivity(in);
    }
}
