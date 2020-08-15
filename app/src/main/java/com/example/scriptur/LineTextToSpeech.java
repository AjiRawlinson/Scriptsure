package com.example.scriptur;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.Locale;

public class LineTextToSpeech {

    TextToSpeech tts;
    String text;
    Context context;

    public LineTextToSpeech(Context context, String text) {
        this.text = text;


        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }

                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        Log.v("my tag", "Speaking started");                    }

                    @Override
                    public void onDone(String utteranceId) {
//                        currentLine++;
//                        Log.v("my tag", "stopped started");
//                        speakLine();
                    }

                    @Override
                    public void onError(String utteranceId) {

                    }
                });
            }
        });
    }

    public void speakText() {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "");

    }


}
