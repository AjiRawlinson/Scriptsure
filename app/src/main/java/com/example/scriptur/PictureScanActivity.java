package com.example.scriptur;

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
    int sceneID, playID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_scan);
        setTitle("Insert Lines From Picture");


        photo = (ImageView) findViewById(R.id.ivPhoto);
        capture = (Button) findViewById(R.id.btnInputMultiLines);

        Intent in = getIntent();
        String photoPath = in.getStringExtra("PATH");
        sceneID = in.getIntExtra("SCENE_ID", 1);

        DBA = new DBAdaptor(this);
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

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/800, photoH/600));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
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
            int order = DBA.getNumberOfLinesInScene(sceneID);
            DBA.insertLine(insertCharacter.getUID(), line.substring(characterLineDivide + 1), sceneID, order);
        }
        Toast.makeText(this, "" + linesFromImage.size() + " Lines added to Scene", Toast.LENGTH_LONG).show();
        Intent in = new Intent(this, Line_List_Activity.class);
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
