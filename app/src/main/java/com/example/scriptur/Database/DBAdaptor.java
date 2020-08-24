package com.example.scriptur.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBAdaptor {
    DBHelper myHelper;

    public DBAdaptor(Context context) {
         myHelper = new DBHelper(context);
    }

    /*****************
     *     Plays
     *****************/

    public long insertPlay(String title, String colour) {
        SQLiteDatabase DB = myHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(myHelper.PLAY_TITLE, title);
        cv.put(myHelper.PLAY_COLOUR, colour);
        long id = DB.insert(myHelper.PLAY_TABLE_NAME, null, cv);
        return id;
    }

    public ArrayList<Play> getAllPlays() {
        ArrayList<Play> playList = new ArrayList<>();
        SQLiteDatabase DB = myHelper.getReadableDatabase();
        String[] columns = {myHelper.PLAY_ID, myHelper.PLAY_TITLE, myHelper.PLAY_COLOUR};
        Cursor cursor = DB.query(myHelper.PLAY_TABLE_NAME, columns, null, null, null, null, myHelper.PLAY_TITLE + " ASC");
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(myHelper.PLAY_ID));
            String title = cursor.getString(cursor.getColumnIndex(myHelper.PLAY_TITLE));
            String colour = cursor.getString(cursor.getColumnIndex(myHelper.PLAY_COLOUR));
            Play play = new Play(id, title, colour);
            playList.add(play);
        }
        return playList;
    }

    public void deletePlay(Play play) {
        SQLiteDatabase db = myHelper.getWritableDatabase();
        ArrayList<Scene> sceneList = getAllScenesInPlay(play.getUID());
        for(Scene scene: sceneList) {
            deleteScene(scene);
        }
        ArrayList<Character> characterList = getAllCharactersInPlay(play.getUID());
        for(Character character: characterList) {
            deleteCharacter(character);
        }

        String[] UID = {"" + play.getUID()};
        db.delete(myHelper.PLAY_TABLE_NAME, myHelper.PLAY_ID + " = ?", UID);
    }

    public void updatePlay(Play play) {
        SQLiteDatabase DB = myHelper.getWritableDatabase();
        String[] UID = {"" + play.getUID()};
        ContentValues cv = new ContentValues();
        cv.put(myHelper.PLAY_ID, play.getUID());
        cv.put(myHelper.PLAY_TITLE, play.getTitle());
        cv.put(myHelper.PLAY_COLOUR, play.getColour());
        DB.update(myHelper.PLAY_TABLE_NAME, cv, myHelper.PLAY_ID + " =?",  UID);
    }

    public Play getPlayByIndex(int index) {
        ArrayList<Play> plays = getAllPlays();
        if(index < plays.size()) { return plays.get(index); }
        return null;
    }

    public Play getPlayByTitle(String title) { //must have unique play names
        ArrayList<Play> playList = getAllPlays();
        for(Play play: playList) {
            if(play.getTitle().equalsIgnoreCase(title)) { return play; }
        }
        return null;
    }

    public Play getPlayByID(int id) {
        ArrayList<Play> playList = getAllPlays();
        for(Play play: playList) {
            if(play.getUID() == id) { return play; }
        }
        return null;
    }

    /*****************
    *   Character
     *****************/

    public long insertCharacter(String name, String avatarCode, Boolean userRole, String colour, int playID) {
        SQLiteDatabase DB = myHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(myHelper.CHARACTER_NAME, name);
        cv.put(myHelper.CHARACTER_AVATAR_CODE, avatarCode);
        if(!userRole) { cv.put(myHelper.CHARACTER_USER_PART, 0); }
        else { cv.put(myHelper.CHARACTER_USER_PART, 1);}
        cv.put(myHelper.CHARACTER_COLOUR, colour);
        cv.put(myHelper.CHARACTER_PLAY_ID_FK, playID);
        long id = DB.insert(myHelper.CHARACTER_TABLE_NAME, null, cv);
        return id;
    }

    public ArrayList<Character> getAllCharacters() {
        ArrayList<Character> characterList = new ArrayList<>();
        SQLiteDatabase DB = myHelper.getReadableDatabase();
        String[] columns = {myHelper.CHARACTER_ID, myHelper.CHARACTER_NAME, myHelper.CHARACTER_AVATAR_CODE, myHelper.CHARACTER_USER_PART, myHelper.CHARACTER_COLOUR, myHelper.CHARACTER_PLAY_ID_FK};
        Cursor cursor = DB.query(myHelper.CHARACTER_TABLE_NAME, columns, null, null, null, null, myHelper.CHARACTER_NAME + " ASC");
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(myHelper.CHARACTER_ID));
            String name = cursor.getString(cursor.getColumnIndex(myHelper.CHARACTER_NAME));
            String avatarCode = cursor.getString(cursor.getColumnIndex(myHelper.CHARACTER_AVATAR_CODE));
            int userRoleInt = cursor.getInt(cursor.getColumnIndex(myHelper.CHARACTER_USER_PART));
            Boolean userRole = false; if(userRoleInt == 1) { userRole = true; }
            String colour = cursor.getString(cursor.getColumnIndex(myHelper.CHARACTER_COLOUR));
            Play play = getPlayByID(cursor.getInt(cursor.getColumnIndex(myHelper.CHARACTER_PLAY_ID_FK)));
            Character character = new Character(id, name, avatarCode, userRole, colour,  play);
            characterList.add(character);
        }
        return characterList;
    }

    public ArrayList<Character> getAllCharactersInPlay(int playID) {
        ArrayList<Character> characterList = new ArrayList<>();
        SQLiteDatabase DB = myHelper.getReadableDatabase();
        Play play = getPlayByID(playID);//Why needed if just going to filter by play id anyway?
        String[] columns = {myHelper.CHARACTER_ID, myHelper.CHARACTER_NAME, myHelper.CHARACTER_AVATAR_CODE, myHelper.CHARACTER_USER_PART, myHelper.CHARACTER_COLOUR};
        Cursor cursor = DB.query(myHelper.CHARACTER_TABLE_NAME, columns, "" + myHelper.CHARACTER_PLAY_ID_FK + " = ?", new String[] { "" + play.getUID()}, null, null, myHelper.CHARACTER_NAME + " ASC");
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(myHelper.CHARACTER_ID));
            String name = cursor.getString(cursor.getColumnIndex(myHelper.CHARACTER_NAME));
            String avatarCode = cursor.getString(cursor.getColumnIndex(myHelper.CHARACTER_AVATAR_CODE));
            int userRoleInt = cursor.getInt(cursor.getColumnIndex(myHelper.CHARACTER_USER_PART));
            Boolean userRole = false; if(userRoleInt == 1) { userRole = true; }
            String colour = cursor.getString(cursor.getColumnIndex(myHelper.CHARACTER_COLOUR));
            Character character = new Character(id, name, avatarCode, userRole, colour, play);
            characterList.add(character);
        }
        return characterList;
    }

    public void deleteCharacter(Character character) {
        SQLiteDatabase db = myHelper.getWritableDatabase();
        deleteSceneCharacterRowsByCharacter(character);
        String[] UID = {"" + character.getUID()};
        db.delete(myHelper.CHARACTER_TABLE_NAME, myHelper.CHARACTER_ID_FK + " = ?", UID);
    }

    public void updateCharacter(Character character) {
        SQLiteDatabase DB = myHelper.getWritableDatabase();
        String[] UID = {"" + character.getUID()};
        ContentValues cv = new ContentValues();
        cv.put(myHelper.CHARACTER_NAME, character.getName());
        cv.put(myHelper.CHARACTER_AVATAR_CODE, character.getAvatarCode());
        cv.put(myHelper.CHARACTER_COLOUR, character.getColour());
        if(!character.isUserPart()) { cv.put(myHelper.CHARACTER_USER_PART, 0); }
        else { cv.put(myHelper.CHARACTER_USER_PART, 1);}
        DB.update(myHelper.CHARACTER_TABLE_NAME, cv, myHelper.CHARACTER_ID + " =?", UID);
    }

    public Character getCharacterByIndex(int playID, int index) {
        ArrayList<Character> characters = getAllCharactersInPlay(playID);
        if(index < characters.size()) { return characters.get(index); }
        return null;
    }

    public Character getCharacterByName(int playID, String name) { //must have unique character names in play
        ArrayList<Character> characterList = getAllCharactersInPlay(playID);
        for(Character character: characterList) {
            if(character.getName().equalsIgnoreCase(name)) { return character; }
        }
        return null;
    }

    public Character getCharacterByID(int id) {
        ArrayList<Character> characterList = getAllCharacters();
        for(Character character: characterList) {
            if(character.getUID() == id) { return character; }
        }
        return null;
    }

    /*****************
     *   Scenes
     *****************/

    public long insertScene(String name, ArrayList<Integer> characterIDList, String colour, int playID, int order) {
        SQLiteDatabase DB = myHelper.getWritableDatabase();
        ContentValues cvScene = new ContentValues();
        cvScene.put(myHelper.SCENE_NAME, name);
        cvScene.put(myHelper.SCENE_COLOUR, colour);
        cvScene.put(myHelper.SCENE_PLAY_ID_FK, playID);
        cvScene.put(myHelper.SCENE_ORDER, order);
        long id = DB.insert(myHelper.SCENE_TABLE_NAME, null, cvScene);
        //Adding Characers into the Scene_character Join Table - No way to varify done correctly in app
        ContentValues cvSceneCharacter = new ContentValues();
        for(int i: characterIDList) {
            cvSceneCharacter.put(myHelper.CHARACTER_ID_FK, i);
            cvSceneCharacter.put(myHelper.SCENE_ID_FK, getSceneIDByOrder(playID, order));
            long idSC = DB.insert(myHelper.SCENE_CHARACTER_TABLE_NAME, null, cvSceneCharacter);
        }
        return id;
    }

    public ArrayList<Scene> getAllScenes() {
        ArrayList<Scene> sceneList = new ArrayList<>();
        SQLiteDatabase DB = myHelper.getReadableDatabase();
        String[] columns = {myHelper.SCENE_ID, myHelper.SCENE_NAME, myHelper.SCENE_COLOUR, myHelper.SCENE_PLAY_ID_FK,  myHelper.SCENE_ORDER};
        Cursor cursor = DB.query(myHelper.SCENE_TABLE_NAME, columns, null, null, null, null, myHelper.SCENE_ORDER + " ASC");
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(myHelper.SCENE_ID));
            String name = cursor.getString(cursor.getColumnIndex(myHelper.SCENE_NAME));
            String colour = cursor.getString(cursor.getColumnIndex(myHelper.SCENE_COLOUR));
            int playID = cursor.getInt(cursor.getColumnIndex(myHelper.SCENE_PLAY_ID_FK));
            int order = cursor.getInt(cursor.getColumnIndex(myHelper.SCENE_ORDER));
            Scene scene = new Scene(id, name, colour, getPlayByID(playID), order);
            sceneList.add(scene);
        }
        return sceneList;
    }

    public ArrayList<Scene> getAllScenesInPlay(int playid) {
        ArrayList<Scene> sceneList = new ArrayList<>();
        SQLiteDatabase DB = myHelper.getReadableDatabase();
        String[] columns = {myHelper.SCENE_ID, myHelper.SCENE_NAME, myHelper.SCENE_COLOUR,  myHelper.SCENE_ORDER};
        Cursor cursor = DB.query(myHelper.SCENE_TABLE_NAME, columns, "" + myHelper.SCENE_PLAY_ID_FK + " =?", new String[] {"" + playid}, null, null, myHelper.SCENE_ORDER + " ASC");
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(myHelper.SCENE_ID));
            String name = cursor.getString(cursor.getColumnIndex(myHelper.SCENE_NAME));
            String colour = cursor.getString(cursor.getColumnIndex(myHelper.SCENE_COLOUR));
            int order = cursor.getInt(cursor.getColumnIndex(myHelper.SCENE_ORDER));
            Scene scene = new Scene(id, name, colour, getPlayByID(playid), order);
            sceneList.add(scene);
        }
        return sceneList;
    }

    public void deleteScene(Scene scene) {
        SQLiteDatabase db = myHelper.getWritableDatabase();
        ArrayList<Line> lineList = getAllLinesInScene(scene.getUID());
        for(Line line: lineList) {
            deleteLine(line);
        }
        deleteSceneCharacterRowsByScene(scene);
        String[] UID = {"" + scene.getUID()};
        db.delete(myHelper.SCENE_TABLE_NAME, myHelper.SCENE_ID + " = ?", UID);
        moveSceneOrderUpOne(scene.getPlay().getUID(), scene.getOrder());
    }

    public void updateSceneOnly(Scene scene) {
        SQLiteDatabase DB = myHelper.getWritableDatabase();
        String[] UID = {"" + scene.getUID()};
        ContentValues cv = new ContentValues();
        cv.put(myHelper.SCENE_NAME, scene.getName());
        cv.put(myHelper.SCENE_COLOUR, scene.getColour());
        cv.put(myHelper.SCENE_ORDER, scene.getOrder());
        DB.update(myHelper.SCENE_TABLE_NAME, cv, myHelper.SCENE_ID + " =?", UID);

    }

    public void updateSceneAndCharacterList(Scene scene, ArrayList<Integer> characterIDList) {
        SQLiteDatabase DB = myHelper.getWritableDatabase();
        String[] UID = {"" + scene.getUID()};
        ContentValues cv = new ContentValues();
        cv.put(myHelper.SCENE_NAME, scene.getName());
        cv.put(myHelper.SCENE_COLOUR, scene.getColour());
        cv.put(myHelper.SCENE_ORDER, scene.getOrder());
        DB.update(myHelper.SCENE_TABLE_NAME, cv, myHelper.SCENE_ID + " =?", UID);
        //Insert Scene_Character Join Table as well
        deleteSceneCharacterRowsByScene(scene);
        ContentValues cvSceneCharacter = new ContentValues();
        for(int i: characterIDList) {
            cvSceneCharacter.put(myHelper.CHARACTER_ID_FK, i);
            cvSceneCharacter.put(myHelper.SCENE_ID_FK, scene.getUID());
            long idSC = DB.insert(myHelper.SCENE_CHARACTER_TABLE_NAME, null, cvSceneCharacter);
        }
    }

    public Scene getSceneByIndex(int playID, int index) {
        ArrayList<Scene> sceneList = getAllScenesInPlay(playID);
        if(index < sceneList.size()) { return sceneList.get(index); }
        return null;
    }

    public Scene getSceneByID(int id) {
        ArrayList<Scene> sceneList = getAllScenes();
        for(Scene scene: sceneList) {
            if(scene.getUID() == id) { return scene; }
        }
        return null;
    }


    public Scene getSceneByIDandPlayID(int playID, int id) {
        ArrayList<Scene> sceneList = getAllScenesInPlay(playID);
        for(Scene scene: sceneList) {
            if(scene.getUID() == id) { return scene; }
        }
        return null;
    }

    public int getNumberOfScenesInPlay(int playID) { //only useful when adding scene at the end
        ArrayList<Scene> sceneList = getAllScenesInPlay(playID);
        return sceneList.size();
    }

    //if deleting scene, have to make sure order updates its self for other scenes,
    public void moveSceneOrderUpOne(int playID, int orderNum) {
        ArrayList<Scene> sceneList = getAllScenesInPlay(playID);
        for(Scene scene: sceneList) {
            if(scene.getOrder() >= orderNum) {
                int newOrder = scene.getOrder() - 1;
                scene.setOrder(newOrder);
                updateSceneOnly(scene);
            }
        }
    }

    public int getSceneIDByOrder(int playID, int order) {
        SQLiteDatabase DB = myHelper.getReadableDatabase();
        String[] columns = {myHelper.SCENE_ID};
        Cursor cursor = DB.query(myHelper.SCENE_TABLE_NAME, columns, "" + myHelper.SCENE_PLAY_ID_FK + " =? AND " + myHelper.SCENE_ORDER + " =?", new String[] {"" + playID, "" + order}, null, null, null);
        if(cursor.getCount() == 1) {
            cursor.moveToFirst();
            int id = cursor.getInt(cursor.getColumnIndex(myHelper.SCENE_ID));
            return id;
        }
        return 0;
    }

    public void changeSceneOrder(int sceneID, int order) {
        SQLiteDatabase DB = myHelper.getWritableDatabase();
        String[] UID = {"" + sceneID};
        ContentValues cv = new ContentValues();
        cv.put(myHelper.SCENE_ORDER, order);
        DB.update(myHelper.SCENE_TABLE_NAME, cv, myHelper.SCENE_ID + " =?", UID);
    }

    /*****************
     *      Lines
     *****************/

    public long insertLine(int characterID, String dialog, int sceneID, int order) {
        SQLiteDatabase DB = myHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(myHelper.LINE_CHARACTER_ID_FK, characterID);
        cv.put(myHelper.LINE_DIALOG, dialog);
        cv.put(myHelper.LINE_SCENE_ID_FK, sceneID);
        cv.put(myHelper.LINE_ORDER, order);
        cv.put(myHelper.LINE_SCORE, -1);
        long id = DB.insert(myHelper.LINE_TABLE_NAME,null, cv);
        return id;
    }



    public ArrayList<Line> getAllLines() {
        ArrayList<Line> lineList = new ArrayList<>();
        SQLiteDatabase DB = myHelper.getReadableDatabase();
        String[] columns = {myHelper.LINE_ID, myHelper.LINE_CHARACTER_ID_FK, myHelper.LINE_DIALOG, myHelper.LINE_SCENE_ID_FK, myHelper.LINE_ORDER, myHelper.LINE_SCORE};
        Cursor cursor = DB.query(myHelper.LINE_TABLE_NAME, columns, null, null, null, null, myHelper.LINE_ORDER + " ASC");
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(myHelper.LINE_ID));
            int characterID = cursor.getInt(cursor.getColumnIndex(myHelper.LINE_CHARACTER_ID_FK));
            String dialog = cursor.getString(cursor.getColumnIndex(myHelper.LINE_DIALOG));
            int sceneID = cursor.getInt(cursor.getColumnIndex(myHelper.LINE_SCENE_ID_FK));
            int order = cursor.getInt(cursor.getColumnIndex(myHelper.LINE_ORDER));
            int score = cursor.getInt(cursor.getColumnIndex(myHelper.LINE_SCORE));
            Line line = new Line(id, getCharacterByID(characterID), dialog, getSceneByID(sceneID), order, score);
            lineList.add(line);
        }
        return lineList;
    }

    public ArrayList<Line> getAllLinesInScene(int sceneID) {
        ArrayList<Line> lineList = new ArrayList<>();
        SQLiteDatabase DB = myHelper.getReadableDatabase();
        String[] columns = {myHelper.LINE_ID, myHelper.LINE_CHARACTER_ID_FK, myHelper.LINE_DIALOG, myHelper.LINE_SCENE_ID_FK, myHelper.LINE_ORDER, myHelper.LINE_SCORE};
        Cursor cursor = DB.query(myHelper.LINE_TABLE_NAME, columns, "" + myHelper.LINE_SCENE_ID_FK + " =?", new String[] {"" + sceneID}, null, null, myHelper.LINE_ORDER + " ASC");
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(myHelper.LINE_ID));
            int characterID = cursor.getInt(cursor.getColumnIndex(myHelper.LINE_CHARACTER_ID_FK));
            String dialog = cursor.getString(cursor.getColumnIndex(myHelper.LINE_DIALOG));
            int order = cursor.getInt(cursor.getColumnIndex(myHelper.LINE_ORDER));
            int score = cursor.getInt(cursor.getColumnIndex(myHelper.LINE_SCORE));
            Line line = new Line(id, getCharacterByID(characterID), dialog, getSceneByID(sceneID), order, score);
            lineList.add(line);
        }
        return lineList;
    }

    public void deleteLine(Line line) {
        SQLiteDatabase db = myHelper.getWritableDatabase();
        String[] UID = {"" + line.getUID()};
        db.delete(myHelper.LINE_TABLE_NAME, myHelper.LINE_ID + " = ?", UID);
        moveLineOrderUpOne(line.getScene().getUID(), line.getOrderNumber());
    }

    public void updateLine(Line line) {
        SQLiteDatabase DB = myHelper.getWritableDatabase();
        String[] UID = {"" + line.getUID()};
        ContentValues cv = new ContentValues();
        cv.put(myHelper.LINE_CHARACTER_ID_FK, line.getCharacter().getUID());
        cv.put(myHelper.LINE_DIALOG, line.getDialog());
        cv.put(myHelper.LINE_SCORE, line.getScore());
        cv.put(myHelper.LINE_ORDER, line.getOrderNumber());
        DB.update(myHelper.LINE_TABLE_NAME, cv, myHelper.LINE_ID + " =?", UID);

    }

    public int getNumberOfLineByCharacter(Character character) {
        ArrayList<Line> lineList = getAllLines();
        int count = 0;
        for(Line line: lineList) {
            if(line.getCharacter().getUID() == character.getUID()) { count++; }
        }
        return count;
    }

    public Line getLineByID(int lineID) {
        ArrayList<Line> lineList = getAllLines();
        for(Line line: lineList) {
            if(line.getUID() == lineID) { return line; }
        }
        return null;
    }

    public Line getLineByOrderInScene(int sceneID, int order) {
        ArrayList<Line> lineList = getAllLines();
        for(Line line: lineList) {
            if(line.getScene().getUID() == sceneID && line.getOrderNumber() == order) {
                return line;
            }
        }
        return null;
    }

    public int getNumberOfLinesInScene(int sceneID) {
        ArrayList<Line> lineList = getAllLinesInScene(sceneID);
        return lineList.size();
    }

    public void moveLineOrderUpOne(int sceneID, int orderNum) {
        ArrayList<Line> lineList = getAllLinesInScene(sceneID);
        for(Line line: lineList) {
            if(line.getOrderNumber() >= orderNum) {
                int newOrder = line.getOrderNumber() - 1;
                line.setOrderNumber(newOrder);
                updateLine(line);
            }
        }
    }

    public void moveLinesDown(int sceneID, int minOrder, int num) {
        ArrayList<Line> lineList = getAllLinesInScene(sceneID);
        for(Line line: lineList) {
            if(line.getOrderNumber() >= minOrder) {
                int newOrder = line.getOrderNumber() + num;
                line.setOrderNumber(newOrder);
                updateLine(line);
            }
        }
    }

    /*****************
     * Scene_Character
     *****************/

    public ArrayList<Character> getCharactersBySceneID(int sceneID) {
        ArrayList<Character> characterList = new ArrayList<>();
        SQLiteDatabase DB = myHelper.getReadableDatabase();
        String[] columns = {myHelper.SCENE_ID_FK, myHelper.CHARACTER_ID_FK};
        Cursor cursor = DB.query(myHelper.SCENE_CHARACTER_TABLE_NAME, columns, "" + myHelper.SCENE_ID_FK + " =?", new String[] {"" + sceneID}, null, null, null);
        while(cursor.moveToNext()) {
            int characterID = cursor.getInt(cursor.getColumnIndex(myHelper.CHARACTER_ID_FK));
            Character character = getCharacterByID(characterID);
            characterList.add(character);
        }
        return characterList;
    }

    public void deleteSceneCharacterRowsByScene(Scene scene) {
        SQLiteDatabase db = myHelper.getWritableDatabase();
        String[] UID = {"" + scene.getUID()};
        db.delete(myHelper.SCENE_CHARACTER_TABLE_NAME, myHelper.SCENE_ID_FK + " = ?", UID);
    }

    public void deleteSceneCharacterRowsByCharacter(Character character) {
        SQLiteDatabase db = myHelper.getWritableDatabase();
        String[] UID = {"" + character.getUID()};
        db.delete(myHelper.SCENE_CHARACTER_TABLE_NAME, myHelper.CHARACTER_ID_FK + " = ?", UID);
    }

    /**********************************************************************************************************************
     *                                   D A T A B A S E   H E L P E R
     **********************************************************************************************************************/

    public class DBHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "SripturDB";
        private static final int DATABASE_VERSION = 1;

        private static final String PLAY_TABLE_NAME = "play";
        private static final String PLAY_ID = "play_id";
        private static final String PLAY_TITLE = "title";
        private static final String PLAY_COLOUR = "colour";
        private static final String CREATE_TABLE_PLAY = "CREATE TABLE " + PLAY_TABLE_NAME + " (" +
                                                        PLAY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                        PLAY_TITLE + " VARCHAR(255), " +
                                                        PLAY_COLOUR + " VARCHAR(255));";
        private static final String DROP_PLAY_TABLE = "DROP TABLE IF EXISTS " + PLAY_TABLE_NAME;


        private static final String SCENE_TABLE_NAME = "scene";
        private static final String SCENE_ID = "scene_id";
        private static final String SCENE_NAME = "name";
        private static final String SCENE_ORDER = "scene_order";
        private static final String SCENE_COLOUR = "colour";
        private static final String SCENE_PLAY_ID_FK = "play_id";
        private static final String CREATE_SCENE_TABLE = "CREATE TABLE " + SCENE_TABLE_NAME + " (" +
                                                            SCENE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                            SCENE_NAME + " VARCHAR(255), " +
                                                            SCENE_ORDER + " INTEGER, " +
                                                            SCENE_COLOUR + " VARCHAR(255), " +
                                                            SCENE_PLAY_ID_FK + " INTEGER, " +
                                                            "FOREIGN KEY(" + SCENE_PLAY_ID_FK + ") REFERENCES " + PLAY_TABLE_NAME + "(" + PLAY_ID + "));";
        private static final String DROP_SCENE_TABLE = "DROP TABLE IF EXISTS " + SCENE_TABLE_NAME;



        private static final String CHARACTER_TABLE_NAME = "character";
        private static final String CHARACTER_ID = "character_id";
        private static final String CHARACTER_NAME = "name";
        private static final String CHARACTER_AVATAR_CODE = "avatar_code";
        private static final String CHARACTER_USER_PART = "user_part";
        private static final String CHARACTER_COLOUR = "colour";
        private static final String CHARACTER_PLAY_ID_FK = "play_id";
        private static final String CREATE_CHARACTER_TABLE = "CREATE TABLE " + CHARACTER_TABLE_NAME + " (" +
                                                            CHARACTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                            CHARACTER_NAME + " VARCHAR(255), " +
                                                            CHARACTER_AVATAR_CODE + " VARCHAR(255), " +
                                                            CHARACTER_USER_PART + " INTEGER," +
                                                            CHARACTER_COLOUR + " VARCHAR(255), " +
                                                            CHARACTER_PLAY_ID_FK + " INTEGER, " +
                                                            "FOREIGN KEY(" + CHARACTER_PLAY_ID_FK + ") REFERENCES " + PLAY_TABLE_NAME + "(" + PLAY_ID + "));";
        private static final String DROP_CHARACTER_TABLE = "DROP TABLE IF EXISTS " + CHARACTER_TABLE_NAME;


        private static final String LINE_TABLE_NAME = "line";
        private static final String LINE_ID = "line_id";
        private static final String LINE_CHARACTER_ID_FK = "character_id";
        private static final String LINE_SCENE_ID_FK = "scene_id";
        private static final String LINE_DIALOG = "dialog";
        private static final String LINE_ORDER = "line_order";
        private static final String LINE_SCORE = "line_score";
        private static final String CREATE_LINE_TABLE = "CREATE TABLE " + LINE_TABLE_NAME + " (" +
                                                            LINE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                            LINE_CHARACTER_ID_FK + " INTEGER, " +
                                                            LINE_DIALOG + " TEXT, " +
                                                            LINE_SCENE_ID_FK + " INTEGER, " +
                                                            LINE_ORDER + " INTEGER, " +
                                                            LINE_SCORE + " INTEGER, " +
                                                            "FOREIGN KEY(" + LINE_CHARACTER_ID_FK + ") REFERENCES " + CHARACTER_TABLE_NAME + "(" + CHARACTER_ID + "), " +
                                                            "FOREIGN KEY(" + LINE_SCENE_ID_FK + ") REFERENCES " + SCENE_TABLE_NAME + "(" + SCENE_ID + "));";
        private static final String DROP_LINE_TABLE = "DROP TABLE IF EXISTS " + LINE_TABLE_NAME;

        private static final String SCENE_CHARACTER_TABLE_NAME = "scene_character";
        private static final String SCENE_CHARACTER_ID = "scene_character_id";
        private static final String SCENE_ID_FK = "scene_id";
        private static final String CHARACTER_ID_FK = "character_id";
        private static final String CREATE_SCENE_CHARACTER_TABLE = "CREATE TABLE " + SCENE_CHARACTER_TABLE_NAME + " (" +
                                                                    SCENE_CHARACTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                    SCENE_ID_FK + " INTEGER, " +
                                                                    CHARACTER_ID_FK + " INTEGER, " +
                                                                    "FOREIGN KEY(" + SCENE_ID_FK + ") REFERENCES " + SCENE_TABLE_NAME + "(" + SCENE_ID + "), " +
                                                                    "FOREIGN KEY(" + CHARACTER_ID_FK + ") REFERENCES " + CHARACTER_TABLE_NAME + "(" + CHARACTER_ID + "));";
        private static final String DROP_CHARACTER_SCENE_TABLE = "DROP TABLE IF EXISTS " + SCENE_CHARACTER_TABLE_NAME;

        private Context context;

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_PLAY);
            db.execSQL(CREATE_CHARACTER_TABLE);
            db.execSQL(CREATE_SCENE_TABLE);
            db.execSQL(CREATE_LINE_TABLE);
            db.execSQL(CREATE_SCENE_CHARACTER_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Toast.makeText(context, "Upgrade", Toast.LENGTH_SHORT).show();
            db.execSQL(DROP_LINE_TABLE);
            db.execSQL(DROP_SCENE_TABLE);
            db.execSQL(DROP_CHARACTER_TABLE);
            db.execSQL(DROP_PLAY_TABLE);
            db.execSQL(DROP_CHARACTER_SCENE_TABLE);
            onCreate(db);
        }
    }
}
