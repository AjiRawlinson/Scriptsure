package com.example.scriptur;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.scriptur.Database.Character;
import com.example.scriptur.Database.DBAdaptor;
import com.example.scriptur.Database.Line;

import java.util.ArrayList;

public class UpdateLineActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ArrayList<Character> characterList; //need to be global?
    DBAdaptor DBA;
    EditText etDialog;
    Spinner characterSpinner;
    int sceneID, characterID, order;
    Line line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_line);
        setTitle("Update Line");

        DBA = new DBAdaptor(this);
        etDialog = (EditText) findViewById(R.id.etUpdateLineDialog);
        characterSpinner = (Spinner) findViewById(R.id.spinnerUpdateCharacters);

        Intent in = getIntent();
        int lineID = in.getIntExtra("LINE_ID", 1);
        line = DBA.getLineByID(lineID);
        etDialog.setText(line.getDialog());
        characterList = DBA.getCharactersBySceneID(sceneID);
        String[] characterNamesList = new String[characterList.size()];
        for(int i = 0; i < characterNamesList.length; i++) {
            characterNamesList[i] = characterList.get(i).getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateLineActivity.this, android.R.layout.simple_spinner_dropdown_item, characterNamesList);
        characterSpinner.setAdapter(adapter);
        characterSpinner.setOnItemSelectedListener(this);
        int spinnerIndex = 0;
        for(int i = 0; i < characterNamesList.length; i++) {
            if(line.getCharacter().getName().equalsIgnoreCase(characterNamesList[i])) { spinnerIndex = i; }
        }
        characterSpinner.setSelection(spinnerIndex);
    }

    public void updateLineBtn(View v) {
        line.setCharacter(DBA.getCharacterByID(characterID));
        line.setDialog(etDialog.getText().toString());
        DBA.updateLine(line);

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
        characterID = line.getCharacter().getUID();
    }
}
