package com.example.scriptur;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scriptur.Database.DBAdaptor;
import com.example.scriptur.Database.Line;
import com.example.scriptur.RecyclerViewAdaptors.RVAdaptorLine;

import java.util.ArrayList;
import java.util.Locale;

public class Line_List_Activity extends AppCompatActivity implements RVAdaptorLine.OnRowListener {

    RecyclerView rvLine;
    ArrayList<Line> lineList;
    DBAdaptor DBA;
    RVAdaptorLine RVALine;
    TextToSpeech tts;
    SpeechRecognizer recognizer;
    final Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);//can't go in OnCreate??
    TextView sceneName;
    int sceneID, currentLine, lineScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_list);

        DBA = new DBAdaptor(this);
        currentLine = 0;
        lineScore = 0;
        Intent in = getIntent();
        sceneID = in.getIntExtra("SCENE_ID", 1);
        setTitle(DBA.getSceneByID(sceneID).getName());


        rvLine = (RecyclerView) findViewById(R.id.RVLine);
        rvLine.setNestedScrollingEnabled(true);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rvLine.setLayoutManager(llm);
        lineList = DBA.getAllLinesInScene(sceneID); //change to scenes
        RVALine = new RVAdaptorLine(this, lineList, this);
        rvLine.setAdapter(RVALine);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }

                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        currentLine++;
                    }

                    @Override
                    public void onDone(String utteranceId) {
//                        speakLine();
                    }

                    @Override
                    public void onError(String utteranceId) {

                    }
                });
            }
        });


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }



        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        recognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                //TODO vibrate after 2? seconds of no speech
                Toast.makeText(Line_List_Activity.this, "Listening", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> voiceInputAL = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String voiceInput = voiceInputAL.toString().substring(1, (voiceInputAL.toString().length() - 1)); //removes square brackets around input string
                CompareText ct = new CompareText();
                lineScore = ct.calculateScore(lineList.get(currentLine).getDialog(), voiceInput);
                Toast.makeText(Line_List_Activity.this, "actual line: " + lineList.get(currentLine).getDialog() + "\ninput line: " + voiceInput, Toast.LENGTH_SHORT).show();
                Toast.makeText(Line_List_Activity.this, "score: " + lineScore + "%", Toast.LENGTH_LONG).show();
                Line line = lineList.get(currentLine);
                line.setScore(lineScore);
                DBA.updateLine(line);
                currentLine++;
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });


    }

    public void addLineBtn(View v) {
        Intent in = new Intent(this, NewLineActivity.class);
        in.putExtra("SCENE_ID", sceneID);
        startActivity(in);
    }

    public void startBtn(View v) { //rename play lines?
        speakLine();
    }

    public void nextLineBtn(View v) {
        if(tts.isSpeaking()) { tts.stop(); }
        if(currentLine < lineList.size()) {
            currentLine++;
            speakLine();
        } else {
            Toast.makeText(this, "Last line in Scene", Toast.LENGTH_LONG).show();
        }
    }

    public void previousLineBtn(View v) {
        if(tts.isSpeaking()) { tts.stop(); }
        if(currentLine < lineList.size()) {
            currentLine -= 2; // -2 because when speaking starts, currentLine++ is already called
            speakLine();
        } else {
            Toast.makeText(this, "First line in Scene", Toast.LENGTH_LONG).show();
        }
    }

    public void fromTheTopBtn(View v) {
        if(tts.isSpeaking()) { tts.stop(); }
        currentLine = 0;
        speakLine();
    }



    public void speakLine() {
        if(currentLine < lineList.size()) {
            if(lineList.get(currentLine).getCharacter().isUserPart()) {
                recognizer.startListening(recognizerIntent);
            } else {
                rvLine.smoothScrollToPosition(currentLine);
                tts.speak(lineList.get(currentLine).getDialog(), TextToSpeech.QUEUE_FLUSH, null, "");
            }
        } else { currentLine = 0; }
    }

    @Override
    public void onRowLongClick(final int position, View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenu().add("Edit");
        popup.getMenu().add("Delete");
//        popup.getMenu().add("Move Up"); //TODO
//        popup.getMenu().add("Move Down"); //TODO
//        insert line below?
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.toString()) {
                    case "Edit":
                        Intent in = new Intent(Line_List_Activity.this, UpdateLineActivity.class);
                        in.putExtra("LINE_ID", lineList.get(position).getUID());
                        startActivity(in);
                        break;
                    case "Delete":
                        deleteLineConfirmation(position);
                        break;
                    default://do nothing
                        break;
                }
                return true;
            }
        });
    }

    public void deleteLineConfirmation(final int position) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        DBA.deleteLine(lineList.get(position));
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
//                        do nothing
                        break;
                    default://do nothing
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete this Line: ")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void checkPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},1);
            }


    }
}
