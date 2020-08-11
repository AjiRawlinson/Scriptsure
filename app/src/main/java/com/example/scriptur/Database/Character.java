package com.example.scriptur.Database;

public class Character {

    private String name, gender, colour;
    private int UID;
    private boolean userPart;
    private Play play;

    public Character() { }

    public Character(int id, String name, String gender, boolean userPart, String colour, Play play) {
        this.UID = id;
        this.name = name;
        this.userPart = userPart;
        this.gender = gender;
        this.colour = colour;
        this.play = play;
    }


    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setFemale() {
        this.gender = "female";
    }

    public void setMale() {
        this.gender = "male";
    }

    public boolean isUserPart() {
        return userPart;
    }

    public void setUserPart(boolean userPart) {
        this.userPart = userPart;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public Play getPlay() {
        return play;
    }

    public void setPlay(Play play) {
        this.play = play;
    }
}
