package com.example.scriptur.Database;

import com.example.scriptur.Database.Play;

public class Scene {

    private int UID;
    private String name;
    private Play play;
    private int order;
//    private ArrayList<Line> lines;
//    private ArrayList<Character> characters;

    public Scene() {

    }

    public Scene (int id, String name, Play play, int order) {
        this.UID = id;
        this.name = name;
        this.play = play;
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    public Play getPlay() {
        return play;
    }

    public void setPlay(Play play) {
        this.play = play;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
