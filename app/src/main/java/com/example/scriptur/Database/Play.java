package com.example.scriptur.Database;

import java.util.ArrayList;

public class Play {

    private String title, colour;
    private int UID;
//    ArrayList<Scene> scenes;
//    ArrayList<Character> characters;

    public Play() {
    }

    public Play(String title, String colour) {
        this.title = title;
        this.colour = colour;
    }

    public Play(int id, String title, String colour) {
        this.UID = id;
        this.title = title;
        this.colour = colour;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

}
