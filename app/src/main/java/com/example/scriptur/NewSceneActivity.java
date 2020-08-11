package com.example.scriptur;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.scriptur.Database.Character;
import com.example.scriptur.Database.DBAdaptor;

import java.util.ArrayList;
import java.util.List;

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
        if(characterIDs.size() < 1) {
            Toast.makeText(this, "Scene must contain at least 1 character, Please select a Character.", Toast.LENGTH_LONG).show();
        } else {
            DBA.insertScene(name.getText().toString(), characterIDs, colour, playid, order);

            Intent in = new Intent(this, Scene_List_Activity.class);
            in.putExtra("PLAY_ID", playid);
            startActivity(in);
        }
    }
}
