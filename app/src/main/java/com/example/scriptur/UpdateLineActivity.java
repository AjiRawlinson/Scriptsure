package com.example.scriptur;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
        sceneID = line.getScene().getUID();
        characterID = line.getCharacter().getUID();
        etDialog.setText(line.getDialog());
        characterList = DBA.getCharactersBySceneID(sceneID);
        String[] characterNamesList = new String[characterList.size()];
        for(int i = 0; i < characterNamesList.length; i++) {
            characterNamesList[i] = characterList.get(i).getName();
        }
        Log.v("TAG", "Before.");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateLineActivity.this, android.R.layout.simple_spinner_dropdown_item, characterNamesList);
        characterSpinner.setAdapter(adapter);
        characterSpinner.setOnItemSelectedListener(this);

        Log.v("TAG", "After.");
        Log.v("TAG", characterNamesList[0]);
        int spinnerIndex = -1;
        for(int i = 0; i < characterNamesList.length; i++) {
            if(line.getCharacter().getName().equalsIgnoreCase(characterNamesList[i])) { spinnerIndex = i; }
        }
        Toast.makeText(UpdateLineActivity.this, "Index: " + spinnerIndex, Toast.LENGTH_LONG);
        characterSpinner.setSelection(spinnerIndex);
    }

    public void updateLineBtn(View v) {
        String dialogCapitalized = etDialog.getText().toString().trim().substring(0,1).toUpperCase() + etDialog.getText().toString().trim().substring(1);
        line.setCharacter(DBA.getCharacterByID(characterID));
        line.setDialog(dialogCapitalized);
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

    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, Line_List_Activity.class);
        in.putExtra("SCENE_ID", line.getScene().getUID());
        startActivity(in);
    }
}
