package com.example.scriptur;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scriptur.Database.DBAdaptor;

public class NewCharacterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    DBAdaptor DBA;
    TextView playTitle;
    EditText name;
    Spinner genderSpinner;
    Switch userRoleSwitch;
    private static final String[] genderList = {"Unisex", "Male", "Female"};
    String gender;
    boolean userRole;
    int playid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_character);
        setTitle("New Character");

        DBA = new DBAdaptor(this);
        name = (EditText) findViewById(R.id.etCharacterName);
        genderSpinner = (Spinner) findViewById(R.id.genderSpinner);
        userRoleSwitch = (Switch) findViewById(R.id.userRoleSwitch);
        gender = "unisex";

        Intent in = getIntent();
        playid = in.getIntExtra("PLAY_ID", 0);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewCharacterActivity.this, android.R.layout.simple_spinner_dropdown_item, genderList);
        genderSpinner.setAdapter(adapter);
        genderSpinner.setOnItemSelectedListener(this);
    }



    public void saveCharacterBtn(View v) {
        if(userRoleSwitch.isChecked()) { userRole = true; }
        else{ userRole = false; }
        DBA.insertCharacter(name.getText().toString(), gender, userRole, playid);

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
        gender = "unisex";
    }
}
