package com.example.scriptur.Database;

public class Character {

    private String name, avatarCode, colour;
    private int UID;
    private boolean userPart;
    private Play play;

    public Character() { }

    public Character(int id, String name, String avatarCode, boolean userPart, String colour, Play play) {
        this.UID = id;
        this.name = name;
        this.userPart = userPart;
        this.avatarCode = avatarCode;
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

    public String getAvatarCode() {
        return avatarCode;
    }

    public void setAvatarCode(String gender) {
        this.avatarCode = gender;
    }

    public void setFemale() {
        this.avatarCode = "female";
    }

    public void setMale() {
        this.avatarCode = "male";
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
