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

    public long insertPlay(String title) {
        SQLiteDatabase DB = myHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(myHelper.PLAY_TITLE, title);
        long id = DB.insert(myHelper.PLAY_TABLE_NAME, null, cv);
        return id;
    }

    public ArrayList<Play> getAllPlays() {
        ArrayList<Play> playList = new ArrayList<>();
        SQLiteDatabase DB = myHelper.getReadableDatabase();
        String[] columns = {myHelper.PLAY_ID, myHelper.PLAY_TITLE};
        Cursor cursor = DB.query(myHelper.PLAY_TABLE_NAME, columns, null, null, null, null, myHelper.PLAY_TITLE + " ASC");
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(myHelper.PLAY_ID));
            String title = cursor.getString(cursor.getColumnIndex(myHelper.PLAY_TITLE));
            Play play = new Play(id, title);
            playList.add(play);
        }
        return playList;
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

    public long insertCharacter(String name, String gender, Boolean userRole, int playID) {
        SQLiteDatabase DB = myHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(myHelper.CHARACTER_NAME, name);
        cv.put(myHelper.CHARACTER_GENDER, gender);
        if(!userRole) { cv.put(myHelper.CHARACTER_USER_PART, 0); }
        else { cv.put(myHelper.CHARACTER_USER_PART, 1);}
        cv.put(myHelper.CHARACTER_PLAY_ID_FK, playID);
        long id = DB.insert(myHelper.CHARACTER_TABLE_NAME, null, cv);
        return id;
    }

    public ArrayList<Character> getAllCharacters() {
        ArrayList<Character> characterList = new ArrayList<>();
        SQLiteDatabase DB = myHelper.getReadableDatabase();
        String[] columns = {myHelper.CHARACTER_ID, myHelper.CHARACTER_NAME, myHelper.CHARACTER_GENDER, myHelper.CHARACTER_USER_PART, myHelper.CHARACTER_PLAY_ID_FK};
        Cursor cursor = DB.query(myHelper.CHARACTER_TABLE_NAME, columns, null, null, null, null, myHelper.CHARACTER_NAME + " ASC");
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(myHelper.CHARACTER_ID));
            String name = cursor.getString(cursor.getColumnIndex(myHelper.CHARACTER_NAME));
            String gender = cursor.getString(cursor.getColumnIndex(myHelper.CHARACTER_GENDER));
            int userRoleInt = cursor.getInt(cursor.getColumnIndex(myHelper.CHARACTER_USER_PART));
            Boolean userRole = false; if(userRoleInt == 1) { userRole = true; }
            Play play = getPlayByID(cursor.getInt(cursor.getColumnIndex(myHelper.CHARACTER_PLAY_ID_FK)));
            Character character = new Character(id, name, gender, userRole, play);
            characterList.add(character);
        }
        return characterList;
    }

    public ArrayList<Character> getAllCharactersInPlay(int playID) {
        ArrayList<Character> characterList = new ArrayList<>();
        SQLiteDatabase DB = myHelper.getReadableDatabase();
        Play play = getPlayByID(playID);//Why needed if just going to filter by play id anyway?
        String[] columns = {myHelper.CHARACTER_ID, myHelper.CHARACTER_NAME, myHelper.CHARACTER_GENDER, myHelper.CHARACTER_USER_PART};
        Cursor cursor = DB.query(myHelper.CHARACTER_TABLE_NAME, columns, "" + myHelper.CHARACTER_PLAY_ID_FK + " = ?", new String[] { "" + play.getUID()}, null, null, myHelper.CHARACTER_NAME + " ASC");
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(myHelper.CHARACTER_ID));
            String name = cursor.getString(cursor.getColumnIndex(myHelper.CHARACTER_NAME));
            String gender = cursor.getString(cursor.getColumnIndex(myHelper.CHARACTER_GENDER));
            int userRoleInt = cursor.getInt(cursor.getColumnIndex(myHelper.CHARACTER_USER_PART));
            Boolean userRole = false; if(userRoleInt == 1) { userRole = true; }
            Character character = new Character(id, name, gender, userRole, play);
            characterList.add(character);
        }
        return characterList;
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

    public long insertScene(String name, ArrayList<Integer> characterIDList, int playID, int order) {
        SQLiteDatabase DB = myHelper.getWritableDatabase();
        ContentValues cvScene = new ContentValues();
        cvScene.put(myHelper.SCENE_NAME, name);
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

    public ArrayList<Scene> getAllScenesInPlay(int playid) {
        ArrayList<Scene> sceneList = new ArrayList<>();
        SQLiteDatabase DB = myHelper.getReadableDatabase();
        String[] columns = {myHelper.SCENE_ID, myHelper.SCENE_NAME,  myHelper.SCENE_ORDER};
        Cursor cursor = DB.query(myHelper.SCENE_TABLE_NAME, columns, "" + myHelper.SCENE_PLAY_ID_FK + " =?", new String[] {"" + playid}, null, null, myHelper.SCENE_ORDER + " ASC");
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(myHelper.SCENE_ID));
            String name = cursor.getString(cursor.getColumnIndex(myHelper.SCENE_NAME));
            int order = cursor.getInt(cursor.getColumnIndex(myHelper.SCENE_ORDER));
            Scene scene = new Scene(id, name, getPlayByID(playid), order);
            sceneList.add(scene);
        }
        return sceneList;
    }

    public Scene getSceneByIndex(int playID, int index) {
        ArrayList<Scene> sceneList = getAllScenesInPlay(playID);
        if(index < sceneList.size()) { return sceneList.get(index); }
        return null;
    }

    public Scene getSceneByID(int playID, int id) {
        ArrayList<Scene> sceneList = getAllScenesInPlay(playID);
        for(Scene scene: sceneList) {
            if(scene.getUID() == id) { return scene; }
        }
            return null;
    }

    public int getNumberOfScenesInPlay(int playID) {
        ArrayList<Scene> sceneList = getAllScenesInPlay(playID);
        return sceneList.size();
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

    /**********************************************************************************************************************
     *                                   D A T A B A S E   H E L P E R
     **********************************************************************************************************************/

    public class DBHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "SripturDB";
        private static final int DATABASE_VERSION = 1;

        private static final String PLAY_TABLE_NAME = "play";
        private static final String PLAY_ID = "play_id";
        private static final String PLAY_TITLE = "title";
//        private static final String PLAY_SCENES = "scenes";
//        private static final String PLAY_CHARACTERS = "characters";
        private static final String CREATE_TABLE_PLAY = "CREATE TABLE " + PLAY_TABLE_NAME + " (" +
                                                        PLAY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                        PLAY_TITLE + " VARCHAR(255));";
        private static final String DROP_PLAY_TABLE = "DROP TABLE IF EXISTS " + PLAY_TABLE_NAME;


        private static final String SCENE_TABLE_NAME = "scene";
        private static final String SCENE_ID = "scene_id";
        private static final String SCENE_NAME = "name";
        private static final String SCENE_ORDER = "scene_order";
        private static final String SCENE_PLAY_ID_FK = "play_id";
        private static final String CREATE_SCENE_TABLE = "CREATE TABLE " + SCENE_TABLE_NAME + " (" +
                                                            SCENE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                            SCENE_NAME + " VARCHAR(255), " +
                                                            SCENE_ORDER + " INTEGER, " +
                                                            SCENE_PLAY_ID_FK + " INTEGER, " +
                                                            "FOREIGN KEY(" + SCENE_PLAY_ID_FK + ") REFERENCES " + PLAY_TABLE_NAME + "(" + PLAY_ID + "));";
        private static final String DROP_SCENE_TABLE = "DROP TABLE IF EXISTS " + SCENE_TABLE_NAME;



        private static final String CHARACTER_TABLE_NAME = "character";
        private static final String CHARACTER_ID = "character_id";
        private static final String CHARACTER_NAME = "name";
        private static final String CHARACTER_USER_PART = "user_part";
        private static final String CHARACTER_GENDER = "gender";
        private static final String CHARACTER_PLAY_ID_FK = "play_id";
        private static final String CREATE_CHARACTER_TABLE = "CREATE TABLE " + CHARACTER_TABLE_NAME + " (" +
                                                            CHARACTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                            CHARACTER_NAME + " VARCHAR(255), " +
                                                            CHARACTER_GENDER + " VARCHAR(255), " +
                                                            CHARACTER_USER_PART + " INTEGER," +
                                                            CHARACTER_PLAY_ID_FK + " INTEGER, " +
                                                            "FOREIGN KEY(" + CHARACTER_PLAY_ID_FK + ") REFERENCES " + PLAY_TABLE_NAME + "(" + PLAY_ID + "));";
        private static final String DROP_CHARACTER_TABLE = "DROP TABLE IF EXISTS " + CHARACTER_TABLE_NAME;


        private static final String LINE_TABLE_NAME = "line";
        private static final String LINE_ID = "line_id";
        private static final String LINE_CHARACTER = "character";//??
        private static final String LINE_DIALOG = "dialog";
        private static final String LINE_ORDER = "line_order";
        private static final String CREATE_LINE_TABLE = "CREATE TABLE " + LINE_TABLE_NAME + " (" +
                                                            LINE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                            LINE_CHARACTER + " VARCHAR(255), " +
                                                            LINE_ORDER + " INTEGER, " +
                                                            LINE_DIALOG + " TEXT);";
        private static final String DROP_LINE_TABLE = "DROP TABLE IF EXISTS " + LINE_TABLE_NAME;

        private static final String SCENE_CHARACTER_TABLE_NAME = "scene_character";
        private static final String SCENE_CHARACTER_ID = "scene_character_id";
        private static final String SCENE_ID_FK = "scene_id";
        private static final String CHARACTER_ID_FK = "character_id";
        private static final String CREATE_SCENE_CHARACTER_TABLE = "CREATE TABLE " + SCENE_CHARACTER_TABLE_NAME + " (" +
                                                                    SCENE_CHARACTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                    SCENE_ID_FK + " INTEGER, " +
                                                                    CHARACTER_ID_FK + " INTEGER, " +
                                                                    "FOREIGN KEY(" + SCENE_ID_FK + ") REFERENCES " + SCENE_TABLE_NAME + "(" + SCENE_ID + ")," +
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
