package com.example.scriptur.DataManipulation;

import android.util.Log;
import android.util.SparseArray;

import com.example.scriptur.Database.DBAdaptor;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;
import java.util.List;

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
        String onlyDialog = line;
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

            //search Block Text for any character names, the block of text we want to get dialog from
            for(String characterName: characterNames) {

                if (textInTextBlock.contains(characterName + ":") || textInTextBlock.contains(characterName + ".")) {
                    blockIncludesCharacter = true;
                    break;
                }
            }

            if(blockIncludesCharacter) {
                boolean foundFirstDialog = false;
                int lineCursor = 0;
                List<? extends Text> lines = textBlock.get(Blockcursor).getComponents();

                while (lineCursor < lines.size()) { //loop through all lines in block
                    String textInLine = lines.get(lineCursor).getValue();
                    lineIncludesCharacter = false;

                    //search line for characterName
                    for(String characterName: characterNames) {
                        if (textInLine.contains(characterName + ":") || textInLine.contains(characterName + ".")) {
                            character = characterName;
                            lineIncludesCharacter = true;
                            foundFirstDialog = true;
                            resultindex++;
                            break;
                        }
                    }

                    if(lineIncludesCharacter) {
                        int endOfCharacterPrefix = textInLine.indexOf(character) + character.length() + 2;
                        characterRecognised.add(character + ": ");
                        String dialog = textInLine.substring(endOfCharacterPrefix);
                        linesRecognised.add(dialog);
                    } else if (foundFirstDialog && !lineIncludesCharacter) {
                        String dialog = linesRecognised.get(resultindex) + " " + textInLine;
                        linesRecognised.set(resultindex, dialog);
                    }
                    lineCursor++;
                }


            }
            Blockcursor++;
        }
        for(int i = 0; i < linesRecognised.size(); i++) {
            String dialog = removeStageDirections(linesRecognised.get(i));
            Log.d("Dialog Only", dialog);
            linesRecognised.set(i, "" + characterRecognised.get(i) + dialog);
        }
        return linesRecognised;
    }
}
