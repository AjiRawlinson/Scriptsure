package com.example.scriptur.Database;

import java.util.ArrayList;

public class Play {

    private String title;
    private int UID;
//    ArrayList<Scene> scenes;
//    ArrayList<Character> characters;

    public Play() {
    }

    public Play(String title) {
        this.title = title;
    }

    public Play(int id, String title) {
        this.UID = id;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

}
