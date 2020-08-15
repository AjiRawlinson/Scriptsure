package com.example.scriptur.Database;

public class Line {

    private String dialog;
    private Scene scene;
    private Character character;
    private int orderNumber;
    private int score;
    private int UID;

    public Line() { }

    public Line(int id, Character character, String dialog, Scene scene, int order, int score) {
        this.UID = id;
        this.character = character;
        this.dialog = dialog;
        this.scene = scene;
        this.orderNumber = order;
        this.score = score;
    }

    public String getDialog() {
        return dialog;
    }

    public void setDialog(String dialog) {
        this.dialog = dialog;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }
}
