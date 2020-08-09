package com.example.scriptur.Database;

public class Character {

    private String name;
    private int UID;
    private Play play;
    private String gender;
    private boolean userPart;

    public Character() { }

    public Character(int id, String name, String gender, boolean userPart, Play play) {
        this.UID = id;
        this.name = name;
        this.play = play;
        this.userPart = userPart;
        this.gender = gender;
        this.userPart = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Play getPlay() {
        return play;
    }

    public void setPlay(Play play) {
        this.play = play;
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

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }
}
