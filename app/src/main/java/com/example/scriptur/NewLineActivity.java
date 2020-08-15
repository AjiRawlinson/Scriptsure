package com.example.scriptur;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scriptur.Database.Character;
import com.example.scriptur.Database.DBAdaptor;
import com.example.scriptur.Database.Line;

import java.util.ArrayList;

public class NewLineActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ArrayList<Character> characterList; //need to be global?
    DBAdaptor DBA;
    EditText etDialog;
    Spinner characterSpinner;
    int sceneID, characterID, order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_line);
        setTitle("New Line");

        DBA = new DBAdaptor(this);
        etDialog = (EditText) findViewById(R.id.etLineDialog);
        characterSpinner = (Spinner) findViewById(R.id.SpinnerCharacters);
        characterID = 0;

        Intent in = getIntent();
        sceneID = in.getIntExtra("SCENE_ID", 1);
        order = DBA.getNumberOfLinesInScene(sceneID);
        characterList = DBA.getCharactersBySceneID(sceneID);
        String[] characterNamesList = new String[characterList.size()];
        for(int i = 0; i < characterNamesList.length; i++) {
            characterNamesList[i] = characterList.get(i).getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewLineActivity.this, android.R.layout.simple_spinner_dropdown_item, characterNamesList);
        characterSpinner.setAdapter(adapter);
        characterSpinner.setOnItemSelectedListener(this);
    }

    public void saveLineBtn(View v) {
        DBA.insertLine(characterID, etDialog.getText().toString(), sceneID, order);

        Intent in = new Intent(this, Line_List_Activity.class);
        in.putExtra("SCENE_ID", sceneID);
        startActivity(in);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        characterID = characterList.get(position).getUID();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        characterID = characterList.get(0).getUID();
    }

}
