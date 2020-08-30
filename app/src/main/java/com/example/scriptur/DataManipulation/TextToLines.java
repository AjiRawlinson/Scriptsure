package com.example.scriptur.DataManipulation;

import android.util.Log;
import android.util.SparseArray;

import com.example.scriptur.Database.DBAdaptor;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextToLines {

    int sceneId;
    ArrayList<String> characterNames;
    int Blockcursor;
    SparseArray<TextBlock> textBlock;

    public TextToLines(int sceneId, SparseArray<TextBlock> text, ArrayList<String> names) {
        this.sceneId = sceneId;
        this.textBlock = text;
        this.characterNames = names;
        this.Blockcursor = 0;
        getCharacterNames();
    }

    public String removeStageDirections(String line) {
        Pattern p = Pattern.compile("\\p{Alpha}");
        Matcher m = p.matcher(line);
        String onlyDialog;
        if(m.find()) {
            onlyDialog = line.substring(m.start());
        } else { onlyDialog = line; }

        int stageDirectionStart;
        int stageDirectionEnd;

        do {
            stageDirectionStart = onlyDialog.indexOf('(');
            stageDirectionEnd = onlyDialog.indexOf(')');

            if (stageDirectionStart != -1 && stageDirectionEnd != -1) {
                if (stageDirectionStart < stageDirectionEnd) {
                    String lineBeginning = onlyDialog.substring(0, stageDirectionStart);
                    if(stageDirectionEnd + 1 >= onlyDialog.length()) {
                        onlyDialog = lineBeginning;
                    } else {
                        String lineEnding = onlyDialog.substring(stageDirectionEnd + 1);
                        onlyDialog = lineBeginning + lineEnding;
                    }
                } else {
                    onlyDialog = onlyDialog.substring(stageDirectionEnd, stageDirectionStart);
                }
            } else if (stageDirectionStart != -1) {
                onlyDialog = onlyDialog.substring(0, stageDirectionStart);
            } else if (stageDirectionEnd != -1) {
                onlyDialog = onlyDialog.substring(stageDirectionEnd + 1);
            }
        } while(stageDirectionStart != -1 && stageDirectionEnd != -1);
        return onlyDialog;
    }

    public void getCharacterNames() {
        for(String name: characterNames) {
            name = name.toUpperCase();
        }
    }

    public ArrayList<String> getLinesFromTextBlocks() {
        boolean blockIncludesCharacter = false, lineIncludesCharacter = false;
        String character = "";
        ArrayList<String> linesRecognised = new ArrayList<String>();
        ArrayList<String> characterRecognised = new ArrayList<String>();
        int resultindex = -1;

        while(Blockcursor < textBlock.size()) {

            Log.d("LINES RECOGNISED", "Reach Block" + Blockcursor);
            String textInTextBlock = textBlock.get(Blockcursor).getValue();
            blockIncludesCharacter = false;
            Log.d("Block:", "" + textInTextBlock);

            //search Block Text for any character names, the block of text we want to get dialog from
            for(String characterName: characterNames) {

                if (textInTextBlock.contains(characterName)) {
                    blockIncludesCharacter = true;
                    break;
                }
            }

            //If current block of texts contains a Characters name
            if(blockIncludesCharacter) {
                boolean foundFirstDialog = false;
                int lineCursor = 0;
                List<? extends Text> lines = textBlock.get(Blockcursor).getComponents();

                //loop through all lines in that block
                while (lineCursor < lines.size()) {
                    String textInLine = lines.get(lineCursor).getValue();
                    lineIncludesCharacter = false;

                    //search line for characterName
                    for(String characterName: characterNames) {
                        if (textInLine.contains(characterName)) {
                            character = characterName;
                            lineIncludesCharacter = true;
                            foundFirstDialog = true;
                            resultindex++;
                            break;
                        }
                    }

                    //Add character to Arraylist of Characters, and rest of text text to Dialog Arraylist
                    if(lineIncludesCharacter) {
                        int endOfCharacterPrefix = textInLine.indexOf(character) + character.length();
                        characterRecognised.add(removePunctuation(character) + ":");
                        String dialog = textInLine.substring(endOfCharacterPrefix);
                        linesRecognised.add(dialog);
                    }

                    //if No Character name found on this line, Adding dialog to current Dialog Arraylist Element
                    else if (foundFirstDialog && !lineIncludesCharacter) {
                        String dialog = linesRecognised.get(resultindex) + " " + textInLine;
                        linesRecognised.set(resultindex, dialog);
                    }
                    lineCursor++;
                }
            }
            Blockcursor++;
        }

        //looping through each line, and removing any stage direction and characternames so can me returned in on Arraylist
        for(int i = 0; i < linesRecognised.size(); i++) { //
            String dialog = removeStageDirections(linesRecognised.get(i));
            Log.d("Dialog Only", dialog);
            linesRecognised.set(i, "" + characterRecognised.get(i) + dialog.trim());
        }
        return linesRecognised;
    }

    public String removePunctuation(String text){
        String onlyLetters = text.replaceAll("[^a-zA-Z ]", "");
        return onlyLetters;
    }
}
