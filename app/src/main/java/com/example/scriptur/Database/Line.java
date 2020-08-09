package com.example.scriptur.Database;

public class Line {

    private String dialog;
    private Scene scene;
    private Character character;
    private int orderNumber;
    private int UID;

    public Line() { }

    public Line(Character character, String dialog, Scene scene) {
        this.character = character;
        this.scene = scene;
        this.dialog = dialog;
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

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }
}
