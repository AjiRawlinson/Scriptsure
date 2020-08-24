package com.example.scriptur;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewLineActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ArrayList<Character> characterList; //need to be global?
    DBAdaptor DBA;
    EditText etDialog;
    Spinner characterSpinner;
    boolean lineInsert;
    private static final String[] avatarNameArray = {"Female 1", "Female 2", "Female 3", "Female 4", "Female 5", "Female 6",
                                                    "Male 1", "Male 2", "Male 3", "Male 4", "Male 5", "Male 6"};
    private static final Integer[] avatarImageArray = {R.drawable.female1, R.drawable.female2, R.drawable.female3,
                                                        R.drawable.female4, R.drawable.female5, R.drawable.female6,
                                                        R.drawable.male1, R.drawable.male2, R.drawable.male3,
                                                        R.drawable.male4, R.drawable.male5, R.drawable.male6};
    int sceneID, characterID, order;
    String photoPath = "";
    private static final int CAMERA_REQUEST = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_line);
        setTitle("New Line");
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        DBA = new DBAdaptor(this);
        etDialog = (EditText) findViewById(R.id.etLineDialog);
        characterSpinner = (Spinner) findViewById(R.id.SpinnerCharacters);
        characterID = 0;

        Intent in = getIntent();
        sceneID = in.getIntExtra("SCENE_ID", 1);
        order = in.getIntExtra("ORDER_NUM", -1);
        lineInsert = true;
        if(order == -1) {
            lineInsert = false;
            order = DBA.getNumberOfLinesInScene(sceneID);
        }

        characterList = DBA.getCharactersBySceneID(sceneID);
        String[] characterNamesList = new String[characterList.size()];
        for(int i = 0; i < characterNamesList.length; i++) {
            characterNamesList[i] = characterList.get(i).getName();
        }

        Integer[] avatarImageArrayInScene = new Integer[characterList.size()];

        int count = 0;
        for(Character character: characterList) {
            for(int i = 0; i < avatarNameArray.length; i++) {
                if(avatarNameArray[i].equalsIgnoreCase(character.getAvatarCode())) {
                    avatarImageArrayInScene[count] = avatarImageArray[i];
                    count++;
                }
            }
        }

        SpinnerAdapter adapter = new SpinnerAdapter(NewLineActivity.this, R.layout.avatar_spinner_row_layout,  characterNamesList, avatarImageArrayInScene);
        characterSpinner.setAdapter(adapter);
        characterSpinner.setOnItemSelectedListener(this);
    }

    public void saveLineBtn(View v) {
        if(etDialog.getText().toString().length() > 0) {
            if (lineInsert) {
                DBA.moveLinesDown(sceneID, order, 1);
            }
            String dialogCapitalized = etDialog.getText().toString().trim().substring(0, 1).toUpperCase() + etDialog.getText().toString().trim().substring(1);
            DBA.insertLine(characterID, dialogCapitalized, sceneID, order);

            Intent in = new Intent(this, Line_List_Activity.class);
            in.putExtra("SCENE_ID", sceneID);
            startActivity(in);
        } else { Toast.makeText(this, "Please Enter Character Dialog" , Toast.LENGTH_LONG).show(); }
    }

    public void openCameraBtn(View v) {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        }
        else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            if(cameraIntent.resolveActivity(getPackageManager()) != null) {
                File photo = null;
                try {
                    photo = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(photo != null) {
                    Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photo);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {

                Intent in = new Intent(this, PictureScanActivity.class);
                in.putExtra("PATH", photoPath);
                in.putExtra("SCENE_ID", sceneID);
                if(lineInsert) {
                    in.putExtra("ORDER_NUM", order);
                }
                startActivity(in);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        characterID = characterList.get(position).getUID();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        characterID = characterList.get(0).getUID();
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, Line_List_Activity.class);
        in.putExtra("SCENE_ID", sceneID);
        startActivity(in);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String fileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(fileName, ".jpg", storageDir);

        photoPath = image.getAbsolutePath();
        Log.v("TAG", photoPath);
        return image;
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
