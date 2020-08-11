package com.example.scriptur;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.scriptur.Database.Character;
import com.example.scriptur.Database.DBAdaptor;

public class UpdateCharacterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    DBAdaptor DBA;
    EditText name;
    Spinner genderSpinner;
    Switch userRoleSwitch;
    Button colourBtn;
    private static final String[] genderList = {"Unisex", "Male", "Female"};
    String gender, colour;
    boolean userRole;
    int playid;
    Character character;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_character);
        setTitle("Update Character");

        DBA = new DBAdaptor(this);
        name = (EditText) findViewById(R.id.etUpdateCharacterName);
        genderSpinner = (Spinner) findViewById(R.id.spinnerUpdateGender);
        userRoleSwitch = (Switch) findViewById(R.id.switchUpdateUserRole);
        colourBtn = (Button) findViewById(R.id.btn_Update_Character_Colour);

        Intent in = getIntent();
        int characterID = in.getIntExtra("CHARACTER_ID", 1);

        character = DBA.getCharacterByID(characterID);
        name.setText(character.getName());
        colour = character.getColour();
        gender = character.getGender();
        colourBtn.setBackgroundColor(Color.parseColor(colour));
        playid = character.getPlay().getUID();
        if(character.isUserPart()) {userRoleSwitch.setChecked(true); }
        else { userRoleSwitch.setChecked(false); }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateCharacterActivity.this, android.R.layout.simple_spinner_dropdown_item, genderList);
        genderSpinner.setAdapter(adapter);
        genderSpinner.setOnItemSelectedListener(this);
        int spinnerIndex = 0;
        for(int i = 0; i < genderList.length; i++) {
            if(character.getGender().equalsIgnoreCase(genderList[i])) { spinnerIndex = i; }
        }
        genderSpinner.setSelection(spinnerIndex);


    }

    public void updateCharacterColourBtn(View v) {
        Colours colours = new Colours();
        colour = colours.randomColour();
        colourBtn.setBackgroundColor(Color.parseColor(colour));
    }

    public void updateCharacterBtn(View v) {
        if(userRoleSwitch.isChecked()) { userRole = true; }
        else{ userRole = false; }
        character.setName(name.getText().toString());
        character.setColour(colour);
        character.setGender(gender);
        character.setUserPart(userRole);
        DBA.updateCharacter(character);

        Intent in = new Intent(this, Character_List_Activity.class);
        in.putExtra("PLAY_ID", playid);
        startActivity(in);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position) {
            case 0:
                gender = genderList[0].toLowerCase();
                break;
            case 1:
                gender = genderList[1].toLowerCase();
                break;
            case 2:
                gender = genderList[2].toLowerCase();
                break;
            default:
                gender = "unisex";
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        gender = character.getGender();
    }
}

