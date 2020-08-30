package com.example.scriptur;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.scriptur.DataManipulation.TextToLines;
import com.example.scriptur.Database.Character;
import com.example.scriptur.Database.DBAdaptor;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.ArrayList;

public class PictureScanActivity extends AppCompatActivity {

    ImageView photo;
    Button capture;
    Bitmap bitmap;
    private static final int CAMERA_REQUEST = 1001;//maybe?
    DBAdaptor DBA;
    ArrayList<Character> characterList;
    ArrayList<String> characterNames;
    int sceneID, playID ,order;
    boolean insertLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_scan);
        setTitle("Insert Lines From Picture");
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        }


        photo = (ImageView) findViewById(R.id.ivPhoto);
        capture = (Button) findViewById(R.id.btnInputMultiLines);


        DBA = new DBAdaptor(this);

        Intent in = getIntent();
        String photoPath = in.getStringExtra("PATH");
        sceneID = in.getIntExtra("SCENE_ID", 1);
        order = in.getIntExtra("ORDER_NUM", -1);
        insertLine = true;
        if(order == -1) {
            insertLine = false;
            order = DBA.getNumberOfLinesInScene(sceneID);
        }

        playID = DBA.getSceneByID(sceneID).getPlay().getUID();
        characterList = DBA.getCharactersBySceneID(sceneID);
        characterNames = new ArrayList<String>();
        for(Character character: characterList) {
            characterNames.add(character.getName().toUpperCase());
        }


        int targetW = photo.getWidth(); //0?
        int targetH = photo.getHeight(); //0?


        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inPurgeable = true;

        bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
        photo.setImageBitmap(bitmap);

    }

    public void takePicture(View v) { //change name
        TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if(!recognizer.isOperational()) {
            Toast.makeText(getApplicationContext(), "Text Recognizer not available yet.", Toast.LENGTH_SHORT).show();
        }
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        final SparseArray<TextBlock> items = recognizer.detect(frame);
        TextToLines ttl = new TextToLines(sceneID,items, characterNames);
        ArrayList<String> linesFromImage = ttl.getLinesFromTextBlocks();

        for(String line: linesFromImage) {
            int characterLineDivide = line.indexOf(':');
            Character insertCharacter = new Character();
            String name = line.substring(0, characterLineDivide);
            for(Character character: characterList) {
                if(name.equalsIgnoreCase(character.getName())) {
                    insertCharacter = character;
                    break;
                }
            }
            if(insertLine) {
                DBA.moveLinesDown(sceneID, order, linesFromImage.size());
                insertLine = false;//already moved lines that fall below.
            }
            DBA.insertLine(insertCharacter.getUID(), line.substring(characterLineDivide + 1), sceneID, order);
            order++;
        }
        Toast.makeText(this, "" + linesFromImage.size() + " Lines added to Scene", Toast.LENGTH_LONG).show();
        Intent in = new Intent(this, LineListActivity.class);
        in.putExtra("SCENE_ID", sceneID);
        startActivity(in);
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, NewLineActivity.class);
        in.putExtra("SCENE_ID", sceneID);
        startActivity(in);
    }
}
