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
import android.widget.Toast;

import com.example.scriptur.Database.Character;
import com.example.scriptur.Database.DBAdaptor;
import com.example.scriptur.Database.Scene;

import org.apache.commons.text.WordUtils;

import java.util.ArrayList;

public class UpdateSceneActivity extends AppCompatActivity {

    DBAdaptor DBA;
    EditText name;
    ListView characterLV;
    Button colourBtn;
    ArrayList<Integer> characterIDs;
    ArrayList<String> characterNames;
    String colour;
    Scene scene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_scene);
        setTitle("Update Scene");

        DBA = new DBAdaptor(this);
        name = (EditText) findViewById(R.id.etUpdateSceneName);
        characterLV = (ListView) findViewById(R.id.lvUpdateCharacters);
        characterLV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        colourBtn = (Button) findViewById(R.id.btn_Update_Scene_Colour);
        characterIDs = new ArrayList<>();
        characterNames = new ArrayList<>();

        Intent in = getIntent();
        int sceneID = in.getIntExtra("SCENE_ID", 1);
        scene = DBA.getSceneByID(sceneID);
        colour = scene.getColour();
        colourBtn.setBackgroundColor(Color.parseColor(colour));
        name.setText(scene.getName());

        ArrayList<Character> characterList = DBA.getAllCharactersInPlay(scene.getPlay().getUID());
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
                        Character character = DBA.getCharacterByName(scene.getPlay().getUID(), characterLV.getItemAtPosition(i).toString());//maybe an easier way to do this??
                        characterIDs.add(character.getUID());
                    }
                }
            }
        });


        //I'm sure there is a better way to do, but right now my brain is frazzled and it's the only way I know, might look again later
        ArrayList<Character> characterListInScene = DBA.getCharactersBySceneID(scene.getUID());
        ArrayList<String> characterNamesInScene = new ArrayList<>();
        for(int i = 0; i < characterListInScene.size(); i++) {
            characterNamesInScene.add(characterListInScene.get(i).getName());
            characterIDs.add(characterListInScene.get(i).getUID());
        }
        for(int i = 0; i < characterNames.size(); i++) {
            if(characterNamesInScene.contains(characterNames.get(i))) {
                characterLV.setItemChecked(i, true);
            }
        }
    }

    public void updateSceneColourBtn(View v) {
        Colours colours = new Colours();
        colour = colours.randomColour();
        colourBtn.setBackgroundColor(Color.parseColor(colour));
    }

    public void updateSceneBtn(View v) {
        ArrayList<Scene> sceneList = DBA.getAllScenesInPlay(scene.getPlay().getUID());
        String nameCapitalized = WordUtils.capitalizeFully(name.getText().toString().trim());
        boolean validName = true;
        for(Scene sceneDB: sceneList) {
            if (sceneDB.getName().equalsIgnoreCase(nameCapitalized) && sceneDB.getUID() != scene.getUID()) {
                validName = false;
            }
        }

        if(validName) {
            if (characterIDs.size() < 1) {
                Toast.makeText(this, "Scene must contain at least 1 character, Please select a Character.", Toast.LENGTH_LONG).show();
            } else {
                scene.setName(nameCapitalized);
                scene.setColour(colour);
                DBA.updateScene(scene, characterIDs);

                Intent in = new Intent(this, SceneCharacterTabbedActivity.class);
                in.putExtra("PLAY_ID", scene.getPlay().getUID());
                in.putExtra("TAB_NUM", 1); //decides which tab to open on 0 = characters, 1 = scenes
                startActivity(in);
            }
        } else {
            name.setText("");
            Toast.makeText(this, "Scene with that name already exists in this play.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, SceneCharacterTabbedActivity.class);
        in.putExtra("PLAY_ID", scene.getPlay().getUID());
        in.putExtra("TAB_NUM", 1);
        startActivity(in);
    }
}
