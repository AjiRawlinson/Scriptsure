package com.example.scriptur;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scriptur.DataManipulation.Colours;
import com.example.scriptur.Database.Character;
import com.example.scriptur.Database.DBAdaptor;

import org.apache.commons.text.WordUtils;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

public class NewCharacterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    DBAdaptor DBA;
    EditText name;
    ImageView topImage;
    Spinner genderSpinner;
    Switch userRoleSwitch;
    Button colourBtn;
    private TextToSpeech tts;
    private static final String[] avatarNameArray = {"Female 1", "Female 2", "Female 3", "Female 4", "Female 5", "Female 6",
                                                    "Male 1", "Male 2", "Male 3", "Male 4", "Male 5", "Male 6"};
    private static final Integer[] avatarImageArray = {R.drawable.female1, R.drawable.female2, R.drawable.female3,
                                                R.drawable.female4, R.drawable.female5, R.drawable.female6,
                                                R.drawable.male1, R.drawable.male2, R.drawable.male3,
                                                R.drawable.male4, R.drawable.male5, R.drawable.male6};
    String avatar, colour;
    boolean userRole;
    int playid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_character);
        setTitle("New Character");
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        DBA = new DBAdaptor(this);
        name = (EditText) findViewById(R.id.etCharacterName);
        topImage = (ImageView) findViewById(R.id.ivNewCharacter);
        genderSpinner = (Spinner) findViewById(R.id.genderSpinner);
        userRoleSwitch = (Switch) findViewById(R.id.userRoleSwitch);
        colourBtn = (Button) findViewById(R.id.btn_Character_Colour);
        Colours colours = new Colours();
        colour = colours.randomColour();
        colourBtn.setBackgroundColor(Color.parseColor(colour));
        topImage.setBackgroundColor(Color.parseColor(colour));
        avatar = "female1";

        Intent in = getIntent();
        playid = in.getIntExtra("PLAY_ID", 0);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });


        SpinnerAdapter adapter = new SpinnerAdapter(NewCharacterActivity.this, R.layout.avatar_spinner_row_layout, avatarNameArray, avatarImageArray);
        genderSpinner.setAdapter(adapter);
        genderSpinner.setOnItemSelectedListener(this);

    }

    public void setCharacterColourBtn(View v) {
        Colours colours = new Colours();
        colour = colours.randomColour();
        topImage.setBackgroundColor(Color.parseColor(colour));
        colourBtn.setBackgroundColor(Color.parseColor(colour));
    }

    public void testAvatarVoice(String code) {
        Set<Voice> voices = tts.getVoices();
        for(Voice voice: voices) {
            if(voice.getName().contains("en-gb") && voice.getName().contains(code)) {
                tts.setVoice(voice);
                tts.speak("The quick brown fox jumped over the lazy dog",
                        TextToSpeech.QUEUE_ADD, null, "");
            }
        }
    }

    public void testVoice(View v) {
        switch (avatar) {
            case "female 1":
                testAvatarVoice("gba-local");
                break;
            case "female 2":
                testAvatarVoice("gba-network");
                break;
            case "female 3":
                testAvatarVoice("gbc-local");
                break;
            case "female 4":
                testAvatarVoice("gbc-network");
                break;
            case "female 5":
                testAvatarVoice("fis-local");
                break;
            case "female 6":
                testAvatarVoice("fis-network");
                break;
            case "male 1":
                testAvatarVoice("gbb-local");
                break;
            case "male 2":
                testAvatarVoice("gbb-network");
                break;
            case "male 3":
                testAvatarVoice("gbd-local");
                break;
            case "male 4":
                testAvatarVoice("gbd-network");
                break;
            case "male 5":
                testAvatarVoice("rjs-local");
                break;
            case "male 6":
                testAvatarVoice("rjs-network");
                break;
        }
    }



    public void saveCharacterBtn(View v) {
        if(name.getText().toString().length() > 0) {
            ArrayList<Character> characterList = DBA.getAllCharactersInPlay(playid);
            String nameCapitalized = WordUtils.capitalizeFully(name.getText().toString().trim());
            boolean validName = true;
            for (Character characterDB : characterList) {
                if (characterDB.getName().equalsIgnoreCase(nameCapitalized)) {
                    validName = false;
                }
            }

            if (validName) {
                if (userRoleSwitch.isChecked()) {
                    userRole = true;
                } else {
                    userRole = false;
                }
                DBA.insertCharacter(nameCapitalized, avatar, userRole, colour, playid);

                Intent in = new Intent(this, SceneCharacterTabbedActivity.class);
                in.putExtra("PLAY_ID", playid);
                in.putExtra("TAB_NUM", 0); //decides which tab to open on 0 = characters, 1 = scenes
                startActivity(in);
            } else {
                name.setText("");
                Toast.makeText(this, "Character with that name already exists in this play.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Please Enter Character Name.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position) {
            case 0:
                avatar = avatarNameArray[0].toLowerCase();
                topImage.setImageResource(R.drawable.female1);
                break;
            case 1:
                avatar = avatarNameArray[1].toLowerCase();
                topImage.setImageResource(R.drawable.female2);
                break;
            case 2:
                avatar = avatarNameArray[2].toLowerCase();
                topImage.setImageResource(R.drawable.female3);
                break;
            case 3:
                avatar = avatarNameArray[3].toLowerCase();
                topImage.setImageResource(R.drawable.female4);
                break;
            case 4:
                avatar = avatarNameArray[4].toLowerCase();
                topImage.setImageResource(R.drawable.female5);
                break;
            case 5:
                avatar = avatarNameArray[5].toLowerCase();
                topImage.setImageResource(R.drawable.female6);
                break;
            case 6:
                avatar = avatarNameArray[6].toLowerCase();
                topImage.setImageResource(R.drawable.male1);
                break;
            case 7:
                avatar = avatarNameArray[7].toLowerCase();
                topImage.setImageResource(R.drawable.male2);
                break;
            case 8:
                avatar = avatarNameArray[8].toLowerCase();
                topImage.setImageResource(R.drawable.male3);
                break;
            case 9:
                avatar = avatarNameArray[9].toLowerCase();
                topImage.setImageResource(R.drawable.male4);
                break;
            case 10:
                avatar = avatarNameArray[10].toLowerCase();
                topImage.setImageResource(R.drawable.male5);
                break;
            case 11:
                avatar = avatarNameArray[11].toLowerCase();
                topImage.setImageResource(R.drawable.male6);
                break;
        }

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        avatar = avatarNameArray[0].toLowerCase();
    }


    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, SceneCharacterTabbedActivity.class);
        in.putExtra("PLAY_ID", playid);
        in.putExtra("TAB_NUM", 0);
        startActivity(in);
    }

    /**********************************************************************************************************************
     *                                  S P I N N E R   A D A P T O R
     **********************************************************************************************************************/


    public class SpinnerAdapter extends ArrayAdapter<String> {

        Context context;
        String[] avatarNameArray;
        Integer[] avatarImageArray;

        public SpinnerAdapter(Context context, int resource, String[] objects, Integer[] imageArray)  {
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
