package com.example.scriptur;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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
    private static final String[] avatarNameArray = {"Female 1", "Female 2", "Female 3", "Female 4", "Female 5", "Female 6",
            "Male 1", "Male 2", "Male 3", "Male 4", "Male 5", "Male 6"};
    private static final Integer[] avatarImageArray = {R.drawable.female1, R.drawable.female2, R.drawable.female3,
            R.drawable.female4, R.drawable.female5, R.drawable.female6,
            R.drawable.male1, R.drawable.male2, R.drawable.male3,
            R.drawable.male4, R.drawable.male5, R.drawable.male6};
    int sceneID, characterID, order;
    Line line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_line);
        setTitle("Update Line");
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

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

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateLineActivity.this, android.R.layout.simple_spinner_dropdown_item, characterNamesList);
//        characterSpinner.setAdapter(adapter);
//        characterSpinner.setOnItemSelectedListener(this);

        Integer[] avatarImageArrayInScene = new Integer[characterList.size()];

        int count = 0;
        for(Character character: characterList) {
            for(int i = 0; i < avatarNameArray.length; i++) {
                if(avatarNameArray[i].equalsIgnoreCase(character.getAvatarCode())) {
                    avatarImageArrayInScene[count] = avatarImageArray[i];
//                    Toast.makeText(this, "" + count + " avatar: " + avatarImageArrayInScene[count].toString(), Toast.LENGTH_LONG).show();
                    count++;
                }
            }
        }

        SpinnerAdapter adapter = new SpinnerAdapter(UpdateLineActivity.this, R.layout.avatar_spinner_row_layout, characterNamesList, avatarImageArrayInScene);
        characterSpinner.setAdapter(adapter);
        characterSpinner.setOnItemSelectedListener(this);

        int spinnerIndex = -1;
        for(int i = 0; i < characterNamesList.length; i++) {
            if(line.getCharacter().getName().equalsIgnoreCase(characterNamesList[i])) { spinnerIndex = i; }
        }
        characterSpinner.setSelection(spinnerIndex);
//
    }

    public void updateLineBtn(View v) {
        if(etDialog.getText().toString().length() > 0) {
            String dialogCapitalized = etDialog.getText().toString().trim().substring(0,1).toUpperCase() + etDialog.getText().toString().trim().substring(1);
            line.setCharacter(DBA.getCharacterByID(characterID));
            line.setDialog(dialogCapitalized);
            DBA.updateLine(line);

            Intent in = new Intent(this, LineListActivity.class);
            in.putExtra("SCENE_ID", sceneID);
            startActivity(in);
        } else { Toast.makeText(this, "Please Enter Character Dialog" , Toast.LENGTH_LONG).show(); }
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
        Intent in = new Intent(this, LineListActivity.class);
        in.putExtra("SCENE_ID", line.getScene().getUID());
        startActivity(in);
    }

    /**********************************************************************************************************************
     *                                  S P I N N E R   A D A P T O R
     **********************************************************************************************************************/


    public class SpinnerAdapter extends ArrayAdapter<String> {

        Context context;
        String[] avatarNameArray;
        Integer[] avatarImageArray;

        public SpinnerAdapter(Context context, int resource, String[] objects, Integer[] imageArray) {
            super(context, R.layout.avatar_spinner_row_layout, R.id.tvAvatarSpinner, objects);
            this.context = context;
            this.avatarNameArray = objects; //rename?
            this.avatarImageArray = imageArray;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View converView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.avatar_spinner_row_layout, parent, false);

            TextView textView = (TextView) row.findViewById(R.id.tvAvatarSpinner);
            textView.setText(avatarNameArray[position]);

            ImageView imageView = (ImageView) row.findViewById(R.id.ivAvatarSpinner);
            imageView.setImageResource(avatarImageArray[position]);

            return row;
        }

    }
}
