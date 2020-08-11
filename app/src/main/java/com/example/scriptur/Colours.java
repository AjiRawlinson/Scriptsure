package com.example.scriptur;

import java.util.ArrayList;
import java.util.Random;

public class Colours {

    ArrayList<String> colourList = new ArrayList<>();
    int presses;

    public Colours() {
        presses = 0;
        colourList.add("#ABDEE6"); colourList.add("#CBAACB"); colourList.add("#FFFFB5"); colourList.add("#FFCCB6"); colourList.add("#F3B0C3");
        colourList.add("#C6DBDA"); colourList.add("#FEE1E8"); colourList.add("#FED7C3"); colourList.add("#F6EAC2"); colourList.add("#ECD5E3");
        colourList.add("#FF968A"); colourList.add("#FFAEA5"); colourList.add("#FFC5BF"); colourList.add("#FFD8BE"); colourList.add("#FFC8A2");
        colourList.add("#D4F0F0"); colourList.add("#8FCACA"); colourList.add("#CCE2CB"); colourList.add("#B6CFB6"); colourList.add("#97C1A9");
        colourList.add("#FCB9AA"); colourList.add("#FFDBCC"); colourList.add("#ECEAE4"); colourList.add("#A2E1DB"); colourList.add("#55CBCD");
    }

    public String randomColour() {
        Random random = new Random();
        int randomIndex = random.nextInt(colourList.size());
        return colourList.get(randomIndex);
    }
}
