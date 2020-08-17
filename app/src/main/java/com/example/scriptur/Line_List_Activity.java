package com.example.scriptur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import android.speech.tts.Voice;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scriptur.Database.DBAdaptor;
import com.example.scriptur.Database.Line;
import com.example.scriptur.RecyclerViewAdaptors.RVAdaptorLine;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.zip.Inflater;

public class Line_List_Activity extends AppCompatActivity implements RVAdaptorLine.OnRowListener {

    RecyclerView rvLine;
    ArrayList<Line> actualLineList, displayLineList;
    DBAdaptor DBA;
    RVAdaptorLine RVALine;
    private TextToSpeech tts;
    SpeechRecognizer recognizer;
    final Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);//can't go in OnCreate??
    TextView noData;
    int sceneID, currentLine, lineScore;
    boolean hideLines = false;

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

        noData = (TextView) findViewById(R.id.tvLineNoData);
        rvLine = (RecyclerView) findViewById(R.id.RVLine);
        rvLine.setNestedScrollingEnabled(true);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rvLine.setLayoutManager(llm);
        actualLineList = DBA.getAllLinesInScene(sceneID);
        displayLineList = DBA.getAllLinesInScene(sceneID);
        if(!displayLineList.isEmpty()) {
            RVALine = new RVAdaptorLine(this, displayLineList, hideLines, this);
            rvLine.setAdapter(RVALine);
        } else { noData.setVisibility(View.VISIBLE); }

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
                        new Thread() {
                            public void run() {
                                Line_List_Activity.this.runOnUiThread(new Runnable() {
                                    public void run() { speakLine(); }
                                });
                            }
                        }.start();
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
                lineScore = ct.calculateScore(actualLineList.get(currentLine).getDialog(), voiceInput);
                Toast.makeText(Line_List_Activity.this, "input line: " + voiceInput + "\nscore: " + lineScore + "%", Toast.LENGTH_SHORT).show();
                Line line = actualLineList.get(currentLine);
                line.setScore(lineScore);
                DBA.updateLine(line);
                currentLine++;
                RVALine.notifyDataSetChanged();
                speakLine();
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
        if(currentLine < displayLineList.size()) {
            currentLine++;
            speakLine();
        } else {
            Toast.makeText(this, "Last line in Scene", Toast.LENGTH_LONG).show();
        }
    }

    public void previousLineBtn(View v) {
        if(tts.isSpeaking()) { tts.stop(); }
        if(currentLine < displayLineList.size()) {
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
        if(currentLine < displayLineList.size()) {
            rvLine.smoothScrollToPosition(currentLine);
            if(actualLineList.get(currentLine).getCharacter().isUserPart()) {
                recognizer.startListening(recognizerIntent);
            } else {
                switch (actualLineList.get(currentLine).getCharacter().getAvatarCode()) {
                    case "female 1":
                        getVoice("gba-local", actualLineList.get(currentLine).getDialog());
                        break;
                    case "female 2":
                        getVoice("gba-network", actualLineList.get(currentLine).getDialog());
                        break;
                    case "female 3":
                        getVoice("gbc-local", actualLineList.get(currentLine).getDialog());
                        break;
                    case "female 4":
                        getVoice("gbc-network", actualLineList.get(currentLine).getDialog());
                        break;
                    case "female 5":
                        getVoice("fis-local", actualLineList.get(currentLine).getDialog());
                        break;
                    case "female 6":
                        getVoice("fis-network", actualLineList.get(currentLine).getDialog());
                        break;
                    case "male 1":
                        getVoice("gbb-local", actualLineList.get(currentLine).getDialog());
                        break;
                    case "male 2":
                        getVoice("gbb-network", actualLineList.get(currentLine).getDialog());
                        break;
                    case "male 3":
                        getVoice("gbd-local", actualLineList.get(currentLine).getDialog());
                        break;
                    case "male 4":
                        getVoice("gbd-network", actualLineList.get(currentLine).getDialog());
                        break;
                    case "male 5":
                        getVoice("rjs-local", actualLineList.get(currentLine).getDialog());
                        break;
                    case "male 6":
                        getVoice("rjs-network", actualLineList.get(currentLine).getDialog());
                        break;
                    default:
                        break;
                }
//                tts.speak(lineList.get(currentLine).getDialog(), TextToSpeech.QUEUE_FLUSH, null, "");
            }
        } else { currentLine = 0; }
    }

    public void getVoice(String avatarCode, String dialog) {
        Set<Voice> voices = tts.getVoices();
        int count = 0;
        Toast.makeText(this, "here!" + voices.size(), Toast.LENGTH_SHORT).show();
        for(Voice voice: voices) {
            if(voice.getName().contains("en-gb") && voice.getName().contains(avatarCode)) {
                tts.setVoice(voice);
                tts.speak(dialog, TextToSpeech.QUEUE_ADD, null, "");
                Log.v("TAG", "Voice " + count + ": "  + voice.getName());
            }
            count++;
        }
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
                        in.putExtra("LINE_ID", actualLineList.get(position).getUID());
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
                        DBA.deleteLine(displayLineList.get(position));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.line_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.itemNewLine:
                Intent in = new Intent(this, NewLineActivity.class);
                in.putExtra("SCENE_ID", sceneID);
                startActivity(in);
                return true;
            case R.id.itemhideUserLine:
                if(!hideLines) {
                    hideLines = true;
                    for (Line line : displayLineList) {
                        if (line.getCharacter().isUserPart()) {
                            String stars = line.getDialog().replaceAll("[^ ]", "*");
                            line.setDialog(stars);
                        }
                    }
                } else {
                    hideLines = false;
                    ArrayList<Line> newLineList = DBA.getAllLinesInScene(sceneID);
                    for(int i = 0; i < displayLineList.size(); i++) { //notifying adapter on data set chanege only seems to work when you set chage, re geting it from DB
                        if(displayLineList.get(1).getCharacter().isUserPart()) {displayLineList.get(i).setDialog(newLineList.get(i).getDialog()); }
                    }
                }
                RVALine.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, SceneCharacterTabbedActivity.class);
        in.putExtra("PLAY_ID", DBA.getSceneByID(sceneID).getPlay().getUID());
        in.putExtra("TAB_NUM", 1);
        startActivity(in);
    }
}
